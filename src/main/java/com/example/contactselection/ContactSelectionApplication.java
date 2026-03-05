package com.example.contactselection;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point – Contact Selection API
 * Tech stack: Spring Boot 3 + MyBatis + MySQL
 */
@SpringBootApplication
@MapperScan("com.example.contactselection.repository")
public class ContactSelectionApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContactSelectionApplication.class, args);
    }
}
