package server;

import java.time.Instant;

public class Value {
    private final String value;
    private State state;
    private Instant createdAt;
    private Instant expiredAt;

    public Value(String value) {
        this.value = value;
    }

    public static Value create(String value) {
        var v = new Value(value);

        v.state = State.NONE;

        return v;
    }

    public static Value create(String value, int milliSeconds) {
        var v = new Value(value);

        v.state = State.EXPIRE;
        v.createdAt = Instant.now();
        v.expiredAt = v.createdAt.plusMillis(milliSeconds);

        return v;
    }

    public String unwrap() {
        return switch (state) {
            case State.NONE -> value;
            case State.EXPIRE -> expiredAt.isAfter(Instant.now()) ? value : null;
        };
    }

    enum State {
        EXPIRE, NONE
    }

    @Override
    public String toString() {
        return "Value{" +
                "value='" + value + '\'' +
                ", state=" + state +
                ", createdAt=" + createdAt +
                ", expiredAt=" + expiredAt +
                '}';
    }
}
