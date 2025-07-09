package server;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class Storage {
    private final Map<String, Value> map;

    public Storage(Map<String, Value> map) {
        this.map = map;
    }

    public void put(String key, String value) {
        map.put(key, Value.create(value));
    }

    public void put(String key, String value, int milliSeconds) {
        map.put(key, Value.create(value, milliSeconds));
    }

    public String get(String key) {
        return Optional.ofNullable(map.get(key))
                .map(Value::unwrap)
                .orElse(null);
    }

    public List<Value> findKey(Pattern pattern) {
        System.out.println(map.keySet());
        return map.keySet()
                .stream().filter(value -> pattern.matcher(value).matches())
                .map(Value::create)
                .toList();
    }
}
