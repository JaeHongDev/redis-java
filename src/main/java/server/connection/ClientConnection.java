package server.connection;

import java.net.Socket;
import server.RedisConfig;
import server.Storage;

public final class ClientConnection extends Connection {
    public ClientConnection(Socket clientSocket, RedisConfig redisConfig, Storage storage) {
        super(clientSocket, redisConfig, storage);
    }
}
