package result;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BulkStringResult extends Result {

    private final List<SingleResult> results;

    public BulkStringResult(String... strings) {
        this.results = Arrays.stream(strings).map(SingleResult::new).toList();
    }

    public Collection<? extends SingleResult> getResults() {
        return results;
    }
}
