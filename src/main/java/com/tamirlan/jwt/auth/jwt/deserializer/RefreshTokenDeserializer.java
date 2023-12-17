package com.tamirlan.jwt.auth.jwt.deserializer;

import com.tamirlan.jwt.auth.jwt.model.RefreshToken;

public interface RefreshTokenDeserializer {

    public RefreshToken deserialize(String strFormatToken);

}
