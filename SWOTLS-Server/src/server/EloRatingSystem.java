package server;

import java.util.List;

public class EloRatingSystem {
    private static final int K = 24;

    public static int calcChange(int winnerRank, int loserRank){
        int d = loserRank - winnerRank;
        if (d > 400) d = 400;
        else if (d < -400) d = -400;
        double we = 1 / (1 + Math.pow(10, d / 400.0));
        double diff = 1 - we;
        return (int)Math.round(K * diff);
    }

    public static void updateRating(Player winner, Player loser){
        int change = calcChange(winner.getElo(), loser.getElo());
        winner.setElo(winner.getElo()+change);
        loser.setElo(loser.getElo()-change);
    }

    public static void updateRating(Team winner, Team loser){
        List<Player> winners = winner.getPlayers();
        int winnersRating = 0;
        for(Player p : winners){
            winnersRating += p.getElo();
        }
        winnersRating /= winners.size();

        List<Player> losers = winner.getPlayers();
        int losersRating = 0;
        for(Player p : losers){
            losersRating += p.getElo();
        }
        losersRating /= losers.size();

        int change = calcChange(winnersRating, losersRating);

        for(Player p : winners){
            p.setElo(p.getElo()+change);
        }
        for(Player p : losers){
            p.setElo(p.getElo()+change);
        }
    }
}
