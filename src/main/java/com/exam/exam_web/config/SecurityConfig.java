package com.exam.exam_web.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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

                .authorizeHttpRequests(auth -> auth

                        // Cho phép preflight request
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Login + static resources
                        .requestMatchers(
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        // API public
                        .requestMatchers(
                                "/api/exams/**",
                                "/api/courses/**",
                                "/api/teacher/questions/**"
                        ).permitAll()

                        .requestMatchers("/api/**").permitAll()

                        // API teacher
                        .requestMatchers("/api/teacher/**")
                        .hasAnyRole("TEACHER", "ADMIN")

                        // MVC pages
                        .requestMatchers("/students/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/questions/**")
                        .hasRole("TEACHER")

                        .requestMatchers("/calendar/**")
                        .hasRole("STUDENT")

                        .requestMatchers(
                                "/courses/**",
                                "/exams/**",
                                "/history/**"
                        ).authenticated()

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

                            response.getWriter().write("""
                                    {
                                      "success": true,
                                      "message": "Login successful"
                                    }
                                    """);
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

        configuration.setAllowedOriginPatterns(List.of(
                "https://*.vercel.app",
                "http://localhost:5173",
                "http://localhost:3000"
        ));

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