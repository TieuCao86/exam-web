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
                        .requestMatchers(
                                "/api/exams/**",
                                "/api/courses/**",
                                "/api/teacher/questions/**"
                        ).permitAll()

                        .requestMatchers("/api/**").permitAll()

                        // BẮT BUỘC các API nằm trong gói /api/teacher/** phải có quyền TEACHER hoặc ADMIN
                        .requestMatchers("/api/teacher/**").hasAnyRole("TEACHER", "ADMIN")

                        // 3. PHÂN QUYỀN CHO CÁC TRANG GIAO DIỆN (VIEW - MVC WEB)
                        .requestMatchers("/students/**").hasRole("ADMIN")
                        .requestMatchers("/questions/**").hasRole("TEACHER")
                        .requestMatchers("/calendar/**").hasRole("STUDENT")
                        .requestMatchers("/courses/**", "/exams/**", "/history/**").authenticated()

                        // Tất cả các yêu cầu còn lại đều phải đăng nhập
                        .anyRequest().authenticated()
                )

                // CẤU HÌNH ĐĂNG NHẬP GIAO DIỆN (Tương thích cả Thymeleaf lẫn React)
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")

                                // 💡 CẬP NHẬT TRONG SUCCESS HANDLER:
                                .successHandler((request, response, authentication) -> {
                                    String acceptHeader = request.getHeader("Accept");
                                    String xRequestedWith = request.getHeader("X-Requested-With");

                                    // Nới lỏng điều kiện: Chỉ cần một trong các dấu hiệu từ React/Ajax xuất hiện là duyệt luôn
                                    if ((acceptHeader != null && acceptHeader.contains("application/json"))
                                            || "XMLHttpRequest".equals(xRequestedWith)
                                            || request.getRequestURI().contains("/api")) {

                                        response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_OK);
                                        response.setContentType("application/json;charset=UTF-8");
                                        response.getWriter().write("{\"success\": true, \"message\": \"Login successful\"}");
                                        response.getWriter().flush();
                                    } else {
                                        new RoleBasedSuccessHandler().onAuthenticationSuccess(request, response, authentication);
                                    }
                                })

// 💡 CẬP NHẬT TRONG FAILURE HANDLER:
                                .failureHandler((request, response, exception) -> {
                                    String acceptHeader = request.getHeader("Accept");
                                    String xRequestedWith = request.getHeader("X-Requested-With");

                                    if ((acceptHeader != null && acceptHeader.contains("application/json"))
                                            || "XMLHttpRequest".equals(xRequestedWith)
                                            || request.getRequestURI().contains("/api")) {

                                        response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                                        response.setContentType("application/json;charset=UTF-8");
                                        response.getWriter().write("{\"success\": false, \"message\": \"Invalid username or password\"}");
                                        response.getWriter().flush();
                                    } else {
                                        response.sendRedirect("/login?error");
                                    }
                                })
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