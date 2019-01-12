package server;

import javax.sql.rowset.CachedRowSet;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
//
public class ServerThread extends Thread{
	private Socket socket;
	private SystemUser currentUser = null;
	DatabaseHandler dbH=null;
	String remoteIP=null;
	public ServerThread(Socket socket,DatabaseHandler dbH) {
		this.dbH=dbH;
		this.socket=socket;
		
	}
	public String getCleanIP() {
		String currIP=socket.getRemoteSocketAddress().toString();
		String[] split = currIP.split(":");
		return split[0];
	}
	public void updateUser() {
		if(LoggedInList.getUserByIP(remoteIP)!=null)
		{
			currentUser=LoggedInList.getUserByIP(remoteIP);
		}
	}
	public void run() {
		this.remoteIP=getCleanIP();
		updateUser();
		try {
			String message=null;
			BufferedReader bufferedReader = new BufferedReader ( new InputStreamReader(socket.getInputStream()));
			while((message=bufferedReader.readLine())!=null)
			{
				System.out.println("Acquired message: "+message);
				if(message.contains("get-table")){
					String[] request = message.split(";");
					String tableName = request[1];
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					try {
						
						CachedRowSet crs = dbH.getQueryBuilder().getTable(tableName);
						oos.writeObject(crs);
						oos.close();
						os.close();
					} catch (SQLException e) {
						//System.out.println("SQL error while gettin table");
						e.printStackTrace();
					}
					
				}
				if(message.contains("remove-record")){
					String[] request = message.split(";");
					String tablename = request[1];
					int id = Integer.parseInt(request[2]);
					
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("boolean");
					//usuwaj wszystkie powiązane anyways
					boolean success;
					try {
						success = dbH.getQueryBuilder().removeFromTable(tablename,id);
						sr.setBoolTypeResponse(success);
						oos.writeObject(sr);
					} catch (SQLException e) {
						if(e.getMessage().contains("foreign key constraint fails"))
						{
							sr.setBoolTypeResponse(false);
							sr.setStringTypeResponse("fk-check");
							oos.writeObject(sr);
						}
					}
					oos.close();
					os.close();
				}
				if(message.contains("create-tournament")){
					if(currentUser!=null)
					{
						if(!currentUser.getPermissions().equals("FULL")&&!currentUser.getPermissions().equals("ORGANIZER")){
							System.out.println("Can't create tournament - no user or no permissions");
							continue;
						}
					}
					String[] request = message.split(";");
					String name = request[1];
					String system = request[2];
					String type = request[3];
					String additional = request[4];
					
					int operator = currentUser.getId();
					
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("boolean");
					
					boolean success;
					try {
						success = dbH.getQueryBuilder().createTournament(name,system,type,additional,operator);
						sr.setBoolTypeResponse(success);
						oos.writeObject(sr);
					} catch (SQLException e) {
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						System.out.println("SQLException when adding a tournament.");
						e.printStackTrace();
					}
					oos.close();
					os.close();
				}
				if(message.contains("create-user")){
					//TODO check if logged in
					String[] request = message.split(";");
					String name = request[1];
					String pw = request[2];
					String perms = request[3];
					
					
					
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("boolean");
					
					boolean success;
					try {
						success = dbH.getQueryBuilder().createUser(name,pw,perms);
						sr.setBoolTypeResponse(success);
						oos.writeObject(sr);
					} catch (SQLException e) {
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						System.out.println("SQLException when adding a user.");
						e.printStackTrace();
					}
					oos.close();
					os.close();
				}
				if(message.contains("verify-login")){
					String[] request = message.split(";");
					String login = request[1];
					String pw = request[2];
					
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("int");
					int uid=-1;
					SystemUser fetchedUser=null;
					try {
						fetchedUser = dbH.getQueryBuilder().verifySystemLogin(login,pw);
						if(fetchedUser==null)
							sr.setBoolTypeResponse(false);
						else
						{
							sr.setBoolTypeResponse(true);
							currentUser=fetchedUser;
							LoggedInList.addUser(getCleanIP(), fetchedUser);
							uid = fetchedUser.getId();
							sr.setStringTypeResponse(fetchedUser.getPermissions());
							
						}
						sr.setIntTypeResponse(uid);
						oos.writeObject(sr);
					} catch (SQLException e) {
						sr.setBoolTypeResponse(false);
						sr.setIntTypeResponse(-1);
						oos.writeObject(sr);
					}
					oos.close();
					os.close();
				}
				if(message.contains("add-player")){
					//TODO check if logged in
					String[] request = message.split(";");
					String name = request[1];
					String surname = request[2];
					String nickname = request[3];
					String contact = request[4];
					String language = request[5];
					String additional = request[6];
					String teamid = request[7];
				
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("boolean");
					
					boolean success;
					try {
						success = dbH.getQueryBuilder().addPlayer(name,nickname,surname,contact,language,additional,teamid);
						sr.setBoolTypeResponse(success);
						oos.writeObject(sr);
					} catch (SQLException e) {
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						System.out.println("SQLException when adding a user.");
					}
					oos.close();
					os.close();
				}
				if(message.contains("log-out")) {
					LoggedInList.removeUserByIP(getCleanIP());
					System.out.println("User logged out");
				}
			}
		} catch (IOException e) {
			System.out.println("Connection closed.");
		}
	}
}
