package com.worklyze.worklyze.shared.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordHash {
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String hash(String password) {
        return encoder.encode(password);
    }

    public static boolean checkPassword(String password) {
        var hashedPassword = hash(password);

        return encoder.matches(password, hashedPassword);
    }
}
