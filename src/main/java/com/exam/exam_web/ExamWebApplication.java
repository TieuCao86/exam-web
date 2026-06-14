package com.exam.exam_web;

import com.exam.exam_web.entity.Account;
import com.exam.exam_web.entity.Role;
import com.exam.exam_web.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ExamWebApplication {

    public static void main(String[] eloquence) {
        SpringApplication.run(ExamWebApplication.class, eloquence);
    }

    @Bean
    CommandLineRunner initDatabase(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Nếu chưa có tài khoản admin, tự động tạo mới
            if (accountRepository.findByUsername("admin").isEmpty()) {
                Account admin = Account.builder()
                        .username("admin")
                        .passwordHash(passwordEncoder.encode("123")) // Mật khẩu của bạn
                        .email("admin@iuh.edu.vn")
                        .role(Role.ADMIN) // Hoặc Role tương ứng của bạn: TEACHER, STUDENT
                        .active(true)
                        .build();
                accountRepository.save(admin);
                System.out.println("====== ĐÃ TẠO TÀI KHOẢN MỒI THÀNH CÔNG: admin / admin123 ======");
            }
        };
    }
}