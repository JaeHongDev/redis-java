package file;

import java.util.HashMap;
import java.util.Map;
import server.Value;

public class RdbMetadata {

    private final Map<String, Object> auxiliaryFields = new HashMap<>();
    private final Map<String, Value> storage = new HashMap<>();

    public void appendAuxiliaryFiled(String keyName, Object value) {
        auxiliaryFields.put(keyName, value);
    }

    public Map<String, Object> getAuxiliaryFields() {
        return auxiliaryFields;
    }

    public void appendStorage(String key, String value) {
        storage.put(key, Value.create(value));
    }

    public void appendStorage(String key, Value value) {
        storage.put(key, value);
    }

    public Map<String, Value> getStorage() {
        return storage;
    }

    public void setIndexSize(int i) {
    }

    public void setExpireKeySize(int i) {

    }

    public void setKeySize(int i) {

    }
    // head
    // metadata
    // database
    // end of file section

}
