package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SwissTournament extends Tournament {
    /* W systemie szwajcarskim z góry określa się liczbę rund, które należy rozegrać. Na rundę składają się bezpośrednie
       pojedynki (gry) rozgrywane jednocześnie. Za zwycięstwo w grze uczestnik otrzymuje jeden punkt, za remis pół punktu.
       Dobór par przeciwników w kolejnych rundach zależy od wyników uzyskanych w poprzednich. Pary dobiera się w miarę
       możliwości spośród tych uczestników, którzy dotychczas zdobyli jednakową liczbę punktów.
    */

	private static DatabaseHandler dbH = null;
	
	public static void setDbh(DatabaseHandler dbh) {
		dbH=dbh;
	}
    public static List<MatchPair> drawMatchPairs(List<? extends Identifiable> participants){
        //Losowanie par meczowych (każdy uczestnik ma tylko jeden mecz w danym etapie)
        List<MatchPair> matchPairs = new ArrayList<>();
        for(int i = 0; i<participants.size(); i+=2){
            matchPairs.add(new MatchPair(participants.get(i).getId(), participants.get(i+1).getId()));
        }
        return matchPairs;
    }

    /**
     * Zakańcza etap zapisów i przechodzi do pierwszego etapu (generuje mecze startowe).
     * @param tournamentId Id turnieju, którego zapisy zakończyć
     * @return true jeżeli udało się zakończyć zapisy i przejśc do 1 etapu, false jeżeli się nie udało (liczba zwodników jest nieparzysta)
     */
    public static boolean endEntriesStage(int tournamentId){
        ArrayList<TournamentParticipant> participants = getTournamentParticipants(tournamentId);

        if(participants.size()%2==1) {
            //TODO Jeżeli liczba zawodników jest nieparzysta, to przerywamy i wyślij komunikat
            return false;
        }

        //Wylosowanie par startowych losowo
        List<MatchPair> matchPairs = drawMatchPairs(participants); //wylosowanie par meczowych
        createMatches(tournamentId, matchPairs);

        setTournamentStage(tournamentId, 1);

        return true;
    }

    /**
     * Zapisuje wynik meczu i oznacza go jako zakończony. Na podstawie wprowadzonego wyniku uaktualnia ranking i punktację uczestników.
     * @param matchId Identyfikator meczu w bazie danych.
     */
    public static void saveResult(int matchId, int scoreA, int scoreB){ //prawdopodobnie klasa Match jako parametr będzie albo id meczu żeby go z bazy pobrać
        server.Match match = getMatchById(matchId);
        Competitor competitorA = match.getSideA();
        Competitor competitorB = match.getSideB();

        float didAwin = 1;
        if(scoreA<scoreB){
            didAwin = 0;
            addPoint(match.getCompetitionId(), competitorB.getId()); //Dodanie wygranemu 1 punkta
        }else if(scoreA==scoreB){
            didAwin = 0.5f;
            //Remis - gracze dostają po 0.5 punktu
            addHalfPoint(match.getCompetitionId(), competitorA.getId());
            addHalfPoint(match.getCompetitionId(), competitorB.getId());
        }else{
            didAwin = 1;
            addPoint(match.getCompetitionId(), competitorA.getId()); //Dodanie wygranemu 1 punkta
        }

        updateRating(match.getCompetitionId(), competitorA, competitorB, didAwin); //Aktualizacja rankingu uczestników meczu

        match.setScoreA(scoreA);
        match.setScoreB(scoreB);
        //TODO Zapisać wynik meczu do bazy jeżeli gdzieś wcześniej tego nie zrobiłeś
    }

    /**
     * Zakańcza aktualny etap (generuje raport) i przechodzi do następnego (generuje mecze dla pozostałych w następnym etapie uczestników).
     * @param tournamentId Id turnieju, który ma przejść do następnego etapu
     * @return true jeżeli udało się wygenerować nowe mecze, false jeżeli turniej zakończony i wysyłamy komunikat o zwycięzcy
     */
    public static boolean nextStage(int tournamentId){
        //Uwaga: Jeżeli etap turnieju = 0 to zamiast tej metody wywołać endEntriesStage
        //Uwaga: do następnego etapu można przejść tylko wtedy gdy wszystkie mecze w turnieju zostały zakończone (wprowadzono wyniki)

        int stage = getTournamentStage(tournamentId);
        if(stage==-1) {
            //Jeżeli turniej jest już zakończony (etap -1) to przerwać i
            //TODO wysłać komunikat o zwycięzcy
            return false;
        }else if(stage==0){
            return endEntriesStage(tournamentId);
        }

        ArrayList<TournamentParticipant> participants = getTournamentParticipants(tournamentId);
        //Generacja raportu z listą graczy i ich punktacją po zakończonym etapie (uczestnicy posortowani od największej ilości punktów)
        Collections.sort(participants, Collections.reverseOrder());
        String report = "Punktacja po X rundzie turnieju:\n";
        boolean isSoloType = isSoloType(tournamentId); //sprawdzenie typu turnieju (solo czy drużynowy)
        for(TournamentParticipant p : participants){
            if(isSoloType)
                report += getDisplayedNameOfPlayer(p.getId()) + " - " + p.getPoints();
            else
                report += getNameOfTeam(p.getId()) + " - " + p.getPoints();
            report += "\n";
        }
        createReport(tournamentId,"Raport - Etap " + stage, report);

        if(stage==participants.size()-1){ //Jeżeli aktualny etap był ostatnim (organizator określa liczbę rund)
            setTournamentStage(tournamentId, -1); //oznaczyć etap turnieju jako -1 (zakończony)
            //TODO wysłać komunikat o zwycięzcy
            return false;
        }

        //Dobranie par w taki sposób aby różnica ich punktów była jak najmniejsza (w miarę możliwości rywale powinni mieć jednakowa liczbę punktów)
        List<MatchPair> matchPairs = drawMatchPairs(participants); //lista jest już posortowana wg. punktacji malegąco więc poprostu dobieram z niej kolejno pary
        createMatches(tournamentId, matchPairs);

        setTournamentStage(tournamentId, stage+1);

        return true;
    }


}
