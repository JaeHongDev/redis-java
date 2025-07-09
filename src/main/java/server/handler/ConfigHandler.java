package server.handler;

import java.util.Objects;
import result.BulkStringResult;
import result.Result;
import server.Commands;
import server.RedisConfig;
import server.Storage;

public class ConfigHandler implements Handler{

    @Override
    public Result handle(RedisConfig redisConfig, Storage storage, Commands commands) {

        var arg = commands.poll().toLowerCase();
        var sub = commands.poll().toLowerCase();

        if(Objects.equals(arg.toLowerCase(), "get")){
            if(Objects.equals(sub, "dir")) {
                return new BulkStringResult(sub, redisConfig.dir);
            }
            else if(Objects.equals(sub, "dbfilename")) {
                return new BulkStringResult(sub, redisConfig.dbFileName);
            }
            // TODO add
        }
        return null;
    }
}
