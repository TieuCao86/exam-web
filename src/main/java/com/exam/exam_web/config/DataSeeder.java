package com.exam.exam_web.config;

import com.exam.exam_web.entity.Account;
import com.exam.exam_web.entity.Role;
import com.exam.exam_web.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(AccountRepository accountRepository,
                      PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // Chỉ seed nếu chưa có data
        if (accountRepository.count() > 0) return;

        accountRepository.save(Account.builder()
                .username("admin")
                .email("admin@exam.com")
                .passwordHash(passwordEncoder.encode("123"))
                .role(Role.ADMIN)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build());

        accountRepository.save(Account.builder()
                .username("teacher")
                .email("teacher@exam.com")
                .passwordHash(passwordEncoder.encode("123"))
                .role(Role.TEACHER)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build());

        accountRepository.save(Account.builder()
                .username("student")
                .email("student@exam.com")
                .passwordHash(passwordEncoder.encode("123"))
                .role(Role.STUDENT)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build());

        System.out.println(">>> Seeded 3 accounts thành công!");
    }
}