package file;

import server.RedisConfig;

public class Study {

    public static void main(String...args) {
        RedisConfig.getInstance().init(
                "--dir", "/Users/jaehong/codecrafts/codecrafters-redis-java",
                "--dbfilename", "dump.rdb"
        );

        RdbFileManager.init(RedisConfig.getInstance());
    }
}
