package com.pharmacy.gateway.config;

import com.pharmacy.gateway.jwtFilter.JwtAuthenticationFilter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        System.out.println("Enter in SecurityWebFilterChain");

        return http.authorizeExchange(exchange -> exchange
                        // Public end-points exposed via API Gateway
                        .pathMatchers("/user-management-service/register").permitAll()
                        .pathMatchers("/user-management-service/login").permitAll()
                        .pathMatchers("/user-management-service/users").permitAll()

                        // Protected end-points
                        .pathMatchers("/api/drugs/view").hasAnyRole("ADMIN", "DOCTOR")
                        .pathMatchers("/api/drugs/view").hasAnyRole("ADMIN", "DOCTOR")
                        .pathMatchers("/api/drugs/search").hasAnyRole("ADMIN", "DOCTOR")
                        .pathMatchers("/api/drugs/batch/{batchId}").hasAnyRole("ADMIN", "DOCTOR")

                        .pathMatchers("/api/drugs/**").hasRole("ADMIN")
                        
                        .pathMatchers("/api/orders/add").hasAnyRole("ADMIN","DOCTOR")
                        .pathMatchers("/api/orders/price-stock").hasAnyRole("ADMIN","DOCTOR")
                        .pathMatchers("/api/orders/**").hasRole("ADMIN")

                        
                        .pathMatchers("/api/sales/**").hasRole("ADMIN")
                       
                        .pathMatchers("/api/supplierinventory/**").hasRole("ADMIN")

                        .anyExchange().permitAll()
                )
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .csrf(csrf -> csrf.disable())
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
        
        	
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", config);
        return src;
      }
}
