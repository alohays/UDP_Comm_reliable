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
				
				if (port_Set.contains(recv_packet.getPort())) {  // 이미 존재하던 클라이언트일 경우
					System.out.println("test1");
					byte[] recv_message = new byte[MAXBUFFER];
					for (int i = 0; i < 500; i++) {  // data_Frame[3]에 메시지의 길이가 담겨 있다
						recv_message[i] = data_Frame[i + 3];   // 수신한 데이터 중 메시지 부분을 저장
					}

					//if (data_Frame[2] < (byte) 0x80) {  
						String message = "from <" + recv_packet.getAddress().toString() + " - ";
						message += String.valueOf(recv_packet.getPort());
						message += "> message: " + new String(recv_message);
						System.out.println(message);
					//}
					Send_to_All(data_Frame, recv_packet.getPort());
				} else {   // 새로 join하는 클라이언트인 경우
					System.out.println("test2");
					port_Set.add(recv_packet.getPort());
					Server_ClientList new_client = new Server_ClientList(recv_packet.getAddress(), recv_packet.getPort());
					client_List.add(new_client);  //클라이언트 리스트에 새로운 클라이언트 추가

					String join = "<";
					join += recv_packet.getAddress().toString() + " - ";
					join += String.valueOf(recv_packet.getPort());
					join += "> Join the Chatting.";
					System.out.println(join);       // 새로운 클라이언트가 join했음을 알려줌
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

	public void Send_to_All(byte[] data, int myport) throws IOException {  // 모든 클라이언트에게 data 전송
		int clListSize = client_List.size();
		for (int i = 0; i < clListSize; i++) {
			InetAddress iAddr = client_List.get(i).inetAddress;
			int Send_portNum = client_List.get(i).portNumber;
			
			DatagramPacket send_Packet = new DatagramPacket(data, data.length, iAddr, Send_portNum);
			Dsocket.send(send_Packet);
		}
	}
}
