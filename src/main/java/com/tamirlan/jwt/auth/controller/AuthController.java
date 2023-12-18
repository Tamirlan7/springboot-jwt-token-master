package com.tamirlan.jwt.auth.controller;

import com.tamirlan.jwt.auth.dto.RefreshDto;
import com.tamirlan.jwt.auth.dto.Tokens;
import com.tamirlan.jwt.auth.model.User;
import com.tamirlan.jwt.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Tokens> login(@RequestBody User user) {
        return ResponseEntity.ok(authService.login(user));
    }

    @PostMapping("/register")
    public ResponseEntity<Tokens> register(@RequestBody User user) {
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Tokens> refresh(@RequestBody RefreshDto refreshDto) {
        return ResponseEntity.ok(authService.refresh(refreshDto));
    }
}
