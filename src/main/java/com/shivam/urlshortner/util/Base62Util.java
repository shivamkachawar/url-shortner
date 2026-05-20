package com.shivam.urlshortner.util;

import java.security.SecureRandom;

public class Base62Util {

    private static final String CHARACTERS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final SecureRandom random = new SecureRandom();

    // Existing encoder (optional keep)
    public static String encode(long value) {

        StringBuilder sb = new StringBuilder();

        while (value > 0) {
            sb.append(CHARACTERS.charAt((int) (value % 62)));
            value /= 62;
        }

        return sb.reverse().toString();
    }

    // 🔥 NEW RANDOM CODE GENERATOR
    public static String generateRandomCode(int length) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {

            int index = random.nextInt(CHARACTERS.length());

            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }
}