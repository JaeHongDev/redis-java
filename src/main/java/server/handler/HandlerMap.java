package server.handler;

import java.util.HashMap;
import java.util.Map;
import server.Command;

public class HandlerMap {

    private static final Map<Command, Handler> MAP = new HashMap<>() {{
        this.put(Command.PING, new PingHandler());
        this.put(Command.ECHO, new EchoHandler());
        this.put(Command.SET, new SetHandler());
        this.put(Command.GET, new GetHandler());
        this.put(Command.CONFIG, new ConfigHandler());
        this.put(Command.KEYS, new KeysHandler());
        this.put(Command.INFO, new InfoHandler());
        this.put(Command.REPLCONF, new ReplConfHandler());
        this.put(Command.PSYNC, new PSyncHandler());
    }};

    public static Handler get(Command command) {
        return MAP.get(command);
    }

}
