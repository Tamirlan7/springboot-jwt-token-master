package com.tamirlan.jwt.auth.jwt.deserializer;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.SignedJWT;
import com.tamirlan.jwt.auth.jwt.model.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.UUID;

public class DefaultAccessTokenDeserializer implements AccessTokenDeserializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAccessTokenDeserializer.class);

    private final JWSVerifier jwsVerifier;

    public DefaultAccessTokenDeserializer(JWSVerifier jwsVerifier) {
        this.jwsVerifier = jwsVerifier;
    }

    @Override
    public AccessToken deserialize(String strFormatToken) {
        try {
            var signedJWT = SignedJWT.parse(strFormatToken);

            if (signedJWT.verify(jwsVerifier)) {
                var claimsSet = signedJWT.getJWTClaimsSet();

                return AccessToken.builder()
                        .id(UUID.fromString(claimsSet.getJWTID()))
                        .expiresAt(claimsSet.getExpirationTime().toInstant())
                        .issuedAt(claimsSet.getIssueTime().toInstant())
                        .authorities(claimsSet.getStringListClaim("roles"))
                        .userId((Long) claimsSet.getClaim("userId"))
                        .build();
            }
        } catch (JOSEException | ParseException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return null;
    }
}
