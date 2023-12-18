package com.tamirlan.jwt.auth.service;

import com.tamirlan.jwt.auth.dto.RefreshDto;
import com.tamirlan.jwt.auth.dto.Tokens;
import com.tamirlan.jwt.auth.exception.InvalidTokenException;
import com.tamirlan.jwt.auth.exception.UserNotFoundException;
import com.tamirlan.jwt.auth.exception.UsernameAlreadyExistsException;
import com.tamirlan.jwt.auth.exception.WrongPasswordException;
import com.tamirlan.jwt.auth.jwt.deserializer.AccessTokenDeserializer;
import com.tamirlan.jwt.auth.jwt.deserializer.RefreshTokenDeserializer;
import com.tamirlan.jwt.auth.jwt.factory.AccessTokenFactory;
import com.tamirlan.jwt.auth.jwt.factory.RefreshTokenFactory;
import com.tamirlan.jwt.auth.jwt.model.AccessToken;
import com.tamirlan.jwt.auth.jwt.model.RefreshToken;
import com.tamirlan.jwt.auth.jwt.serializer.AccessTokenSerializer;
import com.tamirlan.jwt.auth.jwt.serializer.RefreshTokenSerializer;
import com.tamirlan.jwt.auth.model.Role;
import com.tamirlan.jwt.auth.model.User;
import com.tamirlan.jwt.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AccessTokenFactory accessTokenFactory;
    private final RefreshTokenFactory refreshTokenFactory;
    private final AccessTokenSerializer accessTokenSerializer;
    private final RefreshTokenSerializer refreshTokenSerializer;
    private final RefreshTokenDeserializer refreshTokenDeserializer;
    private final AccessTokenDeserializer accessTokenDeserializer;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Tokens login(User loginUser) {
        User user = userRepository.findByUsername(loginUser.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User with username " + loginUser.getUsername() + " not found"));

        if (!passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            throw new WrongPasswordException("Wrong password");
        }

        RefreshToken refreshToken = refreshTokenFactory.generate(user);
        AccessToken accessToken = accessTokenFactory.generate(refreshToken);

        return new Tokens(
            accessTokenSerializer.serialize(accessToken),
            refreshTokenSerializer.serialize(refreshToken)
        );
    }

    public Tokens register(User requestUser) {
        if (userRepository.findByUsername(requestUser.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("user with username " + requestUser.getUsername() + " already exists");
        }

        User user = userRepository.save(
            User.builder()
                    .username(requestUser.getUsername())
                    .password(passwordEncoder.encode(requestUser.getPassword()))
                    .role(Role.ROLE_USER)
                    .build()
        );


        RefreshToken refreshToken = refreshTokenFactory.generate(user);
        AccessToken accessToken = accessTokenFactory.generate(refreshToken);

        return new Tokens(
            accessTokenSerializer.serialize(accessToken),
            refreshTokenSerializer.serialize(refreshToken)
        );
    }

    public Tokens refresh(RefreshDto refreshDto) {
        if (accessTokenDeserializer.deserialize(refreshDto.refreshToken()) != null) {
            throw new InvalidTokenException("Invalid refreshToken, you passed the accessToken");
        }

        RefreshToken token = refreshTokenDeserializer.deserialize(refreshDto.refreshToken());

        if (token != null &&
                token.authorities().contains("JWT_REFRESH") &&
                Instant.now().isBefore(token.expiresAt())) {

            User user = userRepository.findById(token.userId())
                    .orElseThrow(() -> new UserNotFoundException("user with id " + token.userId() + " not found"));

            RefreshToken refreshToken = refreshTokenFactory.generate(user);
            AccessToken accessToken = accessTokenFactory.generate(refreshToken);

            return new Tokens(
                accessTokenSerializer.serialize(accessToken),
                refreshTokenSerializer.serialize(refreshToken)
            );
        }

        throw new InvalidTokenException("Invalid refreshToken");
    }
}
