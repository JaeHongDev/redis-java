package server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import result.Result;
import result.SingleResult;
import server.Commands;
import server.RedisConfig;
import server.Storage;

public class ReplConfHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(ReplConfHandler.class);

    @Override
    public Result handle(RedisConfig redisConfig, Storage storage, Commands commands) {
        while (commands.isRemaining()) {
            log.info("command >> {}", commands.poll());
        }
        return new SingleResult("OK");
    }
}
