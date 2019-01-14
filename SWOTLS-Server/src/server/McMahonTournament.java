package server;

import java.sql.SQLException;
import java.util.*;

public class McMahonTournament extends Tournament {
    /* System McMahona, to uogólnienie systemu szwajcarskiego. Może być używany w turniejach gier mających system rankingowy.
       Przed rozpoczęciem rozgrywek gracze są szeregowani na podstawie rankingu, a następnie dzieleni na grupy
       w zależności od siły. W każdej grupie gracze otrzymują pewną liczbę punktów początkowych. W najczęściej
       stosowanej wersji systemu gracze z najsłabszej dostają po 0 punktów McMahona, nieco silniejsi - po 1 punkt,
       następni - po 2 itd. Należy skonstruować metodę przeliczania rankingu na początkowe punkty turniejowe.
       Np. w przypadku rankingu szachowego można przyjąć 1 punkt McMahona za każde 100 punktów ELO.
    */

	private static DatabaseHandler dbH = null;
	
	public static void setDbh(DatabaseHandler dbh) {
		dbH=dbh;
	}
    public static int getPlayersElo(int playerId){
        Player x;
        int elo=1200;
		try {
			x = dbH.getQueryBuilder().getPlayer(playerId);
			elo = x.getElo();
		} catch (SQLException e) {
		
			e.printStackTrace();
		}
        
        return elo;
    }

    public static int getTeamElo(int teamId){
    	int elo=1200;
    	try {
			Team x = dbH.getQueryBuilder().getTeam(teamId);
			int count = 0;
			int sum = 0;
			List<Player> members = x.getPlayers();
			for(int i = 0 ; i<members.size() ; i++)
			{
				Player current = members.get(i);
				sum+=current.getElo();
				count++;
			}
			elo=sum/count;
			
		} catch (SQLException e) {
				e.printStackTrace();
		}
    	
        return elo;
    }

    /**
     * Zakańcza etap zapisów i przechodzi do pierwszego etapu (generuje mecze startowe).
     * @param tournamentId Id turnieju, którego etap zapisów próbujemy zakończyć.
     * @return true jeżeli udało się zakończyć zapisy i przejść do pierwszego etapu, false jeżeli liczba uczestników jest nieparzysta
     */
    public static boolean endEntriesStage(int tournamentId){
        ArrayList<TournamentParticipant> participants = getTournamentParticipants(tournamentId);

        if(participants.size()%2==1) {
            
            return true; //TODO false?
        }

        Map<TournamentParticipant, Integer> eloOfParticipants = new HashMap<>();
        int lowestElo = 9999999;
        boolean isSoloType = isSoloType(tournamentId); //sprawdzenie typu turnieju (solo czy drużynowy)

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
        createMatches(tournamentId, matchPairs);

        setTournamentStage(tournamentId, 1);

        return true;
    }

    /**
     * Zapisuje wynik meczu i oznacza go jako zakończony. Na podstawie wprowadzonego wyniku uaktualnia ranking i punktację uczestników.
     * @param matchId Identyfikator meczu w bazie danych.
     */
    public static void saveResult(int matchId, int sideA, int sideB){ //prawdopodobnie klasa Match jako parametr będzie, albo id meczu żeby go z bazy pobrać
        SwissTournament.saveResult(matchId, sideA, sideB); //To samo co w szwajcarskim
    }

    /**
     * Zakańcza aktualny etap (generuje raport) i przechodzi do następnego (generuje mecze dla pozostałych w następnym etapie uczestników).
     * @param tournamentId Id turnieju, który ma przejść do następnego etapu
     * @return true jeżeli udało się wygenerować nowe mecze, false jeżeli turniej zakończony i wysyłamy komunikat o zwycięzcy
     */
    public static boolean nextStage(int tournamentId){
        return SwissTournament.nextStage(tournamentId); //To samo co w szwajcarskim
    }


}
