package server;

import java.util.List;

// command args refactoring
public class Commands {

    private final List<String> inputs;
    private int offset;
    private final int limit;

    public Commands(List<String> inputs) {
        this.inputs = inputs;
        this.offset = 0;
        this.limit = inputs.size();
    }

    public boolean isRemaining() {
        return offset < limit;
    }

    public String peek(){
        return inputs.get(offset);
    }

    public String poll(){
        return inputs.get(offset++);
    }

    public int size(){
        return limit - offset;
    }

    public List<String> getStrings(){
        return inputs;
    }
}
