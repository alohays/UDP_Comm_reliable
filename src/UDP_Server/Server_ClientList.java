package UDP_Server;

import java.net.InetAddress;

public class Server_ClientList {
	InetAddress inetAddress;
	int portNumber;

	public Server_ClientList(InetAddress inetAddress, int portNumber) {
		this.inetAddress = inetAddress;
		this.portNumber = portNumber;
	}      // 클라이언트 들의 inetAddress와 port 번호들을 저장하는 클라이언트 리스트
}
