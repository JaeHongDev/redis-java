package server;

import file.RdbFileManager;

public class RedisServer {

    public static void run(String...args) {
        final var redisConfig = RedisConfig.getInstance();
        redisConfig.init(args);

        var storage = init(redisConfig);

        run(new MultiThreadRedisServer(redisConfig, storage));
    }

    private static Storage init(RedisConfig redisConfig) {
        var storage = new Storage();
        var fileManager = new RdbFileManager(redisConfig, storage);

        fileManager.init();
        return storage;
    }

    private static void run(RedisServerListener listener) {
        listener.listen();
    }

}
