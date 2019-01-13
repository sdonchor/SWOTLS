package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KnockoutTournament extends Tournament {

    public static List<MatchPair> drawMatchPairs(List<? extends Identifiable> participants){
        //Losowanie par meczowych (każdy uczestnik ma tylko jeden mecz w danym etapie)
        List<MatchPair> matchPairs = new ArrayList<>();
        Collections.shuffle(participants);
        for(int i = 0; i<participants.size(); i+=2){
            matchPairs.add(new MatchPair(participants.get(i).getId(), participants.get(i+1).getId()));
        }
        return matchPairs;
    }

    /**
     * Zakańcza etap zapisów i przechodzi do pierwszego etapu (generuje mecze startowe).
     */
    public static void endEntriesStage(int tournamentId){
        ArrayList<TournamentParticipant> participants = new ArrayList<>(); //TODO pobrać listę uczestników (zawodników albo drużyn) zapisanych do turnieju

        // Zasadniczo system pucharowy stosuje się w rozgrywkach, których liczba uczestników jest potęgą
        // liczby 2, tj.: 4, 8, 16, 32, 64 i 128. W innych przypadkach najczęściej „wirtualnie” uzupełnia
        // się listę uczestników do potęgi liczby 2, skutkiem czego część uczestników w pierwszej rundzie
        // ma wirtualnego przeciwnika, czyli tzw. wolny los i bez gry przechodzi do następnej fazy rozgrywek.

        //Sprawdzenie czy liczba zapisanych do turnieju uczestników (zawodników lub drużyn) jest potęgą liczby 2
        boolean isValidPlayerCount = false;
        for(int i = 2; i<=participants.size(); i*=2){
            if(i == participants.size()){
                isValidPlayerCount = true;
                break;
            }
        }
        if(!isValidPlayerCount) {
            //TODO Jeżeli nie to albo przerwać i wysłać błąd (ServertriggeredEvents->error(String msg)) albo uzupełnić wirtualnymi zawodnikami "wolny los".
        }

        List<MatchPair> matchPairs = drawMatchPairs(participants); //wylosowanie par meczowych
        //TODO Utworzyć mecze w bazie danych na postawie wylosowanych par - mecze te mają być oznaczone jako niezaplanowane (brak daty, brak wyników)

        //TODO Zwiększyć wartość "etap turnieju" z 0 na 1 (0 oznacza zapisy, które właśnie się zakończyły i rozpoczyna się pierwszy etap)

        //TODO Zapisać wszystko w bazie i wysłać event ServertriggeredEvents->dataUpdated() żeby klient sobie odświeżył dane
    }

    /**
     * Zapisuje wynik meczu i oznacza go jako zakończony. Na podstawie wprowadzonego wyniku oblicza uaktualnia ranking i punktację uczestników.
     * @param matchId Identyfikator meczu w bazie danych.
     */
    public static void saveResult(int matchId){ //prawdopodobnie klasa Match jako parametr będzie albo id meczu żeby go z bazy pobrać
        //TODO Remisy są niedopuszczalne w tym systemie, jeżeli remis to wyślij błąd (ServertriggeredEvents->error(String msg))
        //TODO Przegranego oznaczyć jako odpadniętego z turnieju
        //TODO Zaktualizostać ranking uczestników meczu
        //EloRatingSystem.updateRating(); //TODO trzeba sprawdzić jaki typ turnieju (czy drużynowy czy solo) i wywołać metodę z odpowiednim typem parametrów
        //TODO Zaktualizować wynik i status uczestnika w turnieju, a następnie wysłać event ServertriggeredEvents->dataUpdated() żeby klient sobie odświeżył dane
    }

    /**
     * Zakańcza aktualny etap (generuje raport) i przechodzi do następnego (generuje mecze dla pozostałych w następnym etapie uczestników).
     */
    public static void nextStage(int tournamentId){
        //Uwaga: Jeżeli etap turnieju = 0 to zamiast tej metody wywołać endEntriesStage
        //Uwaga: Do następnego etapu można przejść tylko wtedy gdy wszystkie mecze w turnieju zostały zakończone (wprowadzono wyniki)

        //TODO Jeżeli turniej jest już zakończony (etap -1) to przerwać i wysłać komunikat o zwycięzcy

        //Wygenerowanie raportu z listą przegranych (osób które już odpadły) i listą wygranych (osób które jeszcze biorą udział w turnieju)
        String raport = "Uczestnicy pozostali w turnieju:\n";
        ArrayList<Competitor> winners = new ArrayList<>(); //TODO pobrać listę uczestników (zawodników lub drużyn) którzy jeszcze nie odpadli w podanym turnieju
        for(Competitor c : winners){
            raport += c.displayedName() + "\n";
        }
        raport += "\nUczestnicy, którzy już odpadli z turnieju:\n";
        ArrayList<Competitor> losers = new ArrayList<>(); //TODO pobrać listę uczestników (zawodników lub drużyn) którzy już odpadli w podanym turnieju
        for(Competitor c : losers){
            raport += c.displayedName() + "\n";
        }
        //TODO Zapisać raport w bazie

        if(winners.size()==1) {
            String s = "Uczestnik " + winners.get(0) + " wygrał cały turniej!";
            //TODO Jeżeli został tylko jeden gracz to wysłać komunikat o zwycięzcy i oznaczyć etap turnieju jako -1 czyli zakończony.
            //TODO wysłać event ServertriggeredEvents->dataUpdated() (żeby klient odświeżył panel "Wyniki" - inaczej nie pobierze nowego raportu)
            return;
        }

        List<MatchPair> matchPairs = drawMatchPairs(winners); //Wylosowanie nowych par spośród graczy którzy jeszcze nie odpadli
        //TODO Utworzyć mecze w bazie danych na postawie wylosowanych par - mecze te mają być oznaczone jako niezaplanowane (brak daty, brak wyników)

        //TODO Zapisać wszystko w bazie i wysłać event ServertriggeredEvents->dataUpdated() żeby klient sobie odświeżył dane
    }


}
