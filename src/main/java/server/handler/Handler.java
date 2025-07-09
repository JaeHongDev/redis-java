package server.handler;

import result.Result;
import server.Commands;
import server.RedisConfig;
import server.Storage;

public interface Handler {
    Result handle(RedisConfig redisConfig, Storage storage, Commands commands);
}
