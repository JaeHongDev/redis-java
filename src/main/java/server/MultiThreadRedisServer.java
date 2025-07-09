package server;

import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadRedisServer implements RedisServerListener {

    private final RedisConfig redisConfig;
    private final Storage storage;

    public MultiThreadRedisServer(RedisConfig redisConfig, Storage storage) {
        this.redisConfig = redisConfig;
        this.storage = storage;
    }

    @Override
    public void listen() {
        try (ServerSocket serverSocket = new ServerSocket(redisConfig.port)) {
            serverSocket.setReuseAddress(true);

            Socket clientSocket;
            while ((clientSocket = serverSocket.accept()) != null) {
                var connection = new Connection(clientSocket, redisConfig, storage);
                connection.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
