package com.exam.exam_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ExamWebApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ExamWebApplication.class, args);

        // 💡 TUYỆT CHIÊU: Lấy chính bộ mã hóa trong cấu hình để tự băm chuỗi "123"
        PasswordEncoder encoder = context.getBean(PasswordEncoder.class);
        System.out.println("\n=======================================================");
        System.out.println("CHUỖI BĂM CHUẨN CỦA MÁY BẠN: " + encoder.encode("123"));
        System.out.println("=======================================================\n");
    }
}