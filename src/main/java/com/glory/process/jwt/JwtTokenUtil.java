package com.glory.process.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by cdy on 2017/9/10.
 */

@Component
public class JwtTokenUtil {

    @Value("${jwt.expire}")
    private int expire;

    private JWTHelper jwtHelper = new JWTHelper();

    public String generateToken(IJWTInfo jwtInfo) throws Exception {
        return jwtHelper.generateToken(jwtInfo,expire);
    }

}
