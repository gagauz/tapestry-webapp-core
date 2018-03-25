package com.xl0e.util;

import java.util.HashMap;
import java.util.Map;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
    public static boolean equalsToString(Object o1, Object o2) {
        return o1 == o2
                || String.valueOf(o1).equals(String.valueOf(o2));
    }

    private static final Map<Character, String> TRANSLITERATE;

    static {
        TRANSLITERATE = new HashMap<>();
        TRANSLITERATE.put('Й', "J");
        TRANSLITERATE.put('Ц', "Cz");
        TRANSLITERATE.put('У', "U");
        TRANSLITERATE.put('К', "K");
        TRANSLITERATE.put('Е', "E");
        TRANSLITERATE.put('Ё', "Yo");
        TRANSLITERATE.put('Н', "N");
        TRANSLITERATE.put('Г', "G");
        TRANSLITERATE.put('Ш', "Sh");
        TRANSLITERATE.put('Щ', "Shh");
        TRANSLITERATE.put('З', "Z");
        TRANSLITERATE.put('Х', "Kh");
        TRANSLITERATE.put('Ъ', "");
        TRANSLITERATE.put('Ф', "F");
        TRANSLITERATE.put('Ы', "Y");
        TRANSLITERATE.put('В', "V");
        TRANSLITERATE.put('А', "A");
        TRANSLITERATE.put('П', "P");
        TRANSLITERATE.put('Р', "R");
        TRANSLITERATE.put('О', "O");
        TRANSLITERATE.put('Л', "L");
        TRANSLITERATE.put('Д', "D");
        TRANSLITERATE.put('Ж', "Zh");
        TRANSLITERATE.put('Э', "Eh");
        TRANSLITERATE.put('Я', "Ya");
        TRANSLITERATE.put('Ч', "Ch");
        TRANSLITERATE.put('С', "S");
        TRANSLITERATE.put('М', "M");
        TRANSLITERATE.put('И', "I");
        TRANSLITERATE.put('Т', "T");
        TRANSLITERATE.put('Ь', "'");
        TRANSLITERATE.put('Б', "B");
        TRANSLITERATE.put('Ю', "Yu");

        TRANSLITERATE.put('й', "j");
        TRANSLITERATE.put('ц', "cz");
        TRANSLITERATE.put('у', "u");
        TRANSLITERATE.put('к', "k");
        TRANSLITERATE.put('е', "e");
        TRANSLITERATE.put('ё', "yo");
        TRANSLITERATE.put('н', "n");
        TRANSLITERATE.put('г', "g");
        TRANSLITERATE.put('ш', "sh");
        TRANSLITERATE.put('щ', "shh");
        TRANSLITERATE.put('з', "z");
        TRANSLITERATE.put('х', "kh");
        TRANSLITERATE.put('ъ', "");
        TRANSLITERATE.put('ф', "f");
        TRANSLITERATE.put('ы', "y");
        TRANSLITERATE.put('в', "v");
        TRANSLITERATE.put('а', "a");
        TRANSLITERATE.put('п', "p");
        TRANSLITERATE.put('р', "r");
        TRANSLITERATE.put('о', "o");
        TRANSLITERATE.put('л', "l");
        TRANSLITERATE.put('д', "d");
        TRANSLITERATE.put('ж', "zh");
        TRANSLITERATE.put('э', "eh");
        TRANSLITERATE.put('я', "ya");
        TRANSLITERATE.put('ч', "ch");
        TRANSLITERATE.put('с', "s");
        TRANSLITERATE.put('м', "m");
        TRANSLITERATE.put('и', "i");
        TRANSLITERATE.put('т', "t");
        TRANSLITERATE.put('ь', "'");
        TRANSLITERATE.put('б', "b");
        TRANSLITERATE.put('ю', "yu");
        TRANSLITERATE.put(' ', "-");
        TRANSLITERATE.put('/', "-");
        TRANSLITERATE.put('\\', "-");
    }

    public static String transliterate(String from) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < from.length(); i++) {
            String bb = TRANSLITERATE.get(from.charAt(i));
            if (null != bb) {
                sb.append(bb);
            } else {
                sb.append('['
                        + from.charAt(i)
                        + ']');
            }
        }
        return sb.toString();
    }

    public static String join(String separator, Object first, Object... objects) {
        StringBuilder result = new StringBuilder(first.toString());
        for (int i = 0; i < objects.length; i++) {
            result.append(separator);
            result.append(objects[i]);
        }
        return result.toString();
    }
}
