package com.example.codebase.config;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class JasyptConfigTest {

    @Autowired
    @Qualifier("jasyptStringEncryptor")
    private StringEncryptor stringEncryptor;

    @DisplayName("Jasypt 암호화")
    @Test
    void encrypt() {
        String text = "hello world";
        System.out.printf("ENC(%s)%n", stringEncryptor.encrypt(text));
    }

    @DisplayName("Jasypt 복호화")
    @Test
    void decrypt() {
        String text = "XaueZq0cG2GamiRn8Z/Gs6nudz2XghjQvRO0cKIRSeE=";
        System.out.printf("%s%n", stringEncryptor.decrypt(text));
    }

}