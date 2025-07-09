package server;

import java.util.Arrays;

public enum Command {
    PING, ECHO, SET, GET, CONFIG, KEYS;

    public static Command from(String command) {
        final var upperCommand = command.toUpperCase();

        return Arrays.stream(values()).filter(v -> v.name().equals(upperCommand))
                .findFirst()
                .orElseThrow();
    }
}
