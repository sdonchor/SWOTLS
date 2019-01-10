package server;

public class Tournament {
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
