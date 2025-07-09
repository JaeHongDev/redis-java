package file;

import server.RedisConfig;
import server.Storage;

public class Study {

    public static void main(String...args) {
        RedisConfig.getInstance().init(
                "--dir", "/Users/jaehong/codecrafts/codecrafters-redis-java",
                "--dbfilename", "dump.rdb"
        );

        var storage = new Storage();
        var rdbFileManager = new RdbFileManager(RedisConfig.getInstance(), storage);

        rdbFileManager.init();
    }
}
