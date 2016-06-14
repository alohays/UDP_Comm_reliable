package UDP_Client;

import java.nio.ByteBuffer;

public class TypeCast {
	static public byte[] longToByte(int x) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(x);
		return buffer.array();
	}

	static public long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.put(bytes);
		buffer.flip();
		return buffer.getInt();
	}
}
