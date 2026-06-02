package utilities;

public class RandomUtils {

    public static String generateMobile() {

        long num = System.currentTimeMillis() % 10000000000L;

        return "9" + String.valueOf(num).substring(1);
    }

    public static String generateEmail() {

        return "test" + System.currentTimeMillis() + "@mail.com";
    }
}