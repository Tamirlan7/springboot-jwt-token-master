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

1. Client sends a request to /api/v1/user which is can be reached if you are authenticated and you have certain roles
2. The request goes through several Security Filters by Spring Security
3. It reaches AuthenticationFilter, the filter that responsible for determining if a user is authenticated or not
4. there is AuthenticationConverter that was replaced by my own converter called JwtAuthenticationConverter

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
5. something
6. ![authentication diagram](./authentication_diagram)
