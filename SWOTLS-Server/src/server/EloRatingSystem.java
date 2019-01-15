package server;

import java.util.List;

public class EloRatingSystem {
    private static final int K = 24;

    /**
     * Oblicza zmianę rankingu dla podanych danych.
     * @param playerRating Ranking gracza (X) dla którego obliczamy zmianę.
     * @param opponentRating Ranking przeciwnika gracza X (gracza Y).
     * @param result 0 jeśli gracz X przegrał, 0.5 jeśli zremisował, 1 jeśli wygrał
     * @return
     */
    public static int calcChange(int playerRating, int opponentRating, float result){
        int d = opponentRating - playerRating;
        if (d > 400) d = 400;
        else if (d < -400) d = -400;
        double we = 1 / (1 + Math.pow(10, d / 400.0));
        double diff = result - we;
        return (int)Math.round(K * diff);
    }

    /**
     * Uaktualnia ranking graczy.
     * @param playerX Gracz X.
     * @param playerY Gracz Y.
     * @param didXWin 1 jeżeli gracz X wygrał, 0.5 jeżeli remis, 0 jeżeli gracz Y wygrał.
     */
    public static void updateRating(Player playerX, Player playerY, float didXWin){
        int ratingX = playerX.getElo();
        int ratingY = playerY.getElo();
        playerX.setElo( playerX.getElo()+calcChange(ratingX, ratingY, didXWin) );
        playerY.setElo( playerY.getElo()+calcChange(ratingY, ratingX, 1-didXWin) );
    }

    /**
     * Uaktualnia ranking graczy na podstawie wyniku starcia drużynowego.
     * @param teamX Drużyna X.
     * @param teamY Drużyna Y.
     * @param didXWin 1 jeżeli dużyna X wygrała, 0.5 jeżeli remis, 0 jeżeli drużyna Y wygrała.
     */
    public static void updateRating(Team teamX, Team teamY, float didXWin){
        //Obliczenie średniego rankingu graczy z drużyny X
        List<Player> playersX = teamX.getPlayers();
        if(playersX.size()==0)
            return;

        int ratingX = 0;
        for(Player p : playersX){
            ratingX += p.getElo();
        }
        ratingX /= playersX.size();

        //Obliczenie średniego rankingu graczy z drużyny Y
        List<Player> playersY = teamY.getPlayers();
        if(playersY.size()==0)
            return;

        int ratingY = 0;
        for(Player p : playersY){
            ratingY += p.getElo();
        }
        ratingY /= playersY.size();

        //Uaktualnienie rankingu na podstawie obliczonych średnich
        int changeX = calcChange(ratingX, ratingY, didXWin);
        for(Player p : playersX){
            p.setElo(p.getElo()+changeX);
        }
        int changeY = calcChange(ratingY, ratingX, 1-didXWin);
        for(Player p : playersY){
            p.setElo(p.getElo()+changeY);
        }
    }
}
