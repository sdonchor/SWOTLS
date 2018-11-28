package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
//
public class Server {
	private int defaultServerPort=4545;
	private int serverPort;
	private DatabaseHandler dbH=null;
	public Server(int port, DatabaseHandler dbH) {
		this.dbH=dbH;
		serverPort=port;
	}
	public Server(DatabaseHandler dbH) {
		this.dbH=dbH;
		serverPort=defaultServerPort;
	}
	public void runServer() throws IOException{
		ServerSocket serverSocket = new ServerSocket(serverPort);
		System.out.println("Server initialized on port "+serverPort+". Awaiting connections.");
		while(true) {
			Socket socket = serverSocket.accept();
			//System.out.println("new connection");
			new ServerThread(socket,dbH).start();
		}
	}
}
