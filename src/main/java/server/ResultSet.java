package server;

import java.util.List;

public class ResultSet {

    private final List<Result> values;
    private final int size;
    private int offset;

    public ResultSet(List<Result> values) {
        this.values = values;
        this.size = values.size();
        this.offset = 0;
    }

    public List<Result> getValues() {
        return values;
    }

    public Result get(){
        return values.get(offset++);
    }

    public int getSize() {
        return size;
    }
}
