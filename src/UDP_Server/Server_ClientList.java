package UDP_Server;

import java.net.InetAddress;

public class Server_ClientList {
	InetAddress inetAddress;
	int portNumber;

	public Server_ClientList(InetAddress inetAddress, int portNumber) {
		this.inetAddress = inetAddress;
		this.portNumber = portNumber;
	}      // Ŭ���̾�Ʈ ���� inetAddress�� port ��ȣ���� �����ϴ� Ŭ���̾�Ʈ ����Ʈ
}
