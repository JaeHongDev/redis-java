package server.handler;

import result.Result;
import result.SimpleStringResult;
import result.SingleResult;
import server.Commands;
import server.RedisConfig;
import server.Storage;

public class EchoHandler implements Handler {

    @Override
    public Result handle(RedisConfig redisConfig, Storage storage, Commands commands) {
        return new SimpleStringResult(commands.poll());
    }
}
