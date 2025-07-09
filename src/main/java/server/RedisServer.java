package server;

import file.RdbFileManager;
import file.RdbMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisServer {
    private static final Logger log = LoggerFactory.getLogger(RedisServer.class);

    public static void run(String... args) {
        run(initializeConfig(args));
    }

    private static void run(RedisConfig redisConfig) {
        run(redisConfig, initializeMetadata(redisConfig));
    }

    private static void run(RedisConfig redisConfig, RdbMetadata metadata) {
        run(new MultiThreadRedisServer(redisConfig, new Storage(metadata.getStorage()))); }

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
