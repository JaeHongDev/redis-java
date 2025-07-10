package server;

import static common.Protocol.ASTERISK_BYTE;
import static common.Protocol.COLON_BYTE;
import static common.Protocol.DOLLAR_BYTE;
import static common.Protocol.MINUS_BYTE;
import static common.Protocol.PLUS_BYTE;

import java.io.IOException;
import java.util.ArrayList;
import util.Printer;
import util.RedisInputStream;

public class ConnectionProtocolParser {

    public static Object process(RedisInputStream redisInputStream) {
        var readByte = redisInputStream.readByte();
        return switch (readByte) {
            case ASTERISK_BYTE -> processBulkString(redisInputStream);
            case DOLLAR_BYTE -> processString(redisInputStream);
            case PLUS_BYTE -> redisInputStream.readLine();
            case MINUS_BYTE, COLON_BYTE -> throw new UnsupportedOperationException();
            default -> throw new IllegalStateException("Unexpected value: " + readByte);
        };
    }

    private static Object processString(RedisInputStream in) {
        in.readIntCrLf();
        return in.readLine();
    }
    // todo number 생각하기
    private static ArrayList<Object> processBulkString(RedisInputStream in) {
        var size = in.readIntCrLf();
        var ret = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ret.add(process(in));
        }
        return ret;
    }
}
