package application;

public class SystemUser {
	private String login;
	private String pw_hash;
	private String permissions;
	public SystemUser(String login, String pw_hash)
	{
		this.login=login;
		this.pw_hash=pw_hash;
		//this.permissions=permissions;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPw_hash() {
		return pw_hash;
	}
	public void setPw_hash(String pw_hash) {
		this.pw_hash = pw_hash;
	}
	public String getPermissions() {
		return permissions;
	}
	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}
}
