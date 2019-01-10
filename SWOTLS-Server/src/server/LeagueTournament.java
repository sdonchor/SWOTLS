package server;

import java.util.ArrayList;

public class LeagueTournament extends Tournament {
    /* Wieloklasowa liga - używa systemu kołowego osobno w każdej klasie (szczeblu ligowym); po zakończeniu sezonu
       organizator może zadecydować kogo awansować do wyższej klasy, a kogo zdegradować.
    */

    /**
     * Pobiera listę uczestników (zawodników lub drużyn) posegregowanych do osobnych kolekcji według klas, w których się znajdują.
     * @param tournamentId Id turnieju w bazie danych, z którego pobrać uczestników.
     * @return Lista list zawodników z poszczególnych szczebli ligowych.
     */
    public static ArrayList< ArrayList<TournamentParticipant> > getParticipantsInClasses(int tournamentId){
        ArrayList<TournamentParticipant> competitors = new ArrayList<>(); //TODO Pobrać listę zawodników

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
            RoundRobinTournament.endEntriesStage(competitorsOfXClass);
        }
    }

    /**
     * Zapisuje wynik meczu i oznacza go jako zakończony. Na podstawie wprowadzonego wyniku uaktualnia ranking i punktację uczestników.
     * @param matchId Identyfikator meczu w bazie danych.
     */
    public static void saveResult(int matchId){ //prawdopodobnie klasa Match jako parametr będzie albo id meczu żeby go z bazy pobrać
        RoundRobinTournament.saveResult(matchId); //Zapisywanie wyników tak samo jak w systemie kołowym.
    }

    /**
     * Zakańcza aktualny etap (generuje raport) i przechodzi do następnego (generuje mecze dla pozostałych w następnym etapie uczestników).
     */
    public static void nextStage(int tournamentId){
        //Uwaga: do następnego etapu można przejść tylko wtedy gdy wszystkie mecze w turnieju zostały zakończone (wprowadzono wyniki)

        //TODO Jeżeli liga jest już zarchiwizowana (etap -1) to przerwać i wysłać komunikat
        //TODO Wygenerować prosty raport z listą uczestników i ich punktacją po zakończonym etapie (gracze posegregowani na grupy i posortowani w każdej grupie od największej ilości punktów)

        boolean areAllClassesFinished = true; //Ma określać czy we wszystkich klasach ligi sezon już się zakończył
        ArrayList< ArrayList<TournamentParticipant> > classes = getParticipantsInClasses(tournamentId);
        for(ArrayList<TournamentParticipant> competitorsOfXClass : classes){
            //TODO odkomentować i wstawić numer_etapu_do_ktorego_przechodzimy
            /*if(RoundRobinTournament.generateMatches(tournamentId, competitorsOfXClass, numer_etapu_do_ktorego_przechodzimy)) //Wygenerowanie meczy dla każdej klasy za pomocą algorytmu kołowego
                areAllClassesFinished = false;*/
        }

        if(areAllClassesFinished){
            //TODO Wysłać komunikat o zwycięzcy w klasie pierwszej (najważniejszej) i poinformować że zwycięzców pozostałych można odczytać z właśnie wygenerowanego raportu
            //TODO Zwiększyć sezon o 1 i ustawić etap na 0 - oznacza to czas pomiędzy sezonami (organizator może ponownie awansować, degradować, usuwać, dodawać zawodników)
        }

        //TODO Zapisać wszystko w bazie i wysłać event ServertriggeredEvents->dataUpdated() żeby klient sobie odświeżył dane
    }


}
