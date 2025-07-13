package server;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplicaManager {
    private static final Logger log = LoggerFactory.getLogger(ReplicaManager.class);
    private final Set<Connection> replicas = new HashSet<>();

    public void add(Connection connection) {
        replicas.add(connection);
    }

    public void propagate(Commands commands) {
        replicas.forEach(connection -> {
            try {
                connection.send(commands);
            } catch (Exception e) {
                log.error("{}", e.getMessage());
            }
        });
    }
}
