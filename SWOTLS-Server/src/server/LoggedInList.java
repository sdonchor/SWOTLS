package server;

import java.util.HashMap;
import java.util.Map;

public class LoggedInList {
	private static Map <String,SystemUser> userList= new HashMap<String,SystemUser>();
	public static void  addUser(String ip, SystemUser usr) {
		userList.put(ip, usr);
	}
	public static SystemUser getUserByIP(String ip) {
		for(Map.Entry<String, SystemUser> entry:userList.entrySet())
		{
			String entryIP = entry.getKey();
			SystemUser entryUser = entry.getValue();
			if(entryIP.equals(ip)) {
				return entryUser;
			}
		}
		return null;
	}
	public static void removeUserByIP(String ip) {
		userList.remove(ip);
	}
	public static void printList() {
		for(Map.Entry<String, SystemUser> entry:userList.entrySet())
		{
			String entryIP = entry.getKey();
			SystemUser entryUser = entry.getValue();
			System.out.println(entryIP+" - "+entryUser);
		}
	}
}
