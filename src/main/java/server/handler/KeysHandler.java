package server.handler;

import exception.RedisException;
import result.ArrayResult;
import result.Result;
import result.SingleResult;
import server.Commands;
import server.RedisConfig;
import server.Storage;
import server.Value;
import util.PatternMatcher;

public class KeysHandler implements Handler {

    @Override
    public Result handle(RedisConfig redisConfig, Storage storage, Commands commands) {
        var arg = commands.poll();

        if(commands.size() >= 2) {
            throw new RedisException("ERR wrong number of arguments for 'keys' command");
        }
        if(arg.contains("*")) {
            var regex = PatternMatcher.globToRegex(arg);
            var result = storage.findKey(regex);

            System.out.println("search pattern:" + regex);
            System.out.println(result);

            return new ArrayResult(result.stream().map(Value::unwrap).toList());

        }else{
            return new SingleResult(storage.get(arg));
        }
    }
}
