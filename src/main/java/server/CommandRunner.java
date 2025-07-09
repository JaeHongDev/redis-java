package server;

import exception.RedisException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import result.ArrayResult;
import result.Result;
import util.PatternMatcher;
import result.SingleResult;

public class CommandRunner {

    private static final Logger log = LoggerFactory.getLogger(CommandRunner.class);
    private final List<String> commands;
    private final Storage storage;
    private final RedisConfig redisConfig;
    private int offset;
    private int limit;

    public CommandRunner(List<String> commands, Storage storage, RedisConfig redisConfig) {
        this.commands = commands;
        this.offset = 0;
        this.limit = commands.size();
        this.storage = storage;
        this.redisConfig = redisConfig;
    }

    public ResultSet process(){
        List<Result> results = new ArrayList<>();
        log.debug("command list {}", commands);

        while(offset < limit) {
            var command = commands.get(offset++).toLowerCase();

            switch (command) {
                case "ping" -> results.add(new SingleResult("PONG"));
                case "echo" -> results.add(new SingleResult(commands.get(offset++)));
                case "set" -> {
                    var key = commands.get(offset++);
                    var value = commands.get(offset++);

                    if (offset < limit) {
                        if (commands.get(offset).equalsIgnoreCase("px")) {
                            var milliseconds = Integer.parseInt(commands.get(++offset));
                            offset++;
                            storage.put(key, value, milliseconds);
                            results.add(new SingleResult("OK"));
                        }
                    } else {
                        storage.put(key, value);
                        results.add(new SingleResult("OK"));
                    }
                    System.out.println("check");
                }
                case "get" -> {
                    var key = commands.get(offset++);
                    results.add(new SingleResult(storage.get(key)));
                }
                case "config" -> {
                    var arg = commands.get(offset++).toLowerCase();
                    var sub = commands.get(offset++).toLowerCase();

                    if(Objects.equals(arg.toLowerCase(), "get")){

                        if(Objects.equals(sub, "dir")) {
                            results.add(new SingleResult(sub));
                            results.add(new SingleResult(redisConfig.dir));
                        }
                        else if(Objects.equals(sub, "dbfilename")) {
                            results.add(new SingleResult(sub));
                            results.add(new SingleResult(redisConfig.dbFileName));
                        }
                        // TODO add
                    }
                }
                case "keys" -> {
                    var arg = commands.get(offset++);

                    if(commands.size() > 2) {
                        throw new RedisException("ERR wrong number of arguments for 'keys' command");
                    }
                    if(arg.contains("*")) {
                        var regex = PatternMatcher.globToRegex(arg);
                        var result = storage.findKey(regex);

                        System.out.println("search pattern:" + regex);
                        System.out.println(result);

                        results.add(new ArrayResult(result.stream().map(Value::unwrap).toList()));

                    }else{
                        results.add(new SingleResult(storage.get(arg)));
                    }
                }
            }
        }
        return new ResultSet(results);
    }
}
