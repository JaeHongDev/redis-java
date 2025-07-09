package file;

import file.Rdb.EncodingMarker;
import file.Rdb.Value;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import server.RedisConfig;
import server.Storage;
import util.Printer;
import util.RedisRdbInputStream;

public class RdbFileManager {
    private final RedisConfig redisConfig;
    private final Storage storage;

    private int keyCount;
    private int expireKeyCount;
    private int indexCount;

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
            System.out.println(initializedRdbMedata.getStorage());

            storage.init(initializedRdbMedata.getStorage());
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
            case Rdb.INFORMATION_FOLLOWS -> processInformationFollows(rdbInputStream, metadata);
            case Rdb.KEY_EXPIRE_MILLISECONDS -> processKeyExpireMilliseconds(rdbInputStream, metadata);

            case Value.STRING -> {
                var keySize = rdbInputStream.readInt8(ByteOrder.BIG_ENDIAN);
                var key = rdbInputStream.readString(keySize);
                System.out.println(keySize);

                var valueSize = rdbInputStream.readInt8(ByteOrder.BIG_ENDIAN);
                var value = rdbInputStream.readString(valueSize);

                System.out.println("insert: " + key + " " + value);
                metadata.appendStorage(key, value);
                process(rdbInputStream, metadata);
            }

            default -> throw new IllegalStateException("Unexpected value: " + flag);
        }

        return metadata;
    }

    private void processInformationFollows(RedisRdbInputStream rdbInputStream, RdbMetadata metadata)
            throws IOException {
        metadata.setKeySize(rdbInputStream.readInt8(ByteOrder.BIG_ENDIAN));
        metadata.setExpireKeySize(rdbInputStream.readInt8(ByteOrder.BIG_ENDIAN));
        process(rdbInputStream, metadata);
    }

    private void processKeyExpireMilliseconds(RedisRdbInputStream rdbInputStream, RdbMetadata metadata)
            throws IOException {
        var timestamp = rdbInputStream.readInt64(ByteOrder.LITTLE_ENDIAN);
        Printer.print("timestamp: " + timestamp);
        var milliseconds = Instant.ofEpochMilli(timestamp);
        var keySize = rdbInputStream.readInt16(ByteOrder.BIG_ENDIAN);
        var key = rdbInputStream.readString(keySize);

        var valueSize = rdbInputStream.readInt8(ByteOrder.BIG_ENDIAN);
        var value = rdbInputStream.readString(valueSize);

        System.out.println("insert: " + key + " " + value);
        metadata.appendStorage(key, server.Value.create(value, milliseconds));
        process(rdbInputStream, metadata);
    }

    private void processDbSubSection(RedisRdbInputStream rdbInputStream, RdbMetadata metadata) throws IOException {
        metadata.setIndexSize(rdbInputStream.readInt8(ByteOrder.BIG_ENDIAN));
        process(rdbInputStream, metadata);
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
