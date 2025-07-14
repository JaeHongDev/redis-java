package server;

import file.RdbFileManager;
import file.RdbMetadata;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisServer {
    private static final Logger log = LoggerFactory.getLogger(RedisServer.class);

    public static void run(String... args) {
        run(initializeConfig(args));
    }

    private static void run(RedisConfig redisConfig) {
        if (Objects.equals(redisConfig.role, "slave")) {
            var replicaClient = new ReplicaClient(redisConfig);
            var storage = new Storage(replicaClient.connect().getStorage());
            run(new MultiThreadRedisServer(redisConfig, storage));
            return;
        }
        run(redisConfig, initializeMetadata(redisConfig));
    }

    private static void run(RedisConfig redisConfig, RdbMetadata metadata) {
        run(new MultiThreadRedisServer(redisConfig, new Storage(metadata.getStorage()), new ReplicaManager()));
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
