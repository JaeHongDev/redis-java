package file;

public class Rdb {

    //@formatter:off
    public static final byte AUXILIARY_FIELD         = (byte) 0xFA;
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
}
