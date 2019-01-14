package server;



import java.util.ArrayList;
import java.util.List;

public class Tournament {
    public static String getDisplayedNameOfPlayer(int playerId){
        //TODO pobieranie wyświetlanej nazwy zawodnika o podanym id (w formie: Imię "pseudonim" Nazwisko)
        return "Adam \"amalysz\" Małysz";
    }

    public static String getNameOfTeam(int teamId){
        //TODO pobieranie nazwy drużyny o podanym id
        return "2Pesteczka";
    }

    /**
     * @param tournamentId id turnieju do sprawdzenia
     * @return true jeżeli typ turnieju to solo, false jeżeli typ turnieju to drużynowy
     */
    public static boolean isSoloType(int tournamentId){
        //TODO true jeżeli typ turnieju to solo, false jeżeli typ turnieju to drużynowy
        return true;
    }

    public static server.Match getMatchById(int matchId){
        //TODO probranie meczu
        //TODO jeżeli mecz jest drużynowy to nie zapomnij pobrać wszystkich członków obu drużyn!!! Team.setPlayers(List<Player> players)
        server.Match m = new server.Match(1, new Player(1,1200), new Player(2,1200), 1);
        return m;
    }

    public static void saveElo(Player p){
        int id = p.getId();
        int elo = p.getElo();
        //TODO zapisane elo gracza w bazie
    }

    /**
     * Ustawia punkty uczestnikowi (zawodnikowi albo drużynie) punkty w turnieju.
     * @param tournamentId Id turnieju
     * @param participantId Id uczestnika (zawodnika albo drużyny)
     * @param score Punkty, które ustawić uczestnikowi
     */
    public static void setPoints(int tournamentId, int participantId, int score){
        //TODO ustawienie puntków uczestnikowi (zawodnikowi albo drużynie - w zależności od typu turnieju)
    }

    public static void addPoint(int tournamentId, int participantId){
        //TODO dodanie 1 punkta uczestnikowi (zawodnikowi albo drużynie - w zależności od typu turnieju)
    }

    public static void addHalfPoint(int tournamentId, int participantId){
        //TODO dodanie 0.5 punkta uczestnikowi (zawodnikowi albo drużynie - w zależności od typu turnieju)
    }

    public static ArrayList<TournamentParticipant> getTournamentParticipants(int tournamentId){
        ArrayList<TournamentParticipant> participants = new ArrayList<>(); //TODO pobrać listę uczestników (zawodników albo drużyn) zapisanych do turnieju
        //TODO W przypadku ligi pamiętaj żeby pobrać też informację o klasie ligowej w której się znajdują (setLeagueClass) poszczególni gracze
        return participants;
    }

    public static void setTournamentStage(int tournamentId, int stage){
        //TODO ustawienie etapu turnieju
    }

    public static int getTournamentStage(int tournamentId){
        int stage = 0; //TODO pobranie etapu turnieju
        return stage;
    }

    public static void setTournamentSeason(int tournamentId, int season){
        //TODO ustawienie etapu turnieju
    }

    public static int getTournamentSeason(int tournamentId){
        int season = 0; //TODO pobranie sezonu turnieju
        return season;
    }

    public static void createMatches(int tournamentId, List<MatchPair> matchPairs){
        //TODO Utworzyć mecze w bazie danych na postawie podanych par - mecze te mają być oznaczone jako niezaplanowane (brak daty, brak wyników)
        //TODO UWAGA! Jeżeli liczba uczestników (w turnieju kołowym lub lidze) jest nieparzysta, to jeden z nich będzie miał parę z zawodnikiem z id -1 - taki zawodnik oznacza wolny los, i jego przeciwnik nie ma meczu w tej rundzie - czyli pominąć tworzenie meczu w którym jeden z uczestników ma id -1
    }

    public static void createReport(String title, String content){
        //TODO Zapisać zaport w bazie danych
    }

    public static void updateRating(int tournamentId, Competitor competitorX, Competitor competitorY, float didXwin){
        boolean isSoloType = isSoloType(tournamentId); //sprawdzenie typu turnieju (solo czy drużynowy)
        if(isSoloType){
            //TODO Competitor to klasa abstrakcyjna po której dziedziczą Player i Team - nie pamiętam czy można tak robić rzutowanie w dół - chyba można, prawda?
            //Solo
            Player pX = (Player) competitorX;
            Player pY = (Player) competitorY;
            EloRatingSystem.updateRating(pX, pY, didXwin);
            saveElo(pX);
            saveElo(pY);
        }else{
            //Team
            Team tX = (Team) competitorX;
            Team tY = (Team) competitorY;
            EloRatingSystem.updateRating(tX, tY, didXwin);
            for(Player p : tX.getPlayers()){
                saveElo(p);
            }
            for(Player p : tY.getPlayers()){
                saveElo(p);
            }
        }
    }

    /**
     * Zapisuje uczestnika do turnieju.
     * @param competitor Uczestnik, którego dodać do turnieju (zawodnik lub drużyna).
     * @param tournamentId Id turnieju w bazie danych.
     */
    public static void addCompetitor(Competitor competitor, int tournamentId){
        //TODO Sprawdzić czy turniej jest w etapie zapisów (wartość 0) - jeżeli nie to wysłać błąd i przerwać dodawanie
        //TODO Dodaj uczestnika (zawodnika lub drużynę) do turnieju (prawdopodobnie encja Competitor-Tournament w bazie)
        //TODO Wysłać event ServertriggeredEvents->dataUpdated() żeby klient sobie odświeżył dane
    }
}
