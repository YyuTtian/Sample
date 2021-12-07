import java.util.Random;

public class RandomStr {

    private static String[] str = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private static Random random = new Random();

    public static String getStr() {
        int strCount = random.nextInt(20) + 20;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strCount; i++) {
            sb.append(str[random.nextInt(str.length)]);
        }

        return sb.toString();
    }
}
