package com.SecureOrganDonation.secureorgandonation.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.Filter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuditLogFilter auditLogFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());
        
        http.cors(cors -> {});     
        
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth

                // Public endpoints (no token)
                .requestMatchers(
                        "/admin/login",
                        "/hospital/login",
                        "/hospital/register",
                        "/hospitals/approved",
                        "/user/register",
                        "/user/login",
                        "/health"
                ).permitAll()

             // ===== ADMIN ENDPOINTS =====
                .requestMatchers("/admin/hospitals/pending").hasRole("ADMIN")
                .requestMatchers("/admin/hospitals/all").hasRole("ADMIN")
                .requestMatchers("/admin/hospital/*/approve").hasRole("ADMIN")
                .requestMatchers("/admin/hospital/*/reject").hasRole("ADMIN")
                .requestMatchers("/admin/hospital/*").hasRole("ADMIN")
                .requestMatchers("/admin/user/*").hasRole("ADMIN")

                // ===== HOSPITAL ENDPOINTS =====
                .requestMatchers("/hospital/users/pending").hasRole("HOSPITAL")
                .requestMatchers("/hospital/user/*/approve").hasRole("HOSPITAL")
                .requestMatchers("/hospital/user/*/reject").hasRole("HOSPITAL")
				/* .requestMatchers("/notifications/**").hasRole("HOSPITAL") */

                // ===== USER ENDPOINTS =====
                .requestMatchers("/organ-request/**").hasRole("USER")
                .requestMatchers("/recipient-request/**").hasRole("USER")
                
                // ===== NOTIFICATIONS ENDPOINTS =====
                .requestMatchers("/notifications/**").authenticated()

                .anyRequest().authenticated()
        );

        
        
        http.addFilterBefore((Filter) jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore((Filter) auditLogFilter, UsernamePasswordAuthenticationFilter.class);



        return http.build();
    }
}