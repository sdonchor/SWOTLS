package server;

import java.util.ArrayList;
import java.util.Collections;

public class LeagueTournament extends Tournament {
	
    /* Wieloklasowa liga - używa systemu kołowego osobno w każdej klasie (szczeblu ligowym); po zakończeniu sezonu
       organizator może zadecydować kogo awansować do wyższej klasy, a kogo zdegradować.
    */

    /**
     * Pobiera listę uczestników (zawodników lub drużyn) posegregowanych do osobnych kolekcji według klas, w których się znajdują.
     * @param tournamentId Id turnieju w bazie danych, z którego pobrać uczestników.
     * @return Lista list zawodników z poszczególnych szczebli ligowych.
     */
	
	private static DatabaseHandler dbH = null;
	
	public static void setDbh(DatabaseHandler dbh) {
		dbH=dbh;
	}
    public static ArrayList< ArrayList<TournamentParticipant> > getParticipantsInClasses(int tournamentId){
        ArrayList<TournamentParticipant> competitors = getTournamentParticipants(tournamentId);

        //Posegregowanie uczestników do osobnych kolekcji według klas, w których się znajdują.
        ArrayList< ArrayList<TournamentParticipant> > classes = new ArrayList<>();
        for(TournamentParticipant participant : competitors){
            int leagueClass = participant.getLeagueClass();
            if(leagueClass>classes.size()){
                classes.ensureCapacity(leagueClass);
                if(classes.get(leagueClass)==null)
                    classes.add(new ArrayList<>());
            }
            classes.get(leagueClass).add(participant);
        }
        return classes;
    }

    /**
     * Zakańcza etap zapisów i przechodzi do pierwszego etapu (generuje mecze startowe).
     */
    public static void endEntriesStage(int tournamentId){
        ArrayList< ArrayList<TournamentParticipant> > classes = getParticipantsInClasses(tournamentId);

        //Wywołanie algorytmu kołowego dla każdej grupy zawodników
        for(ArrayList<TournamentParticipant> competitorsOfXClass : classes){
            RoundRobinTournament.endEntriesStage(tournamentId, competitorsOfXClass);
        }

        setTournamentStage(tournamentId, 1);
    }

    /**
     * Zapisuje wynik meczu i oznacza go jako zakończony. Na podstawie wprowadzonego wyniku uaktualnia ranking i punktację uczestników.
     * @param matchId Identyfikator meczu w bazie danych.
     */
    public static void saveResult(int matchId, int scoreA, int scoreB){ //prawdopodobnie klasa Match jako parametr będzie albo id meczu żeby go z bazy pobrać
        RoundRobinTournament.saveResult(matchId, scoreA, scoreB); //Zapisywanie wyników tak samo jak w systemie kołowym.
    }

    /**
     * Zakańcza aktualny etap (generuje raport) i przechodzi do następnego (generuje mecze dla pozostałych w następnym etapie uczestników).
     * @param tournamentId Id turnieju, który ma przejść do następnego etapu
     * @return true jeżeli udało się wygenerować nowe mecze, false jeżeli turniej zakończony i wysyłamy komunikat o zwycięzcy
     */
    public static boolean nextStage(int tournamentId){
        int stage = getTournamentStage(tournamentId);
        if(stage==-1) {
            return false;
        }else if(stage==0){
            endEntriesStage(tournamentId);
        }

        ArrayList< ArrayList<TournamentParticipant> > classes = getParticipantsInClasses(tournamentId);
        boolean isSoloType = isSoloType(tournamentId); //sprawdzenie typu turnieju (solo czy drużynowy)

        //Generacja raportu z listą graczy i ich punktacją po zakończonym etapie (uczestnicy posegregowani na grupy i posortowani w każdej grupie od największej ilości punktów)
        String report = "Punktacja w poszczególnych klasach rozgrywkowych po X rundzie turnieju:\n\n";
        for(ArrayList<TournamentParticipant> competitorsOfXClass : classes) {
            report += "Klasa " + competitorsOfXClass.get(0).getLeagueClass() + ":\n";
            Collections.sort(competitorsOfXClass, Collections.reverseOrder());

            for (TournamentParticipant p : competitorsOfXClass) {
                if (isSoloType)
                    report += getDisplayedNameOfPlayer(p.getId()) + " - " + p.getPoints();
                else
                    report += getNameOfTeam(p.getId()) + " - " + p.getPoints();
                report += "\n";
            }
            report += "\n";
        }

        int season = getTournamentSeason(tournamentId);
        createReport(tournamentId, "Raport - Etap " + stage + " - Sezon " + season, report); //zapisanie raportu w bazie danych

        boolean areAllClassesFinished = true; //Ma określać czy we wszystkich klasach ligi sezon już się zakończył
        for(ArrayList<TournamentParticipant> competitorsOfXClass : classes){
            if(RoundRobinTournament.generateMatches(tournamentId, competitorsOfXClass, stage+1)) //Wygenerowanie meczy dla każdej klasy za pomocą algorytmu kołowego
                areAllClassesFinished = false;
        }

        if(areAllClassesFinished){
            //Zwiększenie sezonu o 1 i ustawienie etapu na 0 - oznacza to czas pomiędzy sezonami (organizator może ponownie awansować, degradować, usuwać, dodawać zawodników)
            setTournamentStage(tournamentId, 0);
            setTournamentSeason(tournamentId, season+1);

            
            return false;
        }

        setTournamentStage(tournamentId, stage+1);

        return true;
    }


}
