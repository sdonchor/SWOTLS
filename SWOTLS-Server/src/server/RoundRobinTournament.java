package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoundRobinTournament extends Tournament {
    /* System kołowy – sposób rozgrywania zawodów sportowych w dyscyplinach, w których rywalizacja polega na bezpośrednich
       pojedynkach pomiędzy uczestnikami. W systemie kołowym każdy uczestnik gra kolejno ze wszystkimi przeciwnikami.
       Ten sposób rozgrywek nazywany jest również systemem każdy z każdym, w anglojęzycznej literaturze round-robin.
    */

        public static void endEntriesStage(int tournamentId, ArrayList<TournamentParticipant> competitors) {
        //Numerowanie zawodników pozycjami startowymi 1, 2, ... n (gdzie n to liczba zawodników w turnieju)
        int i = 1;
        for(TournamentParticipant participant : competitors){
            participant.setStartingPosition(i++);
        }

        generateMatches(tournamentId, competitors, 1);
    }

    /**
     * Zakańcza etap zapisów i przechodzi do pierwszego etapu (generuje mecze startowe).
     */
    public static void endEntriesStage(int tournamentId){
        endEntriesStage(tournamentId, getTournamentParticipants(tournamentId));
        setTournamentStage(tournamentId, 1);
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
            endEntriesStage(tournamentId);
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
        }
        createReport("Raport - Etap " + stage, report);

        if(stage==participants.size()-1){ //Jeżeli aktualny etap był ostatnim (liczba_etapów == ilość_uczestników-1)
            setTournamentStage(tournamentId, -1); //oznaczyć etap turnieju jako -1 (zakończony)
            //TODO wysłać komunikat o zwycięzcy
            return false;
        }

        generateMatches(tournamentId, participants, stage+1);
        setTournamentStage(tournamentId, stage+1);
        return true;
    }

    /**
     * Generuje pary meczowe z przekazanej listy zawodników dla podanego etapu.
     * @param tournamentId Id turnieju w bazie, do którego przypisać wygenerowane mecze.
     * @param competitors Lista zawodników wsród których wyłonić pary.
     * @param stageOfMatchesToGenerate Etap dla którego wygenerować pary meczowe.
     * @return True, jeżeli udało się wygenerować mecze; False, jeżeli sezon w tej klasie już się zakończył.
     */
    public static boolean generateMatches(int tournamentId, ArrayList<TournamentParticipant> competitors, int stageOfMatchesToGenerate){
        if(stageOfMatchesToGenerate>competitors.size()-1)
            return false;

        //Wygenerowanie par meczowych za pomocą koła Berga
        List<MatchPair> pairs = BergsCircle.getPairs(competitors, stageOfMatchesToGenerate); //etapy rozgrywek są numerowane od 1 (etap 0 to zapisy, -1 to turniej zakończony)
        createMatches(tournamentId, pairs);
        return true;
    }

}
