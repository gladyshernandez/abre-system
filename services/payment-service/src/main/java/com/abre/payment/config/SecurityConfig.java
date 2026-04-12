package com.abre.payment.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Value("${demo.api-key:test-key}")
    private String demoApiKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/demo/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new DemoKeyFilter(demoApiKey), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private static class DemoKeyFilter extends OncePerRequestFilter {
        private final String validKey;

        DemoKeyFilter(String validKey) {
            this.validKey = validKey;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws IOException, ServletException {
            if (request.getRequestURI().startsWith("/demo")) {
                String providedKey = request.getHeader("X-Demo-Key");
                if (!validKey.equals(providedKey)) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Invalid or missing X-Demo-Key\"}");
                    return;
                }
            }
            filterChain.doFilter(request, response);
        }
    }

}
