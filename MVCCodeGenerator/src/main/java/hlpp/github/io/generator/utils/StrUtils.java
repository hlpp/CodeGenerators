package hlpp.github.io.generator.utils;

public class StrUtils {
    public static String firstLetterLower(String s) {
        String firstLetter = s.substring(0,1).toLowerCase();
        String restLetters = s.substring(1);
        return firstLetter + restLetters;
    }
}
