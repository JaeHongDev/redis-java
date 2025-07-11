package util;

import exception.RedisException;
import java.io.IOException;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import result.ArrayResult;
import result.BinaryBulkStringResult;
import result.BulkStringResult;
import result.Result;
import result.SimpleStringResult;
import result.SingleResult;
import server.ResultSet;

public class RedisOutputStream implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(RedisOutputStream.class);
    private final OutputStream outputStream;

    public RedisOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write(Result result) throws IOException {
        switch (result) {
            case SingleResult singleResult -> {
                if (singleResult.value() == null) {
                    outputStream.write(writeLine("$-1"));
                    outputStream.flush();
                } else {
                    outputStream.write(writeLine("$" + singleResult.value().length()));
                    outputStream.write(writeLine(singleResult.value()));
                    outputStream.flush();
                }
            }
            case SimpleStringResult simpleStringResult -> {
                outputStream.write("+".getBytes());
                outputStream.write(writeLine(simpleStringResult.getValue()));
                outputStream.flush();
            }
            case BulkStringResult bulkStringResult -> {
                var results = bulkStringResult.getResults();
                outputStream.write(writeLine("*" + results.size()));
                for (var singleResult : results){
                    if (singleResult == null) {
                        outputStream.write(writeLine("$-1"));
                    } else {
                        outputStream.write(writeLine("$" + singleResult.value().length()));
                        outputStream.write(writeLine(singleResult.value()));
                    }
                }
                outputStream.flush();
            }
            case ArrayResult arrayResult -> {
                outputStream.write(writeLine("*" + arrayResult.values().size()));
                for (var singleResult : arrayResult.values()) {
                    if (singleResult == null) {
                        outputStream.write(writeLine("$-1"));
                    } else {
                        outputStream.write(writeLine("$" + singleResult.length()));
                        outputStream.write(writeLine(singleResult));
                    }
                }
                outputStream.flush();
            }
            case BinaryBulkStringResult binaryBulkStringResult -> {
                outputStream.write("+".getBytes());
                outputStream.write(writeLine(binaryBulkStringResult.getSimpleString()));
                outputStream.write(writeLine("$" +binaryBulkStringResult.getBinary().length));
                outputStream.write(binaryBulkStringResult.getBinary());
                outputStream.flush();
            }
            default -> throw new IllegalStateException("Unexpected value: " + result);
        }
    }
    public void write(ResultSet resultSet) throws IOException {
        if (resultSet.getSize() >= 2) {
            outputStream.write(writeLine("*" + resultSet.getSize()));
        }

        for (var result : resultSet.getValues()) {
            switch (result) {
                case SingleResult singleResult -> {
                    if (singleResult.value() == null) {
                        outputStream.write(writeLine("$-1"));
                        outputStream.flush();
                    } else {
                        outputStream.write(writeLine("$" + singleResult.value().length()));
                        outputStream.write(writeLine(singleResult.value()));
                        outputStream.flush();
                    }
                }
                case ArrayResult arrayResult -> {
                    outputStream.write(writeLine("*" + arrayResult.values().size()));
                    for (var singleResult : arrayResult.values()) {
                        if (singleResult == null) {
                            outputStream.write(writeLine("$-1"));
                        } else {
                            outputStream.write(writeLine("$" + singleResult.length()));
                            outputStream.write(writeLine(singleResult));
                        }
                    }
                    outputStream.flush();
                }
                default -> throw new IllegalStateException("Unexpected value: " + result);
            }

        }
    }

    private byte[] writeLine(String value) {
        return (value + "\r\n").getBytes();
    }

    @Override
    public void close() throws Exception {
        outputStream.close();
    }

    public void write(RedisException e) throws IOException {
        outputStream.write(writeLine("-" + e.getMessage()));
    }

    public void sendCommand(String... args) throws IOException {
        write("*" + args.length);
        for (String arg : args) {
            write("$" + arg.length());
            write(arg);
        }
        outputStream.flush();
    }

    private void flush() throws IOException {
        outputStream.flush();
    }

    private void write(String input) throws IOException {
        outputStream.write(writeLine(input));
    }
}
