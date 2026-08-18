package com.example.urlshortener.util;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class ShortCodeGenerator {
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int LENGTH = 8;
    private final SecureRandom random;

    public ShortCodeGenerator() { this(new SecureRandom()); }
    ShortCodeGenerator(SecureRandom random) { this.random = random; }

    public String generate() {
        StringBuilder result = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            result.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return result.toString();
    }
}
