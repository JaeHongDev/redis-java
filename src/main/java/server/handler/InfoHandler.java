package server.handler;

import result.BulkStringResult;
import result.Result;
import result.SingleResult;
import server.Commands;
import server.RedisConfig;
import server.Storage;

public class InfoHandler implements Handler {

    @Override
    public Result handle(RedisConfig redisConfig, Storage storage, Commands commands) {
        var replication = commands.poll();

        return new SingleResult(String.format("""
                # Replication
                role:%s
                connected_slaves:0
                master_replid:8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb
                master_repl_offset:0
                second_repl_offset:-1
                repl_backlog_active:0
                repl_backlog_size:1048576
                repl_backlog_first_byte_offset:0
                repl_backlog_histlen:0
                """, redisConfig.role
        ));
    }
}
