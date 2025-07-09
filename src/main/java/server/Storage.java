package server;

import java.io.LineNumberInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;

public class Storage {
    private Map<String, Value> map = new HashMap<>();

    private static final Storage storage = new Storage();

    public void put(String key, String value){
        map.put(key, Value.create(value));
    }

    public void put(String key, String value, int milliSeconds) {
        map.put(key, Value.create(value, milliSeconds));
    }

    public String get(String key){
        return Optional.ofNullable(map.get(key))
                .map(Value::unwrap)
                .orElse(null);
    }

    public static Storage getInstance() {
        return storage;
    }

    public List<Value> findKey(Pattern pattern) {
        System.out.println(map.keySet());
        return map.keySet()
                .stream().filter(value -> pattern.matcher(value).matches())
                .map(Value::create)
                .toList();
    }

    public void init(Map<String, Value> storage) {
        this.map = storage;
    }
}
