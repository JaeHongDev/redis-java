package util;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RedisInputStream extends FilterInputStream {

    private static final int INPUT_BUFFER_SIZE = Integer.parseInt(
            System.getProperty("redis.bufferSize.input", System.getProperty("redis.bufferSize", "8192"))
    );

    protected final byte[] buf;
    protected int count;
    protected int limit;


    public RedisInputStream(InputStream in, int size) {
        super(in);
        if (size <= 0) {
            throw new IllegalArgumentException("buffer size <= zero");
        }
        this.buf = new byte[size];

    }

    public RedisInputStream(InputStream in) {
        this(in, INPUT_BUFFER_SIZE);
    }

    public byte readByte() {
        ensureFill();
        return buf[count++];
    }

    public String readLine() {
        var sb = new StringBuilder();
        while (true) {
            ensureFill();

            byte b = buf[count++];

            // ?
            if (b == '\r') {
                ensureFill(); // must be one more byte

                byte c = buf[count++];
                if (c == '\n') {
                    break;
                }

                sb.append((char) b);
                sb.append((char) c);
            } else {
                sb.append((char) b);
            }
        }

        var reply = sb.toString();
        if (reply.isEmpty()) {
            throw new IllegalStateException("it seems like server has closed");
        }
        return reply;
    }

    public byte[] readLineBytes() {
        ensureFill();

        int pos = count;
        final byte[] buf = this.buf;

        while (true) {
            if (pos == limit) {
                return readLineByesSlowly();
            }

            if (buf[pos++] == '\r') {
                if (pos == limit) {
                    return readLineByesSlowly();
                }

                if (buf[pos++] == '\n') {
                    break;
                }
            }
        }

        final int N = (pos - count) - 2;
        final byte[] line = new byte[N];
        System.arraycopy(buf, count, line, 0, N);
        count = pos;

        return line;
    }

    private byte[] readLineByesSlowly() {
        ByteArrayOutputStream bout = null;

        while (true) {
            ensureFill();

            var b = buf[count++];
            if (b == '\r') {
                ensureFill();

                byte c = buf[count++];
                if (c == '\n') {
                    break;
                }

                if (bout == null) {
                    bout = new ByteArrayOutputStream(16);
                }

                bout.write(b);
                bout.write(c);
            } else {
                if (bout == null) {
                    bout = new ByteArrayOutputStream(16);
                }
                bout.write(b);
            }
        }
        return bout == null ? new byte[0] : bout.toByteArray();
    }

    public int readIntCrLf() {
        return (int) readLongCrlf();
    }

    public long readLongCrlf() {
        final byte[] buf = this.buf;

        ensureFill();
        ;

        final boolean isNeg = buf[count] == '-';

        if (isNeg) {
            ++count;
        }

        long value = 0;
        while (true) {
            ensureFill();

            final int b = buf[count++];

            if (b == '\r') {
                ensureFill();

                if (buf[count++] != '\n') {
                    throw new IllegalArgumentException("Unexpected character!");
                }

                break;
            } else {
                value = value * 10 + b - '0';
            }
        }
        return (isNeg ? -value : value);
    }

    @Override
    public int read(byte[] b, int off, int len) {
        ensureFill();

        var length = Math.min(limit - count, len);
        System.arraycopy(buf, count, b, off, length);
        count += length;
        return length;
    }

    private void ensureFill() {
        if (count >= limit) {
            try {
                limit = in.read(buf);
                count = 0;
                // eol 체크
                if (limit == -1) {
                    throw new IllegalStateException("unexpected end of stream.");
                }
            } catch (IOException e) {
                throw new IllegalStateException();
            }
        }
    }

}
