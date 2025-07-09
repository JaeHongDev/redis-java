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
import util.Printer;
import util.RedisRdbInputStream;

public class RdbFileManager {
    private final RedisConfig redisConfig;

    public RdbFileManager(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    public static RdbMetadata init(RedisConfig redisConfig) {
        var file = redisConfig.getRdbFile();

        if (!file.exists()) {
            createRdbFile(file);
        }

        return readRdbFile(file);
    }

    private static RdbMetadata readRdbFile(File file) {
        try (var rdbInputStream = new RedisRdbInputStream(new FileInputStream(file))) {
            var metadata = new RdbMetadata();
            var PREFIX = rdbInputStream.readString(9);

            return process(rdbInputStream, metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static RdbMetadata process(RedisRdbInputStream rdbInputStream, RdbMetadata metadata) throws IOException {
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

    private static void processInformationFollows(RedisRdbInputStream rdbInputStream, RdbMetadata metadata)
            throws IOException {
        metadata.setKeySize(rdbInputStream.readInt8(ByteOrder.BIG_ENDIAN));
        metadata.setExpireKeySize(rdbInputStream.readInt8(ByteOrder.BIG_ENDIAN));
        process(rdbInputStream, metadata);
    }

    private static void processKeyExpireMilliseconds(RedisRdbInputStream rdbInputStream, RdbMetadata metadata)
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

    private static void processDbSubSection(RedisRdbInputStream rdbInputStream, RdbMetadata metadata) throws IOException {
        metadata.setIndexSize(rdbInputStream.readInt8(ByteOrder.BIG_ENDIAN));
        process(rdbInputStream, metadata);
    }

    private static void processAuxiliaryField(RedisRdbInputStream rdbInputStream, RdbMetadata rdbMetadata) throws IOException {
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
