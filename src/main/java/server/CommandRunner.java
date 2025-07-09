package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import util.PatternMatcher;

public class CommandRunner {

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
        System.out.println("check");
        while(offset < limit) {
            var command = commands.get(offset++).toLowerCase();

            switch (command) {
                case "ping" -> results.add(new Result("PONG"));
                case "echo" -> results.add(new Result(commands.get(offset++)));
                case "set" -> {
                    var key = commands.get(offset++);
                    var value = commands.get(offset++);

                    if (offset < limit) {
                        if (commands.get(offset).equalsIgnoreCase("px")) {
                            var milliseconds = Integer.parseInt(commands.get(++offset));
                            offset++;
                            storage.put(key, value, milliseconds);
                            results.add(new Result("OK"));
                        }
                    } else {
                        storage.put(key, value);
                        results.add(new Result("OK"));
                    }
                    System.out.println("check");
                }
                case "get" -> {
                    var key = commands.get(offset++);
                    results.add(new Result(storage.get(key)));
                }
                case "config" -> {
                    var arg = commands.get(offset++).toLowerCase();
                    var sub = commands.get(offset++).toLowerCase();

                    if(Objects.equals(arg.toLowerCase(), "get")){

                        if(Objects.equals(sub, "dir")) {
                            results.add(new Result(sub));
                            results.add(new Result(redisConfig.dir));
                        }
                        else if(Objects.equals(sub, "dbfilename")) {
                            results.add(new Result(sub));
                            results.add(new Result(redisConfig.dbFileName));
                        }
                        // TODO add
                    }
                }
                case "keys" -> {
                    var arg = commands.get(offset++);
                    if(arg.contains("*")) {
                        var regex = PatternMatcher.globToRegex(arg);
                        var result = storage.findKey(regex);

                        System.out.println(result);
                        result.forEach(value -> results.add(new Result(value.unwrap())));
                    }else{
                        results.add(new Result(storage.get(arg)));
                    }

                }
            }
        }
        return new ResultSet(results);
    }
}
