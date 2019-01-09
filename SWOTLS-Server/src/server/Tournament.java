package server;

public class Tournament {
    public void addCompetitor(Competitor competitor){
        //TODO Sprawdzić czy turniej jest w etapie zapisów (wartość 0) - jeżeli nie to wysłać błąd i przerwać dodawanie
        //TODO Dodaj uczestnika (zawodnika lub drużynę) do turnieju (prawdopodobnie encja Competitor-Tournament w bazie)
        //TODO Wysłać event ServertriggeredEvents->dataUpdated() żeby klient sobie odświeżył dane
    }
}
