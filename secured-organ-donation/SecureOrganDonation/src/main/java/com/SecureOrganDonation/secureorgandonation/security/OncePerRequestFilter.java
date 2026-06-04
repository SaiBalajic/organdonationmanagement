/*
 * // In pom.xml, add dependency (example using bucket4j-core):
 * 
 * <dependency> <groupId>com.github.vladimir-bukhtoyarov</groupId>
 * <artifactId>bucket4j-core</artifactId> <version>7.3.0</version> </dependency>
 * 
 * 
 * // New RateLimitFilter.java package
 * com.SecureOrganDonation.secureorgandonation.security;
 * 
 * import java.io.IOException; import java.time.Duration; import java.util.Map;
 * import java.util.concurrent.ConcurrentHashMap; import
 * javax.servlet.FilterChain; import javax.servlet.ServletException; import
 * javax.servlet.http.HttpServletRequest; import
 * javax.servlet.http.HttpServletResponse; import io.github.bucket4j.*; import
 * org.springframework.stereotype.Component; import
 * org.springframework.web.filter.OncePerRequestFilter;
 * 
 * @Component public class RateLimitFilter extends OncePerRequestFilter {
 * private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
 * 
 * @Override protected void doFilterInternal(HttpServletRequest request,
 * HttpServletResponse response, FilterChain filterChain) throws
 * ServletException, IOException { String ip = request.getRemoteAddr(); Bucket
 * bucket = cache.computeIfAbsent(ip, k -> { Bandwidth limit =
 * Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1))); return
 * Bucket4j.builder().addLimit(limit).build(); });
 * 
 * if (bucket.tryConsume(1)) { filterChain.doFilter(request, response); } else {
 * response.setStatus(429); response.getWriter().write("Too Many Requests"); } }
 * }
 */