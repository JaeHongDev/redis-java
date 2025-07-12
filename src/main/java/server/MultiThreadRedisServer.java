package server;

import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadRedisServer implements RedisServerListener {

    private final RedisConfig redisConfig;
    private final Storage storage;
    private ReplicaManager replicaManager;

    public MultiThreadRedisServer(RedisConfig redisConfig, Storage storage) {
        this.redisConfig = redisConfig;
        this.storage = storage;
    }
    public MultiThreadRedisServer(RedisConfig redisConfig, Storage storage, ReplicaManager replicaManager) {
        this(redisConfig, storage);
        this.replicaManager = replicaManager;
    }

    @Override
    public void listen() {
        try (ServerSocket serverSocket = new ServerSocket(redisConfig.port)) {
            serverSocket.setReuseAddress(true);

            Socket clientSocket;
            while ((clientSocket = serverSocket.accept()) != null) {
                var connection = new Connection(clientSocket, redisConfig, storage, replicaManager);
                connection.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
