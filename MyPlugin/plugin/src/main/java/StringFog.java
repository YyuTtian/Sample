
public class StringFog {
    public static byte[] encrypt(byte[] src) {
        return java.util.Base64.getEncoder().encodeToString(src).getBytes();
    }

    public static String decrypt(byte[] src) {
        return new String(java.util.Base64.getDecoder().decode(new String(src)));
    }
}
