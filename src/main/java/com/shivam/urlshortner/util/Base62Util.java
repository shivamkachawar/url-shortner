package com.shivam.urlshortner.util;

public class Base62Util {

    private static final String CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String encode(Long num) {

        StringBuilder sb = new StringBuilder();

        while (num > 0) {
            int remainder = (int) (num % 62);
            sb.append(CHARSET.charAt(remainder));
            num = num / 62;
        }

        String result = sb.reverse().toString();

        // ensure minimum length = 4
        while (result.length() < 4) {
            result = "0" + result;
        }

        return result;
    }
}