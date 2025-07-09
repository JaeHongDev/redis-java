package server.handler;

import result.Result;
import result.SingleResult;
import server.Commands;
import server.RedisConfig;
import server.Storage;

public class GetHandler implements Handler {
    @Override
    public Result handle(RedisConfig redisConfig, Storage storage, Commands commands) {
        var key = commands.poll();
        return new SingleResult(storage.get(key));
    }
}
