package server;

import java.util.ArrayList;
import java.util.List;
import result.BulkStringResult;
import result.Result;


public class ResultSet {

    private final List<result.Result> values;
    private int offset;

    public ResultSet() {
        this.values = new ArrayList<>();
    }

    public ResultSet(List<Result> values) {
        this.values = values;
        this.offset = 0;
    }

    public void add(Result result) {
        if (result instanceof BulkStringResult bulkStringResult) {
            values.addAll(bulkStringResult.getResults());
        } else {
            values.add(result);
        }
    }

    public List<Result> getValues() {
        return values;
    }

    public Result get() {
        return values.get(offset++);
    }

    public int getSize() {
        return values.size();
    }
}
