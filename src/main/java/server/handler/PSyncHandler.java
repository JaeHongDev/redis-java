package server.handler;

import file.Rdb;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import result.BinaryBulkStringResult;
import result.BulkStringResult;
import result.Result;
import result.SingleResult;
import server.Commands;
import server.RedisConfig;
import server.Storage;

public class PSyncHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(PSyncHandler.class);

    @Override
    public Result handle(RedisConfig redisConfig, Storage storage, Commands commands) {
        while(commands.isRemaining()){
            log.debug("{}", commands.poll());
        }

        return new BinaryBulkStringResult(
                "FULLRESYNC 8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb 0",
                Rdb.EMPTY_FILE
        );
    }
}
