package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoundRobinTournament extends Tournament {
    /* System kołowy – sposób rozgrywania zawodów sportowych w dyscyplinach, w których rywalizacja polega na bezpośrednich
       pojedynkach pomiędzy uczestnikami. W systemie kołowym każdy uczestnik gra kolejno ze wszystkimi przeciwnikami.
       Ten sposób rozgrywek nazywany jest również systemem każdy z każdym, w anglojęzycznej literaturze round-robin.
    */

    public static ArrayList<TournamentParticipant> getCompetitors(int tournamentId){
        ArrayList<TournamentParticipant> competitors = new ArrayList<>(); //TODO pobrać listę uczestników turnieju
        return competitors;
    }

    public static void endEntriesStage(int tournamentId, ArrayList<TournamentParticipant> competitors) {
        //Numerowanie zawodników pozycjami startowymi 1, 2, ... n (gdzie n to liczba zawodników w turnieju)
        int i = 1;
        for(TournamentParticipant participant : competitors){
            participant.setStartingPosition(i++);
        }

        generateMatches(tournamentId, competitors, 1);

        //TODO Zapisać wszystko w bazie i wysłać event ServertriggeredEvents->dataUpdated() żeby klient sobie odświeżył dane
    }

    /**
     * Zakańcza etap zapisów i przechodzi do pierwszego etapu (generuje mecze startowe).
     */
    public static void endEntriesStage(int tournamentId){
        endEntriesStage(tournamentId, getCompetitors(tournamentId));
        //TODO Zwiększyć wartość "etap turnieju" z 0 na 1 (0 oznacza zapisy, które właśnie się zakończyły i rozpoczyna się pierwszy etap)
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
        }
        //TODO zapisać raport w bazie danych

        //TODO Zwiększyć numer etapu. Jeżeli aktualny etap był ostatnim (liczba_etapów == ilość_uczestników-1) to wysłać komunikat o zwycięzcy i oznaczyć etap turnieju jako -1 (zakończony)

        //generateMatches(tournamentId, getCompetitors(tournamentId), numer_etapu_do_ktorego_przechodzimy);

        //TODO Zapisać wszystko w bazie i wysłać event ServertriggeredEvents->dataUpdated() żeby klient sobie odświeżył dane
    }

    /**
     * Generuje pary meczowe z przekazanej listy zawodników dla podanego etapu.
     * @param tournamentId Id turnieju w bazie, do którego przypisać wygenerowane mecze.
     * @param competitors Lista zawodników wsród których wyłonić pary.
     * @param stageOfMatchesToGenerate Etap dla którego wygenerować pary meczowe.
     * @return True, jeżeli udało się wygenerować mecze; False, jeżeli sezon w tej klasie już się zakończył.
     */
    public static boolean generateMatches(int tournamentId, ArrayList<TournamentParticipant> competitors, int stageOfMatchesToGenerate){
        if(stageOfMatchesToGenerate>competitors.size()-1)
            return false;

        //Wygenerowanie par meczowych za pomocą koła Berga
        List<MatchPair> pairs = BergsCircle.getPairs(competitors, stageOfMatchesToGenerate); //etapy rozgrywek są numerowane od 1 (etap 0 to zapisy, -1 to turniej zakończony)
        //TODO Utworzyć mecze z uzyskanych par. UWAGA! Jeżeli liczba uczestników jest nieparzysta, to jeden z nich będzie miał parę z zawodnikiem z id -1 - taki zawodnik oznacza wolny los, i jego przeciwnik nie ma meczu w tej rundzie.
        //TODO Utworzone mecze mają być oznaczone jako niezaplanowane (brak daty, brak wyników).
        return true;
    }

}
