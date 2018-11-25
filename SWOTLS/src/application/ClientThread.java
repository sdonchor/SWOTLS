package application;

import java.io.IOException;

public class ClientThread extends Thread {
	private ServerConnection connection = null;
	private String request;

	public ClientThread(ServerConnection sc) {
		connection=sc;
	}
	public void run() {
		try {
			connection.getTable("contestants");
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
