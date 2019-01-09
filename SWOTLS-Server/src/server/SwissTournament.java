package server;

import java.util.ArrayList;

public class SwissTournament extends Tournament {
    /* W systemie szwajcarskim z góry określa się liczbę rund, które należy rozegrać. Na rundę składają się bezpośrednie
       pojedynki (gry) rozgrywane jednocześnie. Za zwycięstwo w grze uczestnik otrzymuje jeden punkt, za remis pół punktu.
       Dobór par przeciwników w kolejnych rundach zależy od wyników uzyskanych w poprzednich. Pary dobiera się w miarę
       możliwości spośród tych uczestników, którzy dotychczas zdobyli jednakową liczbę punktów. Jeśli liczba uczestników
       zawodów jest nieparzysta, w każdej rundzie jeden z uczestników z najmniejszym dorobkiem punktowym, który jeszcze
       nie pauzował, otrzymuje wolny los (tzw. bye) czyli dostaje punkt bez gry.
    */

    /**
     * Zakańcza etap zapisów i przechodzi do pierwszego etapu (generuje mecze startowe).
     */
    public void endEntriesStage(){
        ArrayList<Competitor> competitors = new ArrayList<>(); //TODO pobrać listę uczestników (zawodników albo drużyn)

        //TODO Jeżeli liczba zawodników jest nie parzysta, to albo nie pozwól na wystartowanie turnieju, albo uczestnikowi z najmniejszym dorobkiem punktowym, który jeszcze nie pauzował daj punkt bez gry (i nie bierz go pod uwagę przy losowaniu par)
        //TODO Dobrać pary w taki sposób aby różnica ich punktów była jak najmniejsza (w miarę możliwości rywale powinni mieć jednakowa liczbę punktów)
        //TODO Utworzone mecze oznaczyć jako niezaplanowane. (każdy uczestnik ma tylko jeden mecz w danym etapie)

        //TODO Zapisać wszystko w bazie i wysłać event ServertriggeredEvents->dataUpdated() żeby klient sobie odświeżył dane
    }

    public void saveResult(){ //prawdopodobnie klasa Match jako parametr będzie
        //TODO Wygranemu dodaj 1 punkt, jeżeli remis to obu ustecztnikom meczu dodaj 0.5 punkta.
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
        //TODO Wygenerować prosty raport z listą graczy i ich punktacją po zakończonym etapie
        //TODO Jeżeli aktualny etap był ostatnim to wysłać komunikat o zwycięzcy i oznaczyć etap turnieju jako -1 (zakończony)
        //TODO Jeżeli liczba graczy jest nieparzysta (i na to pozwalamy) to uczestnikowi z najmniejszym dorobkiem punktowym, który jeszcze nie pauzował daj punkt bez gry (i nie bierz go pod uwagę przy losowaniu par)
        //TODO Dobrać nowe pary meczowe w taki sposób aby różnica ich punktów była jak najmniejsza (jeżeli został tylko jeden gracz to wysłać komunikat o zwycięzcy).
        //TODO Zapisać wszystko w bazie i wysłać event ServertriggeredEvents->dataUpdated() żeby klient sobie odświeżył dane
    }


}
