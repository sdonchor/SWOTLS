package application;

public class User {
    private int id;
    private String login;
    private String permissions;

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPermissions() {
        return permissions;
    }

    public User(int id, String login, String permissions) {
        this.id = id;
        this.login = login;
        this.permissions = permissions;
    }


}
