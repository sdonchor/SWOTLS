package application;

public class Competition {
    private int id;
    private String name;
    public enum Type { SOLO, TEAM }
    private Type type;
    private String additionalInfo;
    private User creator;
}
