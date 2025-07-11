package file;

public class Rdb {

    //@formatter:off
    public static final byte AUXILIARY_FIELD         = (byte) 0xFA;
    public static final byte INFORMATION_FOLLOWS     = (byte) 0xFB;
    public static final byte EOF                     = (byte) 0xFF;
    public static final byte DB_SUBSECTION           = (byte) 0xFE;
    public static final byte TYPE                    = (byte) 0x00;
    public static final byte KEY_EXPIRE_MILLISECONDS = (byte) 0xFC;
    public static final byte KEY_EXPIRE_SECONDS      = (byte) 0xFD;
    //@formatter:on

    public static class EncodingMarker {
        //@formatter:off
        public static final byte BYTE   = (byte) 0xC0;
        public static final byte INT    = (byte) 0xC2;
        //@formatter:on
    }

    public static class Value {
        //0 = String Encoding
        //1 = List Encoding
        //2 = Set Encoding
        //3 = Sorted Set Encoding
        //4 = Hash Encoding
        //9 = Zipmap Encoding
        //10 = Ziplist Encoding
        //11 = Intset Encoding
        //12 = Sorted Set in Ziplist Encoding
        //13 = Hashmap in Ziplist Encoding (Introduced in RDB version 4)
        //14 = List in Quicklist encoding (Introduced in RDB version 7)

        //@formatter:off
        public static final byte STRING     = (byte) 0x00;
        public static final byte LIST       = (byte) 0x01;
        public static final byte SET        = (byte) 0x02;
        public static final byte SORTED_SET = (byte) 0x03;
        public static final byte HASH       = (byte) 0x04;
        public static final byte ZIPMAP     = (byte) 0x09;
        public static final byte ZIPLIST    = (byte) 0x10;
        //@formatter:on

    }

    public static final byte[] EMPTY_FILE = hexStringToByteArray(
            "524544495330303131fa0972656469732d76657205372e322e30fa0a72656469732d62697473c040fa056374696d65c26d08bc65fa08757365642d6d656dc2b0c41000fa08616f662d62617365c000fff06e3bfec0ff5aa2");


    public static byte[] hexStringToByteArray(String s) {
        if (s.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex 문자열 길이는 짝수여야 함");
        }

        int len = s.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            int high = Character.digit(s.charAt(i), 16);
            int low = Character.digit(s.charAt(i + 1), 16);

            if (high == -1 || low == -1) {
                throw new IllegalArgumentException("유효하지 않은 hex 문자: " + s.substring(i, i + 2));
            }

            data[i / 2] = (byte) ((high << 4) + low);
        }

        return data;
    }
}
