package UDP_Server;

import java.net.*;

public class SocketComm_Server {
	public static void main(String[] args) throws UnknownHostException {
		int PortNumber = Integer.parseInt(args[0]);
		work(PortNumber);
	}

	static void work(int PortNumber) throws UnknownHostException {
		try {
			InetAddress inetaddr = InetAddress.getLocalHost();
			System.out.println("IP: " + inetaddr.getHostAddress() + "\nPort: " + PortNumber);
			DatagramSocket Dsocket = new DatagramSocket(PortNumber);

			Server_Receive rf = new Server_Receive(Dsocket);  //receive °´Ã¼ »ý¼º
			System.out.println("Running the UDP Echo Server...");
			rf.start();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
}