package com.tamirlan.jwt.auth.config;

import com.tamirlan.jwt.auth.exception.UserNotFoundException;
import com.tamirlan.jwt.auth.jwt.model.AccessToken;
import com.tamirlan.jwt.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationUserDetailsService implements
        AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
        if (token.getPrincipal() instanceof AccessToken accessToken) {
            return userRepository.findById(accessToken.userId())
                    .orElseThrow(() -> new UserNotFoundException("User with id + " + accessToken.userId() + " not found"));
        }

        throw new UserNotFoundException("PreAuthenticatedAuthenticationToken.getPrincipal() must be instance of Access Token");
    }
}
