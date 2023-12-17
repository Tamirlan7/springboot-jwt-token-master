package com.tamirlan.jwt.auth.jwt.serializer;

import com.tamirlan.jwt.auth.jwt.model.AccessToken;

public interface AccessTokenSerializer {
    public String serialize(AccessToken token);
}
