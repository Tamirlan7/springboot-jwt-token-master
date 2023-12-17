package com.tamirlan.jwt.auth.jwt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record RefreshToken (
        UUID id,
        Instant expiresAt,
        Instant issuedAt,
        Long userId,
        List<String> authorities
) {

}
