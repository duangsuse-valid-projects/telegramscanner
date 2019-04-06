package org.duangsuse.telegramscanner.helper;

/**
 * String helper program
 */
public final class Strings {
    /**
     * Take head n, or entire string
     *
     * @param n take at most
     * @param str target string
     * @return if str.length greater than n, then sub-sequence str, else return str
     */
    public static String take(int n, String str) {
        if (n == 0 || str.length() == 0)
            return "";

        if (str.length() > n)
            return str.substring(0, n - 1);
        else /* = n or < n */
            return str;
    }
}
