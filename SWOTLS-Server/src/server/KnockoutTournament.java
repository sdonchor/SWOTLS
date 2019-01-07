package server;

import java.util.ArrayList;

public class KnockoutTournament extends Tournament {

    public void endEntriesStage(){
        ArrayList<Competitor> competitors = new ArrayList<>(); //TODO pobrać listę uczestników (zawodników albo drużyn)

        // Zasadniczo system pucharowy stosuje się w rozgrywkach, których liczba uczestników jest potęgą
        // liczby 2, tj.: 4, 8, 16, 32, 64 i 128. W innych przypadkach najczęściej „wirtualnie” uzupełnia
        // się listę uczestników do potęgi liczby 2, skutkiem czego część uczestników w pierwszej rundzie
        // ma wirtualnego przeciwnika, czyli tzw. wolny los i bez gry przechodzi do następnej fazy rozgrywek.

        //TODO Sprawdzić czy liczba zapisanych do turnieju uczestników (zawodników lub drużyn) jest potęgą liczby 2
        //TODO Jeżeli nie to albo przerwać i wysłać błąd (ServertriggeredEvents->error(String msg)) albo uzupełnić wirtualnymi zawodnikami "wolny los".

        //TODO Wylosować pary meczowe i utworzyć mecze (każdy uczestnik ma tylko jeden mecz w danym etapie) - mecze te mają być oznaczone jako niezaplanowane

        //TODO Zapisać wszystko w bazie i wysłać event ServertriggeredEvents->dataUpdated() żeby klient sobie odświeżył dane
    }

    public void nextStage(){
        //Uwaga: do następnego etapu można przejść tylko wtedy gdy wszystkie mecze w turnieju zostały zakończone (wprowadzono wyniki)

        //TODO Wylosować nowe pary meczowe tylko spośród graczy którzy jeszcze nie odpadli (wygrali w poprzednim etapie) i utworzyć dla nich mecze oznaczone jako niezaplanowane
        
    }
}
