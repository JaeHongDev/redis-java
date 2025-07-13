package server.connection;

import java.net.Socket;
import server.RedisConfig;
import server.Storage;

public sealed class Connection permits ClientConnection, ReplicaConnection{

    protected final Socket clientSocket;
    protected final RedisConfig redisConfig;
    protected final Storage storage;

    protected Connection(Socket clientSocket, RedisConfig redisConfig, Storage storage) {
        this.clientSocket = clientSocket;
        this.redisConfig = redisConfig;
        this.storage = storage;
    }
}
