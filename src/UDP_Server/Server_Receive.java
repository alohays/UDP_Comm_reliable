package UDP_Server;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;


public class Server_Receive extends Thread {
	final int MAXBUFFER = 508;
	DatagramSocket Dsocket;
	private HashSet<Integer> port_Set = new HashSet<Integer>();
	private ArrayList<Server_ClientList> client_List = new ArrayList<Server_ClientList>();
	
	Server_Receive(DatagramSocket s) {
		Dsocket = s;
	}

	public void run() {
		while (true) {
			byte[] buffer = new byte[MAXBUFFER];
			try {
				DatagramPacket recv_packet = new DatagramPacket(buffer, buffer.length);
				Dsocket.receive(recv_packet);

				byte[] data_Frame = new byte[MAXBUFFER]; 
				data_Frame = recv_packet.getData();
				String test = new String(data_Frame);
				test = test.trim();
				System.out.println(test);
				
				if (port_Set.contains(recv_packet.getPort())) {  // �̹� �����ϴ� Ŭ���̾�Ʈ�� ���
					System.out.println("test1");
					byte[] recv_message = new byte[MAXBUFFER];
					for (int i = 0; i < 500; i++) {  // data_Frame[3]�� �޽����� ���̰� ��� �ִ�
						recv_message[i] = data_Frame[i + 3];   // ������ ������ �� �޽��� �κ��� ����
					}

					//if (data_Frame[2] < (byte) 0x80) {  
						String message = "from <" + recv_packet.getAddress().toString() + " - ";
						message += String.valueOf(recv_packet.getPort());
						message += "> message: " + new String(recv_message);
						System.out.println(message);
					//}
					Send_to_All(data_Frame, recv_packet.getPort());
				} else {   // ���� join�ϴ� Ŭ���̾�Ʈ�� ���
					System.out.println("test2");
					port_Set.add(recv_packet.getPort());
					Server_ClientList new_client = new Server_ClientList(recv_packet.getAddress(), recv_packet.getPort());
					client_List.add(new_client);  //Ŭ���̾�Ʈ ����Ʈ�� ���ο� Ŭ���̾�Ʈ �߰�

					String join = "<";
					join += recv_packet.getAddress().toString() + " - ";
					join += String.valueOf(recv_packet.getPort());
					join += "> Join the Chatting.";
					System.out.println(join);       // ���ο� Ŭ���̾�Ʈ�� join������ �˷���
					byte[] join_byte = join.getBytes();
					for (int i = 0; i < join_byte.length; i++) {
						data_Frame[i + 8] = join_byte[i];
					}
				
					Send_to_All(data_Frame, recv_packet.getPort());
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}

	public void Send_to_All(byte[] data, int myport) throws IOException {  // ��� Ŭ���̾�Ʈ���� data ����
		int clListSize = client_List.size();
		for (int i = 0; i < clListSize; i++) {
			InetAddress iAddr = client_List.get(i).inetAddress;
			int Send_portNum = client_List.get(i).portNumber;
			
			DatagramPacket send_Packet = new DatagramPacket(data, data.length, iAddr, Send_portNum);
			Dsocket.send(send_Packet);
		}
	}
}
