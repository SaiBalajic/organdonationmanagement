//package com.SecureOrganDonation.secureorgandonation.security;
//
//import java.io.IOException;
//import java.time.Duration;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import io.github.bucket4j.Bandwidth;
//import io.github.bucket4j.Bucket;
//import io.github.bucket4j.Refill;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@Component
//public class RateLimitFilter extends OncePerRequestFilter {
//
//    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
//
//    private Bucket createBucket() {
//        Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
//        return Bucket.builder().addLimit(limit).build();
//    }
//
//    private Bucket resolveBucket(String key) {
//        return cache.computeIfAbsent(key, k -> createBucket());
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String path = request.getRequestURI();
//
//        boolean isLogin = path.equals("/admin/login") ||
//                          path.equals("/hospital/login") ||
//                          path.equals("/user/login");
//
//        if (!isLogin) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String ip = request.getRemoteAddr();
//        Bucket bucket = resolveBucket(ip + ":" + path);
//
//        if (bucket.tryConsume(1)) {
//            filterChain.doFilter(request, response);
//        } else {
//            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
//            response.getWriter().write("Too many requests. Please try again later.");
//        }
//    }
//}
