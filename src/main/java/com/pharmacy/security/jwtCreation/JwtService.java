package com.pharmacy.security.jwtCreation;

import com.pharmacy.security.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

	//Injected SPRING_APP_JWTSECRET from application.properties
	@Value("${SPRING_APP_JWTSECRET}")
    private String secretKey;
	
	
	 public JwtService() {
	        System.out.println("JwtService created. secretKey: " + secretKey);
	    }
    @Value("${spring.app.jwtIssuer}")
    private String jwtIssuer;

    //Injected expiration time from application.properties
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private static final String CLAIM_ROLE = "role";

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_ROLE, user.getRole());

        return Jwts
                .builder()
                .claims().add(claims)
                .subject(user.getUserName())
                .issuer(jwtIssuer) //Replaced TOKEN_ISSUER with jwtIssuer
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Using jwtExpiration from config
                .and()
                .signWith(generateKey())
                .compact();
    }

    public SecretKey generateKey() {
        byte[] arr = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(arr);
    }

    public String extractUsername(String jwtToken) {
        return extractClaims(jwtToken, Claims::getSubject);
    }

    private String extractIssuer(String jwtToken) {
        return extractClaims(jwtToken, Claims::getIssuer);
    }

    private <T> T extractClaims(String jwtToken, Function<Claims, T> claimResolver) {
        Claims claims = extractClaims(jwtToken);
        return claimResolver.apply(claims);
    }

    private Claims extractClaims(String jwtToken) {
        return Jwts.parser().verifyWith(generateKey()).build().parseSignedClaims(jwtToken).getPayload();
    }

    public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
        final String username = extractUsername(jwtToken);
        final String issuer = extractIssuer(jwtToken);

        return (username.equals(userDetails.getUsername())
                && jwtIssuer.equals(issuer) //Replaced TOKEN_ISSUER with jwtIssuer
                && !isTokenExpired(jwtToken));
    }

    private boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    private Date extractExpiration(String jwtToken) {
        return extractClaims(jwtToken, Claims::getExpiration);
    }
}