package com.tamirlan.jwt.auth.jwt.factory;

import com.tamirlan.jwt.auth.jwt.model.AccessToken;
import com.tamirlan.jwt.auth.jwt.model.RefreshToken;
import com.tamirlan.jwt.auth.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
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
        List<String> authorities = new ArrayList<>(authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        authorities.add("JWT_REFRESH");

        return RefreshToken.builder()
                .id(UUID.randomUUID())
                .authorities(authorities)
                .issuedAt(now)
                .expiresAt(now.plus(tokenTtl))
                .userId(1L)
                .build();
    }

    public RefreshToken generate(User user) {
        Instant now = Instant.now();
        List<String> authorities = new ArrayList<>(user
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        authorities.add("JWT_REFRESH");

        return RefreshToken.builder()
                .id(UUID.randomUUID())
                .expiresAt(now.plus(tokenTtl))
                .issuedAt(now)
                .authorities(authorities)
                .userId(user.getId())
                .build();
    }

}
