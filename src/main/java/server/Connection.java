package server;

import exception.RedisException;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import result.Result;
import server.handler.HandlerMap;
import util.RedisInputStream;
import util.RedisOutputStream;

public class Connection extends Thread {

    private final Socket clientSocket;
    private final RedisConfig redisConfig;
    private final Storage storage;
    private final ReplicaManager replicaManager;

    private final RedisInputStream redisInputStream;
    private final RedisOutputStream redisOutputStream;

    public Connection(Socket clientSocket, RedisConfig redisConfig, Storage storage, ReplicaManager replicaManager) {
        this.clientSocket = clientSocket;
        this.redisConfig = redisConfig;
        this.storage = storage;
        this.replicaManager = replicaManager;

        try {
            this.redisInputStream = new RedisInputStream(clientSocket.getInputStream());
            this.redisOutputStream = new RedisOutputStream(clientSocket.getOutputStream());
        }catch (Exception e) {
            throw new IllegalStateException();
        }
    }

    @SuppressWarnings("unchecked")
    private Result execute(Object args){
        if (args instanceof List<?> r) {
            var commands = new Commands((List<String>) r);
            if (commands.isRemaining()) {
                var command = Command.from(commands.poll());
                var handler = HandlerMap.get(command);

                var result = handler.handle(redisConfig, storage, commands);

                if(command == Command.PSYNC) {
                    replicaManager.add(this);
                }

                if(command == Command.SET) {
                    replicaManager.propagate(commands);
                }

                return result;
            }
        }
        return null;
    }

    @Override
    public void run() {
        try {
            while (clientSocket.isConnected()) {
                var commands = ConnectionProtocolParser.process(redisInputStream);
                try {
                    var result = execute(commands);
                    redisOutputStream.write(result);
                } catch (RedisException e) {
                    redisOutputStream.write(e);
                }
            }
        }catch (Exception e) {
            // ignore
        }
    }

    public void send(Commands commands) throws IOException {
        var input = commands.getStrings();

        redisOutputStream.write("*" + input.size());
        for(var c: input){
            redisOutputStream.write("$" + c.length());
            redisOutputStream.write(c);
        }
        redisOutputStream.flush();
    }
}
