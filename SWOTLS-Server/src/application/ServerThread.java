package application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread implements Runnable{
	private final int defaultPort = 4545;
	private ServerSocket server = null;
	private Socket client = null;
	private int port;
	public ServerThread(int port) {
		if(port==0)
			this.port=defaultPort;
		else
			this.port=port;
	}
	private void start() throws IOException {
		server = new ServerSocket(port);
		
	}
	@Override
	public void run() {
		try {
			this.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
