package server;

import exception.RedisException;
import java.net.Socket;
import java.util.List;
import result.Result;
import server.handler.HandlerMap;
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


//    @SuppressWarnings("unchecked")
//    private ResultSet execute(Object r) {
//        if (r instanceof List<?>) {
//            var commands = new Commands((List<String>) r);
//            ResultSet resultSet = new ResultSet();
//            while (commands.isRemaining()) {
//                var command = Command.from(commands.poll());
//                var handler = HandlerMap.get(command);
//
//                resultSet.add(handler.handle(redisConfig, storage, commands));
//            }
//            return resultSet;
//        }
//        // catch
//        return null;
//    }

    @SuppressWarnings("unchecked")
    private Result execute(Object args){
        if (args instanceof List<?> r) {
            var commands = new Commands((List<String>) r);
            if (commands.isRemaining()) {
                var command = Command.from(commands.poll());
                var handler = HandlerMap.get(command);

                return handler.handle(redisConfig, storage, commands);
            }
        }
        return null;
    }

    @Override
    public void run() {
        try (var redisInputStream = new RedisInputStream(clientSocket.getInputStream());
             var redisOutputStream = new RedisOutputStream(clientSocket.getOutputStream())) {
            // TODO connection closer 추가하기

            while (clientSocket.isConnected()) {
                var commands = ConnectionProtocolParser.process(redisInputStream);
                try {
                    var result = execute(commands);
                    redisOutputStream.write(result);
                } catch (RedisException e) {
                    redisOutputStream.write(e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
