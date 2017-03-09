package org.gagauz.util;

import java.util.HashMap;
import java.util.Map;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
    public static boolean equalsToString(Object o1, Object o2) {
        return o1 == o2 || String.valueOf(o1).equals(String.valueOf(o2));
    }

    private static final Map<Character, String> TRANSLITERATE;

    static {
        TRANSLITERATE = new HashMap<>();
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
                sb.append('[' + from.charAt(i) + ']');
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
