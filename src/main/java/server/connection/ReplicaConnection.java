package server.connection;

import java.net.Socket;
import server.RedisConfig;
import server.Storage;

public final class ReplicaConnection extends Connection {

    public ReplicaConnection(Connection connection) {
        super(connection.clientSocket, connection.redisConfig, connection.storage);
    }

    public void send() {
    }
}
