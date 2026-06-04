package com.SecureOrganDonation.secureorgandonation.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.SecureOrganDonation.secureorgandonation.model.AuditLog;
import com.SecureOrganDonation.secureorgandonation.repository.AuditLogRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuditLogFilter extends OncePerRequestFilter {

    private final AuditLogRepository auditLogRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

         //Skip logging for these
        if (path.equals("/health") ||
            path.equals("/admin/login") ||
            path.equals("/hospital/login") ||
            path.equals("/user/login")) {

            filterChain.doFilter(request, response);
            return;
        }
        
        
        
        
        

        // Log only important methods
        boolean shouldLog = method.equals("POST") || method.equals("PUT") || method.equals("DELETE");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (shouldLog && auth != null && auth.isAuthenticated()) {

            String email = auth.getName(); // subject/email
            String actorId = auth.getCredentials() != null ? auth.getCredentials().toString() : null;

            String role = auth.getAuthorities().stream()
                    .findFirst()
                    .map(a -> a.getAuthority())
                    .orElse("UNKNOWN");
            String action = resolveAction(role, method, path);


            AuditLog log = AuditLog.builder()
                    .actorEmail(email)
                    .actorId(actorId)
                    .actorRole(role)
                    .action(action)
                    .endpoint(path)
                    .httpMethod(method)
                    .ipAddress(request.getRemoteAddr())
                    .createdAt(LocalDateTime.now())
                    .build();

            auditLogRepository.save(log);
        }

        filterChain.doFilter(request, response);
    }
    
    
    private String resolveAction(String role, String method, String path) {

        // Remove ROLE_ prefix
        role = role.replace("ROLE_", "");

        if (role.equals("ADMIN")) {
            if (method.equals("PUT") && path.contains("/approve"))
                return "ADMIN_APPROVE_ACTION";

            if (method.equals("PUT") && path.contains("/reject"))
                return "ADMIN_REJECT_ACTION";

            if (method.equals("DELETE") && path.contains("/user"))
                return "ADMIN_REMOVE_USER";

            if (method.equals("DELETE") && path.contains("/hospital"))
                return "ADMIN_REMOVE_HOSPITAL";
        }

        if (role.equals("USER")) {
            if (method.equals("POST") && path.contains("/organ-request"))
                return "USER_CREATE_ORGAN_REQUEST";

            if (method.equals("POST") && path.contains("/recipient-request"))
                return "USER_CREATE_RECIPIENT_REQUEST";
        }

        if (role.equals("HOSPITAL")) {
            if (method.equals("PUT") && path.contains("/approve"))
                return "HOSPITAL_APPROVE_USER";

            if (method.equals("PUT") && path.contains("/reject"))
                return "HOSPITAL_REJECT_USER";
        }

        return role + "_" + method + "_ACTION";
    }

    
    
    
}
