package application;

public class Arena {
    private int id;
    private String location;
    private String name;

    public Arena(int id, String location, String name) {
        this.id = id;
        this.location = location;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }
}
