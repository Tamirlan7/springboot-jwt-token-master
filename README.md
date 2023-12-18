# Spring Boot Application with JWT Authentication

#### Admin Credentials:
Username: admin <br />
Password: admin 

## Used Java Libraries:
- Spring Data JPA
- Spring Security
- Sprint Web
- PostgreSQL Driver
- Docker Compose Support
- Lombok
- Nimbus Jose Jwt

## Authentication Process

![authentication diagram](./authentication_diagram.jpg)

1. A client sends a request to /api/v1/user which is can be reached if you are authenticated and you have certain roles
2. The request goes through several Security Filters by Spring Security
3. Once it reaches AuthenticationFilter, the filter that is responsible for determining if a user is authenticated or not <br /> there is AuthenticationConverter that was replaced by my own converter called JwtAuthenticationConverter

JwtAuthenticationConverter's job is to extract Bearer token from the request headers "Authorization", 
deserialize it, and return PreAuthenticatedAuthenticationToken to AuthenticationFilter which will later authenticate this token
```java
@RequiredArgsConstructor
public class JwtAuthenticationConverter implements AuthenticationConverter {

    private final AccessTokenDeserializer accessTokenDeserializer;

    @Override
    public Authentication convert(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer ")) {
            String strFormatToken = header.substring(7);
            AccessToken token = accessTokenDeserializer.deserialize(strFormatToken);

            if (Instant.now().isBefore(token.expiresAt())) {
                return new PreAuthenticatedAuthenticationToken(
                        token,
                        strFormatToken,
                        token.authorities().stream().map(SimpleGrantedAuthority::new).toList()
                );
            }
        }

        return null;
    }
}
```
5. When AuthenticationFilter gets the token, it will search for a user from UserDetailsService using the token, the UserDetailsService is also customized since the JwtAuthenticationConverter return unusual token 
```java
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
```
6. if user is authenticated, the request will get to the next filter called AuthorizationFilter where it will check if a user has a permission for the source

