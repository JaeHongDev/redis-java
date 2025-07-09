package util;

public class Printer {
    public static void xxdL(byte[] bytes) {
        int len = bytes.length;
        int offset = 0;
        while (offset < len) {
            // 주소 출력 (hex, 8자리)

            // hex 영역 (최대 16바이트)
            for (int i = 0; i < 16; i++) {
                if (offset + i < len) {
                    System.out.printf("%02x ", bytes[offset + i]);
                } else {
                    System.out.print("   ");
                }
                // 8바이트마다 공백 추가
                if (i == 7) System.out.print(" ");
            }
            System.out.println();
            offset += 16;
        }
    }

    public static void byteArrayToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%#02x", b & 0xFF));
        }
        System.out.println(sb);
    }

    public static void xxd(byte[] bytes) {
        int len = bytes.length;
        int offset = 0;
        while (offset < len) {
            // 주소 출력 (hex, 8자리)
            System.out.printf("%08x: ", offset);

            // hex 영역 (최대 16바이트)
            for (int i = 0; i < 16; i++) {
                if (offset + i < len) {
                    System.out.printf("%02x ", bytes[offset + i]);
                } else {
                    System.out.print("   ");
                }
                // 8바이트마다 공백 추가
                if (i == 7) System.out.print(" ");
            }
            System.out.print(" ");

            // ascii 영역
            for (int i = 0; i < 16 && offset + i < len; i++) {
                int b = bytes[offset + i] & 0xFF;
                if (b >= 32 && b <= 126) {
                    System.out.print((char) b);
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
            offset += 16;
        }
    }

    public static void print(Object s) {
        System.out.println(s);
    }
}
