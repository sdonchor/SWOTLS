package server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

public class KnockoutTournament extends Tournament {
	private static DatabaseHandler dbH = null;
	
	public static void setDbh(DatabaseHandler dbh) {
		dbH=dbh;
	}
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
     * @param tournamentId Id turnieju, którego zapisy zakończyć
     * @return true jeżeli udało się zakończyć zapisy i przejśc do 1 etapu, false jeżeli się nie udało (liczba zwodników nie jest potęgą liczby 2)
     */
    public static boolean endEntriesStage(int tournamentId){
        ArrayList<TournamentParticipant> participants = getTournamentParticipants(tournamentId);

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
            
            return false; 
        }

        List<MatchPair> matchPairs = drawMatchPairs(participants); //wylosowanie par meczowych
        createMatches(tournamentId, matchPairs); //utworzenie niezaplanowanych meczów w bazie

        //Zwiększenie wartości "etap turnieju" z 0 na 1 (0 oznacza zapisy, które właśnie się zakończyły i rozpoczyna się pierwszy etap)
        setTournamentStage(tournamentId, 1);

        return true;
    }

    /**
     * Zapisuje wynik meczu i oznacza go jako zakończony. Na podstawie wprowadzonego wyniku oblicza uaktualnia ranking i punktację uczestników.
     * @param matchId Identyfikator meczu w bazie danych.
     * @return false jeżeli nie udało się zapisać (remis), true jeżeli udało się zapisać.
     */
    public static boolean saveResult(int matchId, int scoreA, int scoreB){
        if(scoreA == scoreB){
            return false;
        }

        server.Match match = getMatchById(matchId);
        Competitor loser;
        Competitor winner;
        if(scoreA<scoreB){
            loser = match.getSideA();
            winner = match.getSideB();
        }else{
            loser = match.getSideB();
            winner = match.getSideA();
        }

        setPoints(match.getCompetitionId(), loser.getId(), -1); //Oznaczenie przegranego jako odpadniętego z turnieju

        updateRating(match.getCompetitionId(), winner, loser, 1); //Aktualizacja rankingu uczestników meczu
        
       
        match.setScoreA(scoreA);
        match.setScoreB(scoreB);


        return true;
    }	
    public static ArrayList<Competitor> getLosers(int tournamentId){

    	ArrayList<Competitor> losers = new ArrayList<>();
    	try {
    		String type = dbH.getQueryBuilder().getTournamentType(tournamentId);
    		if(type.equals("solo"))
    		{
    			CachedRowSet crs = dbH.getQueryBuilder().getLosers(tournamentId);
    			while(crs.next()) {
    				int playerId = crs.getInt("contestant_id");
    				losers.add(dbH.getQueryBuilder().getPlayer(playerId));
    			}
    		}
    		else if(type.equals("team"))
    		{
    			CachedRowSet crs = dbH.getQueryBuilder().getLosers(tournamentId);
    			while(crs.next()) {
    				int teamId = crs.getInt("team_id");
    				losers.add(dbH.getQueryBuilder().getTeam(teamId));
    			}
    		}
			
				
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
        
        return losers;
    }

    public static ArrayList<Competitor> getWinners(int tournamentId){
    	ArrayList<Competitor> winners = new ArrayList<>();
    	try {
    		String type = dbH.getQueryBuilder().getTournamentType(tournamentId);
    		if(type.equals("solo"))
    		{
    			CachedRowSet crs = dbH.getQueryBuilder().getWinners(tournamentId);
    			while(crs.next()) {
    				int playerId = crs.getInt("contestant_id");
    				winners.add(dbH.getQueryBuilder().getPlayer(playerId));
    			}
    		}
    		else if(type.equals("team"))
    		{
    			CachedRowSet crs = dbH.getQueryBuilder().getWinners(tournamentId);
    			while(crs.next()) {
    				int teamId = crs.getInt("team_id");
    				winners.add(dbH.getQueryBuilder().getTeam(teamId));
    			}
    		}
			
				
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
        return winners;
    }

    /**
     * Zakańcza aktualny etap (generuje raport) i przechodzi do następnego (generuje mecze dla pozostałych w następnym etapie uczestników).
     * @param tournamentId Id turnieju, który ma przejść do następnego etapu
     * @return true jeżeli udało się wygenerować nowe mecze, false jeżeli turniej zakończony i wysyłamy komunikat o zwycięzcy
     */
    public static boolean nextStage(int tournamentId){
        //Uwaga: Jeżeli etap turnieju = 0 to zamiast tej metody wywołać endEntriesStage
        //Uwaga: Do następnego etapu można przejść tylko wtedy gdy wszystkie mecze w turnieju zostały zakończone (wprowadzono wyniki)
    	try {
			if(!dbH.getQueryBuilder().allFinished(tournamentId))
			{
				return true;
			}
		} catch (SQLException e) {
	
			e.printStackTrace();
		}
        int stage = getTournamentStage(tournamentId);
        if(stage==-1) {
            return false;
        }else if(stage==0){
            return endEntriesStage(tournamentId);
        }

        //Wygenerowanie raportu z listą przegranych (osób które już odpadły) i listą wygranych (osób które jeszcze biorą udział w turnieju)
        String report = "Uczestnicy pozostali w turnieju:\n";
        ArrayList<Competitor> winners = getWinners(tournamentId);
        for(Competitor c : winners){
            report += c.displayedName() + "\n";
        }
        report += "\nUczestnicy, którzy już odpadli z turnieju:\n";
        ArrayList<Competitor> losers = getLosers(tournamentId);
        for(Competitor c : losers){
            report += c.displayedName() + "\n";
        }
        createReport(tournamentId,"Raport - Etap " + stage, report);

        if(winners.size()==1) { //Jeżeli został tylko jeden gracz to
            setTournamentStage(tournamentId, -1); //oznaczyć etap turnieju jako -1 czyli zakończony
            String s = "Uczestnik " + winners.get(0) + " wygrał cały turniej!";  
            return false;
        }

        List<MatchPair> matchPairs = drawMatchPairs(winners); //Wylosowanie nowych par spośród graczy którzy jeszcze nie odpadli
        createMatches(tournamentId, matchPairs);

        setTournamentStage(tournamentId, stage+1);
        return true;
    }


}
