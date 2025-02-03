package ru.app.apigateway.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.List;

public class UserAuthenticationBearer {

    public static Mono<Authentication> create(JwtHandler.VerificationResult verificationResult) {
        Claims claims = verificationResult.claims;
        String subject = claims.getSubject();

        List<String> roles = claims.get("roles", List.class);
        String username = claims.get("username", String.class);

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roles.get(0)));
        Long principalId = claims.get("user_id", Long.class);
        CustomPrincipal principal = new CustomPrincipal(principalId, username);

        return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(principal, null, authorities));
    }
}
