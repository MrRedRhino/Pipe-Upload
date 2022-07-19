package org.pipeman.pipe_dl.util.misc;

import java.util.Random;

public class Utils {
    public static long[] parseToLongArray(String[] in) {
        long[] out = new long[in.length];
        for (int i = 0; i < in.length; i++) {
            Long l = parseLong(in[i]);
            if (l == null) return new long[0];
            out[i] = l;
        }
        return out;
    }

    public static Long parseLong(String in) {
        try {
            return Long.parseLong(in);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static String generateRandomString(int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
