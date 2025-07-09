package result;

public class SingleResult extends Result {

    private final String value;

    public SingleResult(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
