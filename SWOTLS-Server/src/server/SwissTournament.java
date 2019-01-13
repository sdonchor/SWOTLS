package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SwissTournament extends Tournament {
    /* W systemie szwajcarskim z góry określa się liczbę rund, które należy rozegrać. Na rundę składają się bezpośrednie
       pojedynki (gry) rozgrywane jednocześnie. Za zwycięstwo w grze uczestnik otrzymuje jeden punkt, za remis pół punktu.
       Dobór par przeciwników w kolejnych rundach zależy od wyników uzyskanych w poprzednich. Pary dobiera się w miarę
       możliwości spośród tych uczestników, którzy dotychczas zdobyli jednakową liczbę punktów. Jeśli liczba uczestników
       zawodów jest nieparzysta, w każdej rundzie jeden z uczestników z najmniejszym dorobkiem punktowym, który jeszcze
       nie pauzował, otrzymuje wolny los (tzw. bye) czyli dostaje punkt bez gry.
    */

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
     */
    public static void endEntriesStage(int tournamentId){
        ArrayList<TournamentParticipant> participants = new ArrayList<>(); //TODO pobrać listę uczestników (zawodników albo drużyn)

        if(participants.size()%2==1) {
            //TODO Jeżeli liczba zawodników jest nieparzysta, to albo nie pozwól na wystartowanie turnieju, albo uczestnikowi z najmniejszym dorobkiem punktowym, który jeszcze nie pauzował daj punkt bez gry (i nie bierz go pod uwagę przy losowaniu par)
        }

        //Wylosowanie par startowych losowo
        List<MatchPair> matchPairs = drawMatchPairs(participants); //wylosowanie par meczowych
        //TODO Utworzyć mecze w bazie danych na postawie wylosowanych par - mecze te mają być oznaczone jako niezaplanowane (brak daty, brak wyników)

        //TODO Zapisać wszystko w bazie i wysłać event ServertriggeredEvents->dataUpdated() żeby klient sobie odświeżył dane
    }

    /**
     * Zapisuje wynik meczu i oznacza go jako zakończony. Na podstawie wprowadzonego wyniku uaktualnia ranking i punktację uczestników.
     * @param matchId Identyfikator meczu w bazie danych.
     */
    public static void saveResult(int matchId){ //prawdopodobnie klasa Match jako parametr będzie albo id meczu żeby go z bazy pobrać
        //TODO Wygranemu dodaj 1 punkt, jeżeli remis to obu ustecztnikom meczu dodaj 0.5 punkta.
        //TODO Zaktualizostać ranking uczestników meczu
        //EloRatingSystem.updateRating(); //TODO trzeba sprawdzić jaki typ turnieju (czy drużynowy czy solo) i wywołać metodę z odpowiednim typem parametrów
        //TODO Zaktualizować wynik i status uczestnika w turnieju, a następnie wysłać event ServertriggeredEvents->dataUpdated() żeby klient sobie odświeżył dane
    }

    /**
     * Zakańcza aktualny etap (generuje raport) i przechodzi do następnego (generuje mecze dla pozostałych w następnym etapie uczestników).
     */
    public static void nextStage(int tournamentId){
        //Uwaga: do następnego etapu można przejść tylko wtedy gdy wszystkie mecze w turnieju zostały zakończone (wprowadzono wyniki)

        //TODO Jeżeli turniej jest już zakończony (etap -1) to przerwać i wysłać komunikat o zwycięzcy (jeżeli więcej niż 1 osoba ma największą ilość punktów to albo ogłosić kilku wzycięzców i niech się martwią sami, albo wygenerować mecze dogrywkowe)

        ArrayList<TournamentParticipant> participants = new ArrayList<>(); //TODO pobrać listę uczestników (zawodników albo drużyn) podanego turnieju
        //Generacja raportu z listą graczy i ich punktacją po zakończonym etapie (uczestnicy posortowani od największej ilości punktów)
        Collections.sort(participants, Collections.reverseOrder());
        String raport = "Punktacja po X rundzie turnieju:\n";

        boolean isSoloType = true; //TODO sprawdzenie typu turnieju (solo czy drużynowy)
        for(TournamentParticipant p : participants){
            if(isSoloType)
                raport += getDisplayedNameOfPlayer(p.getId()) + " - " + p.getPoints();
            else
                raport += getNameOfTeam(p.getId()) + " - " + p.getPoints();
            raport += "\n";
        }
        //TODO zapisać raport w bazie danych (przykładowy format tytułu: "Raport - Etap 1")

        //TODO Jeżeli aktualny etap był ostatnim (organizator określa liczbę rund) to wysłać komunikat o zwycięzcy i oznaczyć etap turnieju jako -1 (zakończony)
        
        //TODO Jeżeli liczba graczy jest nieparzysta (i na to pozwalamy) to uczestnikowi z najmniejszym dorobkiem punktowym, który jeszcze nie pauzował daj punkt bez gry (i nie bierz go pod uwagę przy losowaniu par)

        //Dobranie par w taki sposób aby różnica ich punktów była jak najmniejsza (w miarę możliwości rywale powinni mieć jednakowa liczbę punktów)
        List<MatchPair> matchPairs = drawMatchPairs(participants); //lista jest już posortowana wg. punktacji malegąco więc poprostu dobieram z niej kolejno pary

        //TODO Zapisać wszystko w bazie i wysłać event ServertriggeredEvents->dataUpdated() żeby klient sobie odświeżył dane
    }


}
