package com.tamirlan.jwt.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshDto(
        @NotBlank(message = "refreshToken is required")
        String refreshToken
) {

}

