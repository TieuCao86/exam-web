package com.exam.exam_web.config;

import com.exam.exam_web.services.CustomUserDetails;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Session expired or missing credentials.\"}");
                        })
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(
                                "/login",
                                "/images/**",
                                "/css/**",
                                "/js/**"
                        ).permitAll()

                        .requestMatchers("/api/debug").permitAll()

                        .requestMatchers("/api/student/**")
                        .hasRole("STUDENT")

                        .requestMatchers("/api/teacher/**")
                        .hasAnyRole("TEACHER", "ADMIN")

                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form

                        .loginPage("/login")
                        .loginProcessingUrl("/login")

                        .usernameParameter("username")
                        .passwordParameter("password")

                        .successHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json;charset=UTF-8");

                            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

                            String userId = userDetails.getId(); // Lấy được ID thật dưới DB!
                            String username = userDetails.getUsername();

                            // Lấy Role sạch (Ví dụ: STUDENT)
                            String role = userDetails.getAuthorities().stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .findFirst()
                                    .orElse("ROLE_STUDENT")
                                    .replace("ROLE_", "");

                            System.out.println("LOGIN SESSION = " + request.getSession().getId());
                            System.out.println("AUTH = " + authentication);
                            System.out.println("AUTHORITIES = " + authentication.getAuthorities());

                            String jsonResponse = String.format("""
                            {
                              "success": true,
                              "message": "Login successful",
                              "id": "%s",
                              "username": "%s",
                              "role": "%s"
                            }
                            """, userId, username, role);

                            response.getWriter().write(jsonResponse);
                        })

                        .failureHandler((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");

                            response.getWriter().write("""
                                    {
                                      "success": false,
                                      "message": "Invalid username or password"
                                    }
                                    """);
                        })

                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json;charset=UTF-8");

                            response.getWriter().write("""
                                    {
                                      "success": true,
                                      "message": "Logout successful"
                                    }
                                    """);
                        })
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "https://exam-web-sigma.vercel.app",
                "http://localhost:5173",
                "http://localhost:3000"
        ));

        configuration.setAllowCredentials(true);

        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}