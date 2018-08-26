package pl.pilionerzy.util;

import org.apache.commons.lang3.StringUtils;
import pl.pilionerzy.exception.IllegalPrefixException;

import java.util.Arrays;
import java.util.List;

public class PrefixUtils {

    private static final List<Character> VALID_PREFIXES = Arrays.asList('A', 'B', 'C', 'D');

    public static void validatePrefix(String prefix) {
        if (StringUtils.isBlank(prefix) || !isValid(prefix.trim())) {
            throw new IllegalPrefixException(prefix);
        }
    }

    static boolean isValid(String prefix) {
        if (prefix.length() != 1) {
            return false;
        }
        return VALID_PREFIXES.contains(prefix.charAt(0));
    }

}
