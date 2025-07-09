package util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class RedisRdbInputStream extends BufferedInputStream {

    public RedisRdbInputStream(InputStream in) {
        super(in);
    }

    public String readString(int size) throws IOException {
        return new String(this.readNBytes(size), StandardCharsets.UTF_8);
    }

    // 바이트 수만큼 읽어서 long으로 반환
    public long readBytes(int numBytes, ByteOrder byteOrder) throws IOException {
        byte[] buf = new byte[numBytes];
        int read = this.read(buf);
        if (read != numBytes) {
            throw new IOException("Stream ended early: expected " + numBytes + " bytes, got " + read);
        }
        long result = 0;
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int i = 0; i < numBytes; i++) {
                result = (result << 8) | (buf[i] & 0xFF);
            }
        } else {
            for (int i = numBytes - 1; i >= 0; i--) {
                result = (result << 8) | (buf[i] & 0xFF);
            }
        }
        return result;
    }

    public int readInt8(ByteOrder byteOrder) throws IOException {
        return (int) readBytes(1, byteOrder);
    }

    public int readInt16(ByteOrder byteOrder) throws IOException {
        return (int) readBytes(2, byteOrder);
    }

    public int readInt32(ByteOrder byteOrder) throws IOException {
        return (int) readBytes(4, byteOrder);
    }

    public long readInt64(ByteOrder byteOrder) throws IOException {
        return readBytes(8, byteOrder);
    }

    public byte readByte() throws IOException {
        return (byte) read();
    }
}
