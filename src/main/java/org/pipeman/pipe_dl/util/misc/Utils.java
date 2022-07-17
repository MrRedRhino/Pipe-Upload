package org.pipeman.pipe_dl.util.misc;

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
}
