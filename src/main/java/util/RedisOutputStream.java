package util;

import java.io.IOException;
import java.io.OutputStream;
import server.ResultSet;

public class RedisOutputStream implements AutoCloseable {

    private final OutputStream outputStream;

    public RedisOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write(ResultSet resultSet) throws IOException {
        if(resultSet.getSize() >= 2) {
            outputStream.write(writeLine("*" + resultSet.getSize()));
        }

        for(var result: resultSet.getValues()) {
            if(result.value() == null) {
                outputStream.write(writeLine("$-1"));
                outputStream.flush();
            }else {
                outputStream.write(writeLine("$" + result.value().length()));
                outputStream.write(writeLine(result.value()));
                outputStream.flush();
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
}
