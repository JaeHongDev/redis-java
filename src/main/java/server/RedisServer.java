package server;

import file.RdbFileManager;
import file.RdbMetadata;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.RedisInputStream;
import util.RedisOutputStream;

public class RedisServer {
    private static final Logger log = LoggerFactory.getLogger(RedisServer.class);

    public static void run(String... args) {
        run(initializeConfig(args));
    }

    private static void run(RedisConfig redisConfig) {
        run(redisConfig, initializeMetadata(redisConfig));
    }

    private static void run(RedisConfig redisConfig, RdbMetadata metadata) {
        handShake(redisConfig);
        run(new MultiThreadRedisServer(redisConfig, new Storage(metadata.getStorage())));
    }

    // TODO 이거 이름 바꿔야 할듯..?
    private static void handShake(RedisConfig redisConfig) {
        if (Objects.equals(redisConfig.role, "master")) {
            return;
        }

        try (Socket socket = new Socket(redisConfig.masterHost, redisConfig.masterPort);
             var in = new RedisInputStream(socket.getInputStream());
             var out = new RedisOutputStream(socket.getOutputStream())
        ) {
            out.sendCommand("PING");
            printResponse(in);

            out.sendCommand("REPLCONF", "listening-port", String.valueOf(redisConfig.port));
            printResponse(in);

            out.sendCommand("REPLCONF", "capa", "psync2");
            printResponse(in);

            out.sendCommand("PSYNC", "?", "-1");
            printResponse(in);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void printResponse(RedisInputStream in) {
        var result = ConnectionProtocolParser.process(in);

        System.out.println(result);
    }

    private static void sendCommand(BufferedWriter writer, String... args) throws IOException {
        writer.write("*" + args.length + "\r\n");
        for (String arg : args) {
            writer.write("$" + arg.length() + "\r\n");
            writer.write(arg + "\r\n");
        }
        writer.flush();
    }

    private static void printResponse(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            throw new IOException("Redis 응답 없음 (소켓 끊김)");
        }
        System.out.println("Redis 응답: " + line);
    }


    private static void run(RedisServerListener listener) {
        log.debug("redis server run");
        listener.listen();
    }

    private static RdbMetadata initializeMetadata(RedisConfig redisConfig) {
        return RdbFileManager.init(redisConfig);
    }

    private static RedisConfig initializeConfig(String... args) {
        return RedisConfig.create(args);
    }

}
