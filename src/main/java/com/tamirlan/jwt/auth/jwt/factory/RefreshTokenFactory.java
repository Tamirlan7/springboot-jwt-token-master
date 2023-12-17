package com.tamirlan.jwt.auth.jwt.factory;

import com.tamirlan.jwt.auth.jwt.model.RefreshToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class RefreshTokenFactory {

    private final Duration tokenTtl;

    public RefreshTokenFactory(@Value("${jwt.refresh-token-expiration}") String refreshTokenExpiration) {
        int duration = Integer.parseInt(refreshTokenExpiration.substring(0, refreshTokenExpiration.length() - 1));

        switch (refreshTokenExpiration.charAt(refreshTokenExpiration.length() - 1)) {
            case 'm':
                this.tokenTtl = Duration.ofMinutes(duration);
                break;
            case 'h':
                this.tokenTtl = Duration.ofHours(duration);
                break;
            case 'd':
                this.tokenTtl = Duration.ofDays(duration);
                break;
            default:
                // if in the application.yml jwt.refresh-token-expiration is not specified
                this.tokenTtl = Duration.ofMinutes(15);
        }
    }

    public RefreshToken generate(Authentication authentication) {
        Instant now = Instant.now();
        return RefreshToken.builder()
                .id(UUID.randomUUID())
                .authorities(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .issuedAt(now)
                .expiresAt(now.plus(tokenTtl))
                .userId(1L)
                .build();
    }

}
