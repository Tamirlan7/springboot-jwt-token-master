package com.tamirlan.jwt.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tamirlan.jwt.auth.config.JwtAuthenticationUserDetailsService;
import com.tamirlan.jwt.auth.jwt.deserializer.AccessTokenDeserializer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationConfigurer extends AbstractHttpConfigurer<JwtAuthenticationConfigurer, HttpSecurity> {
    private final JwtAuthenticationUserDetailsService userDetailsService;
    private final AccessTokenDeserializer accessTokenDeserializer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init(HttpSecurity builder) throws Exception {
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        var jwtAuthenticationConverter = new JwtAuthenticationConverter(accessTokenDeserializer);
        var authenticationFilter = new AuthenticationFilter(
            builder.getSharedObject(AuthenticationManager.class),
            jwtAuthenticationConverter
        );
        authenticationFilter.setSuccessHandler((request, response, authentication) ->
            response.setStatus(200)
        );

        var authenticationProvider = new PreAuthenticatedAuthenticationProvider();
        authenticationProvider.setPreAuthenticatedUserDetailsService(userDetailsService);

        builder.addFilterBefore(authenticationFilter, CsrfFilter.class)
                .authenticationProvider(authenticationProvider);
    }
}
