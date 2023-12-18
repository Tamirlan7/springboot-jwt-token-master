package com.tamirlan.jwt.auth.dto;

import lombok.Data;

public record Tokens(
    String accessToken,
    String refreshToken
) {
}
