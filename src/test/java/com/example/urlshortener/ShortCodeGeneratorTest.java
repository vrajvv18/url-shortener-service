package com.example.urlshortener;

import com.example.urlshortener.util.ShortCodeGenerator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;

class ShortCodeGeneratorTest {
    @Test
    void generatesEightCharacterAlphaNumericCodes() {
        ShortCodeGenerator generator = new ShortCodeGenerator();
        for (int i = 0; i < 100; i++) {
            String code = generator.generate();
            assertEquals(8, code.length());
            assertTrue(code.matches("[A-Za-z0-9]{8}"));
        }
    }

    @Test
    void generatesManyCodesWithoutCollisionInSmallSample() {
        ShortCodeGenerator generator = new ShortCodeGenerator();
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < 10_000; i++) codes.add(generator.generate());
        assertEquals(10_000, codes.size());
    }
}
