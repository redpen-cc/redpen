package cc.redpen.util;

public class StringUtils {
    public static boolean isKatakana(char c) {
        return Character.UnicodeBlock.of(c) == Character.UnicodeBlock.KATAKANA;
    }

    public static boolean isBasicLatin(char c) {
        return Character.UnicodeBlock.of(c) == Character.UnicodeBlock.BASIC_LATIN;
    }
}
