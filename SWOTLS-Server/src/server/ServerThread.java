package server;

import javax.sql.rowset.CachedRowSet;
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
//
public class ServerThread extends Thread{
	private Socket socket;
	private SystemUser currentUser = null;
	DatabaseHandler dbH=null;
	public ServerThread(Socket socket,DatabaseHandler dbH) {
		this.dbH=dbH;
		this.socket=socket;
	}
	public void run() {
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
					String[] request = message.split(";");
					String name = request[1];
					String system = request[2];
					String type = request[3];
					String additional = request[4];
					int operator = Integer.parseInt(request[5]);
					
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
			}
		} catch (IOException e) {
			System.out.println("Connection lost.");
		}
	}
}
