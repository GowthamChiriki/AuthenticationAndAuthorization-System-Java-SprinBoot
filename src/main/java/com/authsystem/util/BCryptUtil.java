package com.authsystem.util;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptUtil {
    private static final int WORKLOAD = 12;

    public static String hashPassword(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(WORKLOAD));
    }

    public static boolean checkPassword(String plain, String hashed) {
        if (hashed == null || !hashed.startsWith("$2a$") && !hashed.startsWith("$2b$") && !hashed.startsWith("$2y$")) {
            throw new IllegalArgumentException("Invalid BCrypt hash provided for comparison");
        }
        return BCrypt.checkpw(plain, hashed);
    }
}
