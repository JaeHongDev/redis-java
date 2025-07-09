package file;

import file.Rdb.EncodingMarker;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import server.RedisConfig;
import server.Storage;
import util.Printer;
import util.RedisRdbInputStream;

public class RdbFileManager {
    private final RedisConfig redisConfig;
    private final Storage storage;

    public RdbFileManager(RedisConfig redisConfig, Storage storage) {
        this.redisConfig = redisConfig;
        this.storage = storage;
    }

    public void init() {
        var file = redisConfig.getRdbFile();

        if (!file.exists()) {
            createRdbFile(file);
        }

        readRdbFile(file);
    }

    private void readRdbFile(File file) {

        try (var rdbInputStream = new RedisRdbInputStream(new FileInputStream(redisConfig.getRdbFile()))) {
            var metadata = new RdbMetadata();
            var PREFIX = rdbInputStream.readString(9);
            var initializedRdbMedata = process(rdbInputStream, metadata);

            for (var entry : initializedRdbMedata.getStorage().entrySet()) {
                storage.put(entry.getKey(), entry.getValue());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private RdbMetadata process(RedisRdbInputStream rdbInputStream, RdbMetadata metadata) throws IOException {
        var flag = rdbInputStream.readByte();
        Printer.byteArrayToHex(new byte[]{flag});
        switch (flag) {
            case Rdb.EOF -> {
                System.out.println(metadata.getAuxiliaryFields());
            }
            case Rdb.AUXILIARY_FIELD -> processAuxiliaryField(rdbInputStream, metadata);
            case Rdb.DB_SUBSECTION -> processDbSubSection(rdbInputStream, metadata);
            default -> {
                Printer.byteArrayToHex(new byte[]{flag});
                System.out.println(metadata.getAuxiliaryFields());
            }
        }

        return metadata;
    }

    private void processDbSubSection(RedisRdbInputStream rdbInputStream, RdbMetadata metadata) throws IOException {
        var dbSection = rdbInputStream.readByte();
        var fb = rdbInputStream.readByte();

        var size = rdbInputStream.readInt8(ByteOrder.BIG_ENDIAN);
        var keyType = rdbInputStream.readByte();

        for (int i = 0; i < size; i++) {
            var keySize = rdbInputStream.readInt8(ByteOrder.BIG_ENDIAN);
            var key = rdbInputStream.readString(keySize);

            var valueSize = rdbInputStream.readInt8(ByteOrder.BIG_ENDIAN);
            var value = rdbInputStream.readString(valueSize);

            metadata.appendStorage(key, value);
        }

    }

    private void processAuxiliaryField(RedisRdbInputStream rdbInputStream, RdbMetadata rdbMetadata) throws IOException {
        var keySize = rdbInputStream.readByte();
        var keyName = rdbInputStream.readString(keySize);

        var flag = rdbInputStream.readByte();

        switch (flag) {
            case EncodingMarker.BYTE -> {
                var value = rdbInputStream.readByte();
                rdbMetadata.appendAuxiliaryFiled(keyName, value);
            }

            case EncodingMarker.INT -> {
                var value = rdbInputStream.readInt32(ByteOrder.LITTLE_ENDIAN);
                rdbMetadata.appendAuxiliaryFiled(keyName, value);
            }
            default -> {
                var valueSize = (int) flag;
                var value = rdbInputStream.readString(valueSize);
                rdbMetadata.appendAuxiliaryFiled(keyName, value);
            }
        }
        process(rdbInputStream, rdbMetadata);

    }

    private static void createRdbFile(File file) {
        try {
            Files.createDirectories(file.toPath().getParent());
            Files.write(file.toPath(), "".getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
