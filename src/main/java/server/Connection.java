package server;

import static common.Protocol.ASTERISK_BYTE;
import static common.Protocol.DOLLAR_BYTE;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import util.RedisInputStream;
import util.RedisOutputStream;

public class Connection extends Thread {

    private final Socket clientSocket;
    private final RedisConfig redisConfig;
    private final Storage storage;

    public Connection(Socket clientSocket, RedisConfig redisConfig, Storage storage) {
        this.clientSocket = clientSocket;
        this.redisConfig = redisConfig;
        this.storage = storage;
    }

    private Object process(RedisInputStream redisInputStream) {
        var readByte = redisInputStream.readByte();

        return switch (readByte) {
            // bulk process
            case ASTERISK_BYTE -> processBulkString(redisInputStream);
            case DOLLAR_BYTE -> processString(redisInputStream);
            default -> throw new IllegalStateException("Unexpected value: " + readByte);
        };
    }

    private Object processString(RedisInputStream in) {
        in.readIntCrLf();
        return in.readLine();
    }

    private ArrayList<Object> processBulkString(RedisInputStream in) {
        var size = in.readIntCrLf();
        var ret = new ArrayList<Object>();
        for (int i = 0; i < size; i++) {
            ret.add(process(in));
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    private ResultSet execute(Object r) {
        if (r instanceof List<?>) {
            var commands = (List<String>) r;

            return new CommandRunner(commands, storage, redisConfig).process();
        }
        // catch
        return null;
    }

    @Override
    public void run() {
        try (var redisInputStream = new RedisInputStream(clientSocket.getInputStream());
             var redisOutputStream = new RedisOutputStream(clientSocket.getOutputStream())) {
            while (true) {
                var commands = process(redisInputStream);
                var resultSet = execute(commands);
                redisOutputStream.write(resultSet);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
