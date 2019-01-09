package application;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerConnection {
	private Socket socket = null;
	private String address = null;
	private int port;
	private boolean newRequest = false;
	public ServerConnection(String address, int port) throws UnknownHostException, IOException {
		this.address=address;
		this.port=port;
		//this.socket=new Socket(address,port);
	}
	public void sendServerRequest(String request) throws IOException {
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		printWriter.println(request);
	}
	public boolean getNewRequest() {
		return newRequest;
	}
	public CachedRowSet getTable(String tableName) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "get-table;"+tableName;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		CachedRowSet crs = (CachedRowSet)ois.readObject();
		if(crs!=null)System.out.println("Successfully downloaded table "+tableName);
		/*if(crs!=null)
		{
			try {
				while(crs.next())
				{
					int id = crs.getInt("contestant_id");
					String nickname = crs.getString("nickname");
					System.out.println(id+" "+nickname);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		
		socket.close();
		return crs;
	}
	public void socketClose() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void socketOpen() {
		try {
			socket=new Socket(address,port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean entryRemoval(int id, String type) {
		// TODO Auto-generated method stub
		return false;
	}
	public Permission getCurrentUserPerms() {
		// TODO Auto-generated method stub
		return null;
	}
	public String getCurrentUserName() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean verifyLogin(String id, String pw) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean createNewUser(String id, String pw, Permission perm) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean createNewTournament(String name, String system, String type, String additional) {
		// TODO Auto-generated method stub
		return false;
	}
}
