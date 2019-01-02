package application;

public class Competition {
    private int id;
    private String name;
    public enum Type { SOLO, TEAM }
    private Type type;
    private String additionalInfo;
    private User creator;
    private int stage = 0;

    public Competition(int id, String name, Type type, String additionalInfo, User creator) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.additionalInfo = additionalInfo;
        this.creator = creator;
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
}
