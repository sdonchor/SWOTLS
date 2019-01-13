package server;

import java.util.*;

public class McMahonTournament extends Tournament {
    /* System McMahona, to uogólnienie systemu szwajcarskiego. Może być używany w turniejach gier mających system rankingowy.
       Przed rozpoczęciem rozgrywek gracze są szeregowani na podstawie rankingu, a następnie dzieleni na grupy
       w zależności od siły. W każdej grupie gracze otrzymują pewną liczbę punktów początkowych. W najczęściej
       stosowanej wersji systemu gracze z najsłabszej dostają po 0 punktów McMahona, nieco silniejsi - po 1 punkt,
       następni - po 2 itd. Należy skonstruować metodę przeliczania rankingu na początkowe punkty turniejowe.
       Np. w przypadku rankingu szachowego można przyjąć 1 punkt McMahona za każde 100 punktów ELO.
    */

    public static int getPlayersElo(int playerId){
        int elo = 1200; //TODO pobranie elo gracza o podanym id
        return elo;
    }

    public static int getTeamElo(int teamId){
        int elo = 1200; //TODO pobierz średnią wartość elo wszystkich członków podanej drużyny
        return elo;
    }

    /**
     * Zakańcza etap zapisów i przechodzi do pierwszego etapu (generuje mecze startowe).
     */
    public static void endEntriesStage(int tournamentId){
        ArrayList<TournamentParticipant> participants = new ArrayList<>(); //TODO pobrać listę uczestników (zawodników albo drużyn)

        if(participants.size()%2==1) {
            //TODO Jeżeli liczba zawodników jest nieparzysta, to albo nie pozwól na wystartowanie turnieju, albo uczestnikowi z najmniejszym dorobkiem punktowym, który jeszcze nie pauzował daj punkt bez gry (i nie bierz go pod uwagę przy losowaniu par)
        }

        Map<TournamentParticipant, Integer> eloOfParticipants = new HashMap<>();
        int lowestElo = 9999999;
        boolean isSoloType = true; //TODO sprawdzenie typu turnieju (solo czy drużynowy)

        //Pobranie elo każdego z uczestników i wyznaczenie najmniejszego z nich
        for(TournamentParticipant p : participants){
            int elo;
            if(isSoloType)
                elo = getPlayersElo(p.getId());
            else
                elo = getTeamElo(p.getId());
            eloOfParticipants.put(p, elo);
            if(elo<lowestElo)
                lowestElo = elo;
        }

        //Gracz z najniższym rankingiem ze wszystkich dostaje 0 punktów na start, pozostali za każde 100 ELO więcej od niego dostają 1 punkt startowy
        for(TournamentParticipant p : participants){
            int elo = eloOfParticipants.get(p)-100;
            int points = 0;
            while (elo>=lowestElo){
                points++;
                elo -= 100;
            }
            p.setPoints(points);
        }

        //Dobranie par w taki sposób aby różnica ich punktów była jak najmniejsza (w miarę możliwości rywale powinni mieć jednakowa liczbę punktów)
        Collections.sort(participants, Collections.reverseOrder()); //można to zrobić poprzez posortowanie listy uczestników od największej do najmniejszej liczby punktów i po kolei dobierać pary
        List<MatchPair> matchPairs = SwissTournament.drawMatchPairs(participants);
        //TODO Utworzyć mecze z uzyskanych par - mają być one oznaczone jako niezaplanowane (brak daty, brak wyników).

        //TODO Zapisać wszystko w bazie i wysłać event ServertriggeredEvents->dataUpdated() żeby klient sobie odświeżył dane
    }

    /**
     * Zapisuje wynik meczu i oznacza go jako zakończony. Na podstawie wprowadzonego wyniku uaktualnia ranking i punktację uczestników.
     * @param matchId Identyfikator meczu w bazie danych.
     */
    public static void saveResult(int matchId){ //prawdopodobnie klasa Match jako parametr będzie, albo id meczu żeby go z bazy pobrać
        SwissTournament.saveResult(matchId); //To samo co w szwajcarskim
    }

    /**
     * Zakańcza aktualny etap (generuje raport) i przechodzi do następnego (generuje mecze dla pozostałych w następnym etapie uczestników).
     */
    public static void nextStage(int tournamentId){
        SwissTournament.nextStage(tournamentId); //To samo co w szwajcarskim
    }


}
