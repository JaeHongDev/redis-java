package server;

import exception.RedisException;
import file.RdbFileManager;
import file.RdbMetadata;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import result.Result;
import server.handler.HandlerMap;
import util.RedisInputStream;
import util.RedisOutputStream;

public class ReplicaClient {
    private static final Logger log = LoggerFactory.getLogger(ReplicaClient.class);
    private final RedisConfig redisConfig;
    private final Socket socket;
    private final RedisInputStream redisInputStream;
    private final RedisOutputStream redisOutputStream;

    public ReplicaClient(RedisConfig redisConfig) {
        try {
            this.redisConfig = redisConfig;
            log.debug("{}", redisConfig);
            this.socket = new Socket(redisConfig.masterHost, redisConfig.masterPort);
            this.redisInputStream = new RedisInputStream(this.socket.getInputStream());
            this.redisOutputStream = new RedisOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public RdbMetadata connect() {
        try {
            redisOutputStream.sendCommand("PING");
            printResponse(redisInputStream);

            redisOutputStream.sendCommand("REPLCONF", "listening-port", String.valueOf(redisConfig.port));
            printResponse(redisInputStream);

            redisOutputStream.sendCommand("REPLCONF", "capa", "psync2");
            printResponse(redisInputStream);

            redisOutputStream.sendCommand("PSYNC", "?", "-1");
            printResponse(redisInputStream);

            var rdbMetadata = RdbFileManager.init(redisInputStream);

            new Thread(() -> processSync(rdbMetadata)).start();
            return rdbMetadata;
        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }

    private static void printResponse(RedisInputStream in) {
        var result = ConnectionProtocolParser.process(in);

        System.out.println(result);
    }

    private void processSync(RdbMetadata rdbMetadata) {
        var storage = rdbMetadata.getStorage();

        while (socket.isConnected()) {
            var commands = ConnectionProtocolParser.process(redisInputStream);
            try {
                var result = execute(commands, new Storage(storage));
                redisOutputStream.write(result);
            } catch (RedisException e) {
                try {
                    redisOutputStream.write(e);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
    @SuppressWarnings("unchecked")
    private Result execute(Object args, Storage storage){
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

}
