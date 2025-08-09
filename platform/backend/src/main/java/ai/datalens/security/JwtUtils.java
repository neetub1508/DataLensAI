package ai.datalens.security;

import ai.datalens.constants.JwtTokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.security.jwt.secret-key}")
    private String jwtSecret;

    @Value("${spring.security.jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${spring.security.jwt.refresh-expiration}")
    private long refreshExpirationMs;

    @Value("${spring.security.jwt.verification-expiration:86400000}") // 24 hours default
    private long verificationExpirationMs;

    @Value("${spring.security.jwt.password-reset-expiration:3600000}") // 1 hour default
    private long passwordResetExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwtToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateTokenFromUserId(userPrincipal.getId().toString(), jwtExpirationMs);
    }

    public String generateRefreshToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateTokenFromUserId(userPrincipal.getId().toString(), refreshExpirationMs);
    }

    public String generateTokenFromUserId(String userId, long expirationMs) {
        Date expiryDate = new Date(System.currentTimeMillis() + expirationMs);

        return Jwts.builder()
            .subject(userId)
            .issuedAt(new Date())
            .expiration(expiryDate)
            .signWith(getSigningKey())
            .compact();
    }

    public String getUserIdFromJwtToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();

        return claims.getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();

        return claims.getExpiration();
    }

    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }

    // New methods for AuthService
    public String generateAccessToken(UserPrincipal userPrincipal) {
        return generateTokenFromEmail(userPrincipal.getEmail(), jwtExpirationMs, JwtTokenType.ACCESS);
    }

    public String generateRefreshToken(UserPrincipal userPrincipal) {
        return generateTokenFromEmail(userPrincipal.getEmail(), refreshExpirationMs, JwtTokenType.REFRESH);
    }

    public String generateVerificationToken(String email) {
        return generateTokenFromEmail(email, verificationExpirationMs, JwtTokenType.VERIFICATION);
    }

    public String generatePasswordResetToken(String email) {
        return generateTokenFromEmail(email, passwordResetExpirationMs, JwtTokenType.PASSWORD_RESET);
    }

    private String generateTokenFromEmail(String email, long expirationMs, String tokenType) {
        Date expiryDate = new Date(System.currentTimeMillis() + expirationMs);

        return Jwts.builder()
            .subject(email)
            .claim("type", tokenType)
            .issuedAt(new Date())
            .expiration(expiryDate)
            .signWith(getSigningKey())
            .compact();
    }

    public boolean validateRefreshToken(String token) {
        return validateTokenOfType(token, JwtTokenType.REFRESH);
    }

    public boolean validateVerificationToken(String token) {
        return validateTokenOfType(token, JwtTokenType.VERIFICATION);
    }

    public boolean validatePasswordResetToken(String token) {
        return validateTokenOfType(token, JwtTokenType.PASSWORD_RESET);
    }

    private boolean validateTokenOfType(String token, String expectedType) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

            String tokenType = claims.get("type", String.class);
            return expectedType.equals(tokenType);
        } catch (Exception e) {
            logger.error("Invalid {} token: {}", expectedType, e.getMessage());
            return false;
        }
    }

    public String getEmailFromRefreshToken(String token) {
        return getEmailFromTokenOfType(token, JwtTokenType.REFRESH);
    }

    public String getEmailFromVerificationToken(String token) {
        return getEmailFromTokenOfType(token, JwtTokenType.VERIFICATION);
    }

    public String getEmailFromPasswordResetToken(String token) {
        return getEmailFromTokenOfType(token, JwtTokenType.PASSWORD_RESET);
    }

    private String getEmailFromTokenOfType(String token, String expectedType) {
        Claims claims = Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();

        String tokenType = claims.get("type", String.class);
        if (!expectedType.equals(tokenType)) {
            throw new IllegalArgumentException("Invalid token type. Expected: " + expectedType + ", Got: " + tokenType);
        }

        return claims.getSubject();
    }

    public long getAccessTokenExpiration() {
        return jwtExpirationMs;
    }

    public boolean validateAccessToken(String token) {
        return validateTokenOfType(token, JwtTokenType.ACCESS);
    }

    public String getEmailFromAccessToken(String token) {
        return getEmailFromTokenOfType(token, JwtTokenType.ACCESS);
    }
}