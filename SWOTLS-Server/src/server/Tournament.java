package server;

public class Tournament {
    public static String getDisplayedNameOfPlayer(int playerId){
        //TODO pobieranie wyświetlanej nazwy zawodnika o podanym id (w formie: Imię "pseudonim" Nazwisko)
        return "Adam \"amalysz\" Małysz";
    }

    public static String getNameOfTeam(int teamId){
        //TODO pobieranie nazwy drużyny o podanym id
        return "2Pesteczka";
    }

    /**
     * Zapisuje uczestnika do turnieju.
     * @param competitor Uczestnik, którego dodać do turnieju (zawodnik lub drużyna).
     * @param tournamentId Id turnieju w bazie danych.
     */
    public static void addCompetitor(Competitor competitor, int tournamentId){
        //TODO Sprawdzić czy turniej jest w etapie zapisów (wartość 0) - jeżeli nie to wysłać błąd i przerwać dodawanie
        //TODO Dodaj uczestnika (zawodnika lub drużynę) do turnieju (prawdopodobnie encja Competitor-Tournament w bazie)
        //TODO Wysłać event ServertriggeredEvents->dataUpdated() żeby klient sobie odświeżył dane
    }
}
