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
			socket.send(send_packet);   // 데이터그램 전송
			DatagramPacket join_Packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(join_Packet);  // 데이터그램 수신
			String join_message = buffer.toString();  // 전달 받은 데이터를 string 형태로 변환
			System.out.println("join chatting!");  // 전달 받은 데이터의 앞에 8byte 뒤 부터 출력 (앞부분에 seqNo, ackNo, FLAG, CRC 등을 저장했기 때문) 

			Client_Receive recv = new Client_Receive(socket);   
			recv.start();   // 클라이언트의 receive 객체 생성 및 실행
			
			seqNo = 0;
			byte AckNo = seqNo;
			byte FLAG = 0x7E;

			// Data Sender
			while (true) {
				System.out.println("Input Data: ");
				String data = br.readLine();
				if (data.length() == 0) continue;  // 인풋의 크기가 0인 경우

				buffer = new byte[MAXBUFFER];
				buffer = data.getBytes();

				
				byte[] HDLCframe = HDLC_Frame.makeDataFrame(seqNo, AckNo, FLAG, buffer);  // 전송할 data를 HDLCframe의 형태로 만듬

				
				send_packet = new DatagramPacket(HDLCframe, HDLCframe.length, inetaddr, port);
				socket.send(send_packet);

				// Time out
				ACK = false;
				long start_time = System.currentTimeMillis(); 
				long Limit_resend = 10;   // 타임아웃일 경우 재전송하는 횟수 제한 : 10
				while (!ACK && Limit_resend > 0) {
					long pass_time = System.currentTimeMillis() - start_time;
					if (pass_time > 500) {  // 타임아웃 시간을 500으로 설정
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
