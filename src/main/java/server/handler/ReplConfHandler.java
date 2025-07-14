package server.handler;

import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import result.ArrayResult;
import result.Result;
import result.SingleResult;
import server.Commands;
import server.RedisConfig;
import server.Storage;

public class ReplConfHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(ReplConfHandler.class);

    @Override
    public Result handle(RedisConfig redisConfig, Storage storage, Commands commands) {

        var arg = commands.poll();

        if(Objects.equals(arg, "getack")) {
            return new ArrayResult(List.of("REPLCONF", "ACK", "0"));
        }

        while (commands.isRemaining()) {
            log.info("command >> {}", commands.poll());
        }
        return new SingleResult("OK");
    }
}
