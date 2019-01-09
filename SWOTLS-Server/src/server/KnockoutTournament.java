package server;

import java.util.ArrayList;

public class KnockoutTournament extends Tournament {

    /**
     * Zakańcza etap zapisów i przechodzi do pierwszego etapu (generuje mecze startowe).
     */
    public void endEntriesStage(){
        ArrayList<Competitor> competitors = new ArrayList<>(); //TODO pobrać listę uczestników (zawodników albo drużyn)

        // Zasadniczo system pucharowy stosuje się w rozgrywkach, których liczba uczestników jest potęgą
        // liczby 2, tj.: 4, 8, 16, 32, 64 i 128. W innych przypadkach najczęściej „wirtualnie” uzupełnia
        // się listę uczestników do potęgi liczby 2, skutkiem czego część uczestników w pierwszej rundzie
        // ma wirtualnego przeciwnika, czyli tzw. wolny los i bez gry przechodzi do następnej fazy rozgrywek.

        //TODO Sprawdzić czy liczba zapisanych do turnieju uczestników (zawodników lub drużyn) jest potęgą liczby 2
        //TODO Jeżeli nie to albo przerwać i wysłać błąd (ServertriggeredEvents->error(String msg)) albo uzupełnić wirtualnymi zawodnikami "wolny los".

        //TODO Wylosować pary meczowe i utworzyć mecze (każdy uczestnik ma tylko jeden mecz w danym etapie) - mecze te mają być oznaczone jako niezaplanowane
        //TODO Zwiększyć wartość "etap turnieju" z 0 na 1 (0 oznacza zapisy, które właśnie się zakończyły i rozpoczyna się pierwszy etap)

        //TODO Zapisać wszystko w bazie i wysłać event ServertriggeredEvents->dataUpdated() żeby klient sobie odświeżył dane
    }

    public void saveResult(){ //prawdopodobnie klasa Match jako parametr będzie
        //TODO Remisy są niedopuszczalne w tym systemie, jeżeli remis to wyślij błąd (ServertriggeredEvents->error(String msg))
        //TODO Przegranego oznacza jako odpadniętego z turnieju
        //TODO Zaktualizostać ranking uczestników meczu
        //EloRatingSystem.updateRating();
        //TODO Zaktualizować wynik i status uczestnika w turnieju, a następnie wysłać event ServertriggeredEvents->dataUpdated() żeby klient sobie odświeżył dane
    }

    /**
     * Zakańcza aktualny erap (generuje raport) i przechodzi do następnego (generuje mecze dla pozostałych w następnym etapie uczestników).
     */
    public void nextStage(){
        //Uwaga: do następnego etapu można przejść tylko wtedy gdy wszystkie mecze w turnieju zostały zakończone (wprowadzono wyniki)

        //TODO Jeżeli turniej jest już zakończony (etap -1) to przerwać i wysłać komunikat o zwycięzcy
        //TODO Wygenerować prosty raport z listą przegranych (osób które już odpadły) i listą wygranych (osób które jeszcze biorą udział w turnieju)
        //TODO Wylosować nowe pary meczowe tylko spośród graczy którzy jeszcze nie odpadli (wygrali w poprzednim etapie) i utworzyć dla nich mecze oznaczone jako niezaplanowane (jeżeli został tylko jeden gracz to wysłać komunikat o zwycięzcy i oznaczyć etap turnieju jako -1 czyli zakończony).
        //TODO Zapisać wszystko w bazie i wysłać event ServertriggeredEvents->dataUpdated() żeby klient sobie odświeżył dane
    }


}
