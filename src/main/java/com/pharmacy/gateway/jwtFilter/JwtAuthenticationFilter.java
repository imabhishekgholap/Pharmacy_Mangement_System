package com.pharmacy.gateway.jwtFilter;

import com.pharmacy.gateway.jwtUtil.JwtUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Claims claims = jwtUtil.extractClaims(token);

                if (!jwtUtil.isTokenExpired(claims)) {
                    String username = jwtUtil.getUsername(claims);
                    String role = jwtUtil.getRoles(claims);

                    var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
                    var authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContext securityContext = new SecurityContextImpl(authentication);

                    logger.debug("Authenticated user: {} with role: {}", username, role);

                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
                } else {
                    logger.warn("JWT token has expired for token: {}", token);
                }
            } catch (Exception e) {
                logger.error("Failed to parse or validate JWT token: {}", token, e);
                return Mono.error(new RuntimeException("Invalid JWT token"));
            }
        }

        return chain.filter(exchange);
    }
}
