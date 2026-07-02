package com.mohan.stockguard.config;

import com.mohan.stockguard.entity.Product;
import com.mohan.stockguard.entity.User;
import com.mohan.stockguard.repository.ProductRepository;
import com.mohan.stockguard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            userRepository.save(User.builder()
                .email("admin@example.com")
                .passwordHash(passwordEncoder.encode("adminpass"))
                .role(User.Role.ADMIN)
                .build());
        }

        if (userRepository.findByEmail("customer@example.com").isEmpty()) {
            userRepository.save(User.builder()
                .email("customer@example.com")
                .passwordHash(passwordEncoder.encode("customerpass"))
                .role(User.Role.CUSTOMER)
                .build());
        }

        if (productRepository.count() == 0) {
            productRepository.save(Product.builder()
                .name("Flash Sale Widget")
                .price(new BigDecimal("19.99"))
                .availableStock(50)
                .build());
        }
    }
}
