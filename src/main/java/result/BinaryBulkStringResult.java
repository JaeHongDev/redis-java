package result;

public class BinaryBulkStringResult extends Result {

    private final String simpleString;
    private final byte[] binary;

    public BinaryBulkStringResult(String simpleString, byte[] binary) {
        this.simpleString = simpleString;
        this.binary = binary;
    }

    public String getSimpleString() {
        return simpleString;
    }

    public byte[] getBinary() {
        return binary;
    }
}
