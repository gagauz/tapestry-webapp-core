package org.gagauz.utils;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
    public static boolean equalsToString(Object o1, Object o2) {
        return o1 == o2 || String.valueOf(o1).equals(String.valueOf(o2));
    }

    private static final String[] TRANSLITERATE;

    static {
        TRANSLITERATE = new String[9056];
        TRANSLITERATE['й'] = "j";
        TRANSLITERATE['ц'] = "cz";
        TRANSLITERATE['у'] = "u";
        TRANSLITERATE['к'] = "k";
        TRANSLITERATE['е'] = "e";
        TRANSLITERATE['ё'] = "yo";
        TRANSLITERATE['н'] = "n";
        TRANSLITERATE['г'] = "g";
        TRANSLITERATE['ш'] = "sh";
        TRANSLITERATE['щ'] = "shh";
        TRANSLITERATE['з'] = "z";
        TRANSLITERATE['х'] = "kh";
        TRANSLITERATE['ъ'] = "";
        TRANSLITERATE['ф'] = "f";
        TRANSLITERATE['ы'] = "y";
        TRANSLITERATE['в'] = "v";
        TRANSLITERATE['а'] = "a";
        TRANSLITERATE['п'] = "p";
        TRANSLITERATE['р'] = "r";
        TRANSLITERATE['о'] = "o";
        TRANSLITERATE['л'] = "l";
        TRANSLITERATE['д'] = "d";
        TRANSLITERATE['ж'] = "zh";
        TRANSLITERATE['э'] = "eh";
        TRANSLITERATE['я'] = "ya";
        TRANSLITERATE['ч'] = "ch";
        TRANSLITERATE['с'] = "s";
        TRANSLITERATE['м'] = "m";
        TRANSLITERATE['и'] = "i";
        TRANSLITERATE['т'] = "t";
        TRANSLITERATE['ь'] = "'";
        TRANSLITERATE['б'] = "b";
        TRANSLITERATE['ю'] = "yu";
        TRANSLITERATE[' '] = "-";
        TRANSLITERATE['/'] = "-";
        TRANSLITERATE['\\'] = "-";
    }

    public static String transliterate(String from) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < from.length(); i++) {
            String bb = TRANSLITERATE[Character.toLowerCase(from.charAt(i))];
            if (null != bb) {
                sb.append(bb);
            } else {
                sb.append(from.charAt(i));
            }
        }
        return sb.toString();
    }

}
