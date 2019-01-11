package server;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
	private final static String salt = "S_W_O_T_L_S_2018/2019_38D705FB653DFF65EC7F334811C21FC870FB0208750EDD371436C5C515D7F6B2";
	public static String hashString(String pw) {
		MessageDigest digest;
		pw=pw+salt;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(pw.getBytes(StandardCharsets.UTF_8));
			StringBuffer hexString = new StringBuffer();
		    for (int i = 0; i < hash.length; i++) {
		    String hex = Integer.toHexString(0xff & hash[i]);
		    if(hex.length() == 1) hexString.append('0');
		        hexString.append(hex);
		    }
		    return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		
	}
}
