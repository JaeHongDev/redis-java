package server;

import java.io.File;

// singleton object
public class RedisConfig {

    private static final RedisConfig INSTANCE = new RedisConfig();

    public int port = 6379;
    public String dir = "/tmp/redis-data";
    public String dbFileName = "rdbfile";


    // replica options
    // @formatter:off
    public String role                       = "master"; // default
    public int    connectedSlaves            = 0;
    public String masterReplId               = "8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb";
    public int    masterReplOffset           = 0;
    public int    secondReplOffset           = -1;
    public int    replBacklogActive          = 0;
    public int    replBacklogSize            = 1048576;
    public int    replBacklogFirstByteOffset = 0;
    public int    replBacklogHistlen         = 0;
    // @formatter:on

    public String masterHost;
    public int masterPort;

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
                    case "replicaof" -> setReplica(value);
                }
                index += 2;
            }
            // ignore
            else {
                index++;
            }
        }
    }

    private void setReplica(String value) {
        // <host> <port>
        this.role = "slave";

        var split = value.split(" ");
        this.masterHost = split[0];
        this.masterPort = Integer.parseInt(split[1]);
    }

    public static RedisConfig getInstance() {
        return INSTANCE;
    }

    public File getRdbFile() {
        return new File(dir + "/" + dbFileName);
    }
}
