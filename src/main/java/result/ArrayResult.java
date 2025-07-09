package result;

import java.util.List;

public class ArrayResult extends Result {
    private final List<String> values;

    public ArrayResult(List<String> values) {
        this.values = values;
    }

    public List<String> values() {
        return values;
    }
}
