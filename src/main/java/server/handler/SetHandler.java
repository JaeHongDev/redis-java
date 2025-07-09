package server.handler;

import result.Result;
import result.SingleResult;
import server.Commands;
import server.RedisConfig;
import server.Storage;

public class SetHandler implements Handler {
    @Override
    public Result handle(RedisConfig redisConfig, Storage storage, Commands commands) {
        var key = commands.poll();
        var value = commands.poll();

        if (commands.isRemaining()) {
            if (commands.peek().equalsIgnoreCase("px")) {
                // px 꺼내기
                commands.poll();
                var milliseconds = Integer.parseInt(commands.poll());
                storage.put(key, value, milliseconds);
                return new SingleResult("OK");
            }
        }

        storage.put(key, value);
        return new SingleResult("OK");
    }
}
