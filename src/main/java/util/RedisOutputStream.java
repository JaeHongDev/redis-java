package util;

import exception.RedisException;
import java.io.IOException;
import java.io.OutputStream;
import result.ArrayResult;
import result.SingleResult;
import server.ResultSet;

public class RedisOutputStream implements AutoCloseable {

    private final OutputStream outputStream;

    public RedisOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write(ResultSet resultSet) throws IOException {
        System.out.println("result: " + resultSet.getValues());

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
                    System.out.println(arrayResult.values());
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

}
