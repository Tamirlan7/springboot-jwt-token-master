package com.tamirlan.jwt.auth.jwt;

import com.tamirlan.jwt.auth.jwt.serializer.AccessTokenSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final AccessTokenSerializer tokenSerializer;
    public String generateAccessToken() {
        return null;
    }
    public String generateRefreshToken() {
        return null;
    }
}
