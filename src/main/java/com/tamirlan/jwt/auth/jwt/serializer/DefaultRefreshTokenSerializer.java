package com.tamirlan.jwt.auth.jwt.serializer;

import com.nimbusds.jose.*;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.tamirlan.jwt.auth.jwt.model.RefreshToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;

public class DefaultRefreshTokenSerializer implements RefreshTokenSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRefreshTokenSerializer.class);

    private final JWEEncrypter jweEncrypter;

    private JWEAlgorithm jweAlgorithm = JWEAlgorithm.DIR;

    private EncryptionMethod encryptionMethod = EncryptionMethod.A192GCM;

    public DefaultRefreshTokenSerializer(JWEEncrypter jweEncrypter) {
        this.jweEncrypter = jweEncrypter;
    }

    public DefaultRefreshTokenSerializer(JWEEncrypter jweEncrypter, JWEAlgorithm jweAlgorithm, EncryptionMethod encryptionMethod) {
        this.jweEncrypter = jweEncrypter;
        this.jweAlgorithm = jweAlgorithm;
        this.encryptionMethod = encryptionMethod;
    }

    @Override
    public String serialize(RefreshToken token) {
        var jwsHeader = new JWEHeader.Builder(this.jweAlgorithm, this.encryptionMethod)
                .keyID(token.id().toString())
                .build();

        var claimsSet = new JWTClaimsSet.Builder()
                .expirationTime(java.sql.Date.from(token.expiresAt()))
                .issueTime(Date.from(token.issuedAt()))
                .jwtID(token.id().toString())
                .claim("roles", token.authorities())
                .claim("userId", token.userId())
                .build();

        var encryptedJWT = new EncryptedJWT(jwsHeader, claimsSet);

        try {
            encryptedJWT.encrypt(this.jweEncrypter);

            return encryptedJWT.serialize();
        } catch (JOSEException exception) {
            LOGGER.error(exception.getMessage(), exception);
        }

        return null;
    }

    public void setJweAlgorithm(JWEAlgorithm jweAlgorithm) {
        this.jweAlgorithm = jweAlgorithm;
    }

    public void setEncryptionMethod(EncryptionMethod encryptionMethod) {
        this.encryptionMethod = encryptionMethod;
    }

}
