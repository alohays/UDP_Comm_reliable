package UDP_Client;

import java.util.zip.CRC32;

public class HDLC_Frame {
	public static byte[] makeDataFrame(byte seqNo, byte AckNo, byte FLAG, byte[] data) {  //  HDLC 메시지프레임 (I, U format)
		byte[] ndata = new byte[500];
		for(int i=0;i<data.length;i++){
			ndata[i] = data[i];
		}
		for(int i=data.length;i<500;i++){
			ndata[i] = 0;
		}
		CRC32 crc = new CRC32();
		crc.reset();
		crc.update(ndata);
		int crclen = (int) crc.getValue();

		byte[] crcByte = TypeCast.longToByte(crclen);
		byte[] Frame = new byte[508];   // 전달받은 데이터의 크기보다 8byte를 더 주어야 한다 (seqNo, AckNo, FLAG, crc 때문)

		/*Frame[0] = seqNo;
		Frame[1] = AckNo;
		Frame[2] = FLAG;
		Frame[3] = (byte) data.length;

		for (int i = 0; i < 4; i++) {
			Frame[i+4] = crcByte[i];      // 4byte 크기의 crc 저장
		}*/
		Frame[0] = FLAG;
		Frame[1] = AckNo;
		Frame[2] = seqNo;
		
		for (int i = 3; i < 503; i++) {
			Frame[i] = ndata[i - 3];
		}   // 전달받은 데이터를 Frame에 저장
		for (int i=0; i< 4;i++){
			Frame[i+503] = crcByte[i];
		}
		Frame[507] = FLAG;

		return Frame;
	}

	public static byte[] makeAckFrame(byte FLAG, byte AckNo, byte seqNo, byte[] crcByte) {   // S Format의 ACK 메시지프레임
		byte[] AckFrame = new byte[508];
		/*AckFrame[0] = seqNo;
		AckFrame[1] = AckNo;
		AckFrame[2] = FLAG;
		AckFrame[3] = 0;*/
		AckFrame[0] = FLAG;
		AckFrame[1] = AckNo;
		AckFrame[2] = seqNo;
		for(int i=3;i<503;i++){
			AckFrame[i] = 0;
		}
		for(int i=0;i<4;i++){
			AckFrame[i+503] = crcByte[i];
		}
		AckFrame[507] = FLAG;

		return AckFrame;
	}
}
