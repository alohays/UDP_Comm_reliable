package UDP_Client;

import java.net.*;
import java.util.zip.CRC32;
import java.io.*;

public class Client_Receive extends Thread {
	final static int MAXBUFFER = 508;
	DatagramSocket Dsocket;
	static byte Ack_Number = 1;

	Client_Receive(DatagramSocket s) {
		Dsocket = s;
	}

	public void run() {

		while (true) {
			try {
				byte buffer[] = new byte[MAXBUFFER];
				DatagramPacket recv_packet = new DatagramPacket(buffer, buffer.length);
				Dsocket.receive(recv_packet);

				byte recv_control = buffer[2];
				byte recv_AckNo = buffer[1];
				byte recv_FLAG = buffer[0];
				int recv_length = 500;
				int recv_controlI = (int) recv_control;
				
				System.out.println(recv_control);
				if (recv_controlI < 0x80) {
					System.out.println("test1");
					byte[] CRC = new byte[4], data = new byte[recv_length];
					for (int i = 0; i < 4; i++) {
						CRC[i] = buffer[i + 503];
					}
					for (int i = 0; i < 500; i++) {
						data[i] = buffer[i + 3];
					}
					System.out.println(new String(data));

					//CRC test
					CRC32 CRC_ck = new CRC32();
					CRC_ck.reset();
					CRC_ck.update(data);
					
					int crc_Value = (int) TypeCast.bytesToLong(CRC);	
					int ck_Value = (int) CRC_ck.getValue();
					byte[] ackcrcByte = TypeCast.longToByte(ck_Value);
				
					if (crc_Value != ck_Value) {  // crc 蔼 眉农
						System.out.println("CRC ERROR");

						byte[] NakFrame = HDLC_Frame.makeAckFrame((byte) 0x7E,
								(byte) 0, (byte) 0xFF, ackcrcByte);
						DatagramPacket Nak_Packet = new DatagramPacket(
								NakFrame, NakFrame.length,
								SocketComm_Client.inetaddr,
								SocketComm_Client.port);

						Dsocket.send(Nak_Packet);

						continue;
					}
					/*if (recv_control != Ack_Number) {
						System.out.println("ACK number ERROR " + Ack_Number);
						continue;
					}*/
					//System.out.println("NO ACK number ERROR");
					Ack_Number++;

					// Ack Frame 积己
					byte[] AckFrame = HDLC_Frame.makeAckFrame((byte) 0x7E, Ack_Number, (byte) 0x80, ackcrcByte);
					DatagramPacket Ack_Packet = new DatagramPacket(AckFrame,
							AckFrame.length, SocketComm_Client.inetaddr,
							SocketComm_Client.port);

					//Ack Frame 傈价
					Dsocket.send(Ack_Packet);
				}else if (recv_controlI ==  0xFF) {
					System.out.println("Nak receive complete");
				} 
				else if (recv_controlI >=  0x80) {
					
					SocketComm_Client.seqNo = recv_AckNo;
					SocketComm_Client.ACK = true;
				} 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
