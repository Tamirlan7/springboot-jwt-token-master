package com.tamirlan.jwt.auth.jwt.serializer;

import com.tamirlan.jwt.auth.jwt.model.RefreshToken;

public interface RefreshTokenSerializer {

    public String serialize(RefreshToken token);

}
