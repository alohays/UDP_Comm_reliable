package UDP_Client;

import java.net.*;
import java.io.*;

public class SocketComm_Client {
	final static int MAXBUFFER = 508;
	static InetAddress inetaddr;
	static int port;
	static byte seqNo;
	static boolean ACK;

	public static void main(String[] args) throws InterruptedException {
		if (args.length != 2) {
			System.out.println("Manual: java UDP_Client.SocketComm_Client localhostaddress port");
			System.exit(0);
		}

		try {
			// initialize
			inetaddr = InetAddress.getByName(args[0]);
			port = Integer.parseInt(args[1]);
			DatagramSocket socket = new DatagramSocket();
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			byte buffer[] = new byte[MAXBUFFER];

			DatagramPacket send_packet = new DatagramPacket(buffer, buffer.length, inetaddr, port);
			socket.send(send_packet);   // �����ͱ׷� ����
			DatagramPacket join_Packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(join_Packet);  // �����ͱ׷� ����
			String join_message = buffer.toString();  // ���� ���� �����͸� string ���·� ��ȯ
			System.out.println("join chatting!");  // ���� ���� �������� �տ� 8byte �� ���� ��� (�պκп� seqNo, ackNo, FLAG, CRC ���� �����߱� ����) 

			Client_Receive recv = new Client_Receive(socket);   
			recv.start();   // Ŭ���̾�Ʈ�� receive ��ü ���� �� ����
			
			seqNo = 0;
			byte AckNo = seqNo;
			byte FLAG = 0x7E;

			// Data Sender
			while (true) {
				System.out.println("Input Data: ");
				String data = br.readLine();
				if (data.length() == 0) continue;  // ��ǲ�� ũ�Ⱑ 0�� ���

				buffer = new byte[MAXBUFFER];
				buffer = data.getBytes();

				
				byte[] HDLCframe = HDLC_Frame.makeDataFrame(seqNo, AckNo, FLAG, buffer);  // ������ data�� HDLCframe�� ���·� ����

				
				send_packet = new DatagramPacket(HDLCframe, HDLCframe.length, inetaddr, port);
				socket.send(send_packet);

				// Time out
				ACK = false;
				long start_time = System.currentTimeMillis(); 
				long Limit_resend = 10;   // Ÿ�Ӿƿ��� ��� �������ϴ� Ƚ�� ���� : 10
				while (!ACK && Limit_resend > 0) {
					long pass_time = System.currentTimeMillis() - start_time;
					if (pass_time > 500) {  // Ÿ�Ӿƿ� �ð��� 500���� ����
						System.out.println("Time out!! <"+ (10 - Limit_resend + 1) + ">");
						socket.send(send_packet);

						Limit_resend--;
						start_time = System.currentTimeMillis();
						continue;
					}
					if (Limit_resend == 0) System.out.println("Send fail!!");
				}
			}
		} catch (UnknownHostException ex) {
			System.out.println("Error in the host address");
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
