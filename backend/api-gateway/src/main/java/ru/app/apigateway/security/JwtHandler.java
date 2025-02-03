package ru.app.apigateway.security;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import ru.app.apigateway.enums.TokenType;
import ru.app.apigateway.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Base64;

@Component
public class JwtHandler {

    private final String secret;

    public JwtHandler(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }

    public Mono<VerificationResult> check(String token) {
        VerificationResult verificationResult = new VerificationResult(getClaimsFromToken(token, TokenType.ACCESS), token);

        return Mono.just(verificationResult)
                .onErrorResume(e -> Mono.error(new UnauthorizedException(e.getMessage())));
    }

    public Claims getClaimsFromToken(String token, TokenType requiredTokenType) {

        byte[] keyBytes = Decoders.BASE64.decode(secret);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String tokenType = claims.get("token_type", String.class);
        if (tokenType == null || !TokenType.valueOf(tokenType).equals(requiredTokenType)) {
            throw new IllegalArgumentException("Invalid token type: " + tokenType);
        }

        return claims;
    }


    public static class VerificationResult {
        public final Claims claims;
        public final String token;

        public VerificationResult(Claims claims, String token) {
            this.claims = claims;
            this.token = token;
        }
    }
}
