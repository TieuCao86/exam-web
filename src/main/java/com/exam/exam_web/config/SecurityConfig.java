package com.exam.exam_web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF để test API qua Postman mượt mà

                .authorizeHttpRequests(auth -> auth
                        // 1. MỞ KHÓA CHO TẤT CẢ TÀI NGUYÊN TĨNH VÀ TRANG LOGIN
                        .requestMatchers(
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        // 2. PHÂN QUYỀN RIÊNG CHO CÁC ĐƯỜNG DẪN REST API (/api/**)
                        // Cho phép sinh viên/mọi người gọi các API chung (xem đề, xem khóa học)
                        .requestMatchers(
                                "/api/exams/**",
                                "/api/courses/**"
                        ).permitAll()

                        .requestMatchers("/api/**").permitAll()

                        // BẮT BUỘC các API nằm trong gói /api/teacher/** phải có quyền TEACHER hoặc ADMIN
                        // (Thêm dòng này giúp Postman không bị lọt xuống formLogin)
                        .requestMatchers("/api/teacher/**").hasAnyRole("TEACHER", "ADMIN")

                        // Các API chung khác nếu có

                        // 3. PHÂN QUYỀN CHO CÁC TRANG GIAO DIỆN (VIEW - MVC WEB)
                        .requestMatchers("/students/**").hasRole("ADMIN")
                        .requestMatchers("/questions/**").hasRole("TEACHER")
                        .requestMatchers("/calendar/**").hasRole("STUDENT")
                        .requestMatchers("/courses/**", "/exams/**", "/history/**").authenticated()

                        // Tất cả các yêu cầu còn lại đều phải đăng nhập
                        .anyRequest().authenticated()
                )

                // CẤU HÌNH ĐĂNG NHẬP GIAO DIỆN (Cho trình duyệt)
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                         .successHandler(new RoleBasedSuccessHandler()) // Tạm thời comment dòng này khi test bằng Postman nếu nó gây loop
                        .failureUrl("/login?error")
                        .permitAll()
                )

                // CẤU HÌNH ĐĂNG XUẤT
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}