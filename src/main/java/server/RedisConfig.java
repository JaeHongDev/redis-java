package server;

import java.io.File;

// singleton object
public class RedisConfig {

    private static final RedisConfig INSTANCE = new RedisConfig();

    public int port = 6379;
    public String dir = "/tmp/redis-data";
    public String dbFileName = "rdbfile";

    private RedisConfig() {
    }

    public static RedisConfig create(String[] args) {
        var redisConfig = new RedisConfig();

        redisConfig.init(args);

        return redisConfig;
    }

    public void init(String... args) {
        var size = args.length;
        var index = 0;

        while (index < size) {
            if (args[index].startsWith("--")) {
                var option = args[index].substring(2);
                var value = args[index + 1];

                switch (option) {
                    case "port" -> port = Integer.parseInt(value);
                    case "dir" -> dir = value;
                    case "dbfilename" -> dbFileName = value;
                }
                index += 2;
            }
            // ignore
            else {
                index++;
            }
        }
    }

    public static RedisConfig getInstance() {
        return INSTANCE;
    }

    public File getRdbFile() {
        return new File(dir + "/" + dbFileName);
    }
}
