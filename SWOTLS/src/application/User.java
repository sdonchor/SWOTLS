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

    @Override
	public String toString() {
		return "User [id=" + id + ", login=" + login + ", permissions=" + permissions + "]";
	}

	public User(int id, String login, String permissions) {
        this.id = id;
        this.login = login;
        this.permissions = permissions;
    }


}
