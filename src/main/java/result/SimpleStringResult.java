package result;

public class SimpleStringResult extends Result {

    private final String value;

    public SimpleStringResult(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
