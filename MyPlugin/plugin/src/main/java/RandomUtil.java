import java.util.Random;

public class RandomUtil {

    private static String[] source = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    private static Random random = new Random();

    public static String randomClassName() {
        StringBuilder stringBuilder = new StringBuilder();

        int countOut = random.nextInt(5) + 3;
        int countInner = random.nextInt(5) + 3;
        for (int i = 0; i < countOut; i++) {
            String temp = "";
            for (int j = 0; j < countInner; j++) {
                int index = random.nextInt(26);
                temp += source[index];
            }
            if (i == countOut - 1) {
                stringBuilder.append(temp);
            } else {
                stringBuilder.append(temp + ".");
            }
        }

        return stringBuilder.toString();
    }
}
