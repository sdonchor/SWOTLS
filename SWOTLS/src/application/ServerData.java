package application;

import java.util.HashMap;
import java.util.Map;

public class ServerData {
    /**
     * Pobiera listę wszystkich zawodników federacji.
     * @return Map z kluczami oznaczającymi nazwę zawodnika i wartościami oznaczającymi jego id.
     */
    public static Map<String, Integer> getListOfAllContestants(){
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("Jakub 'kubpica' Pranica", 0);
        map.put("Sebastian 'TheLegend27' Donchór", 1);
        //id jako wartości, bo potrzebuję pobierać id gracza za pomocą klikniętej nazwy
        //jak jakimś cudem będzie 2 razy gracz o takiej samej nazwie to dopisz na końcu coś w stylu (1)
        return map;
    }

    public static Player getContestantById(int id){
        if(id==0)
            return new Player(0, "Jakub", "Pranica", "kubpica", 1200, "pl", "kubpicapf@gmail.com", "", null);
        else
            return new Player(0, "Sebastian", "Donchór", "TheLegend27", 1200, "pl", "", "", null);
    }

    public static Map<String, Integer> getListOfAllTeams(){
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("2Pesteczka", 0);
        map.put("Unity Female", 1);
        return map;
    }

    public static Team getTeamById(int id){
        return null;
    }

    public static Map<String, Integer> getListOfAllTournaments(){
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("2Pesteczka", 0);
        map.put("Unity Female", 1);
        return map;
    }

    public static Competition getTournamentById(int id){
        return null;
    }

    public static Map<String, Integer> getListOfAllMatches(){
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("2Pesteczka vs Unity Female - 25.11.2018 11:00", 0);
        return map;
    }

    public static Match getMatchById(int id){
        return null;
    }

    public static Map<String, Integer> getListOfAllArenas(){
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("Tauron Arena", 0);
        map.put("Online", 1);
        return map;
    }

    public static Arena getArenaById(int id){
        return new Arena(1,"*Online*", "Online");
    }

    public static Map<String, Integer> getListOfAllUsers(){
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("Admin", 0);
        map.put("kubpica", 1);
        return map;
    }

    public static User getUserById(int id){
        return new User(0, "Admin", "Główny");
    }
}
