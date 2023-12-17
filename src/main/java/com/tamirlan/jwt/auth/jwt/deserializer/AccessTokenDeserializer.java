package com.tamirlan.jwt.auth.jwt.deserializer;

import com.tamirlan.jwt.auth.jwt.model.AccessToken;

public interface AccessTokenDeserializer {
    public AccessToken deserialize(String strFormatToken);
}
