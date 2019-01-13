package application;

public class Competition {
    private int id;
    private String name;
    public enum Type { SOLO, TEAM }
    private Type type;
    private String additionalInfo;
    private User creator;
    private int stage = 0;
    private int system;
    private int season;

    public Competition(int id, String name, Type type, String additionalInfo, User creator, int stage, int system, int season) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.additionalInfo = additionalInfo;
        this.creator = creator;
        this.stage = stage;
        this.system = system;
        this.season = season;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public User getCreator() {
        return creator;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getSystem() {
        return system;
    }

    public int getSeason() {
        return season;
    }
}
