/*
 * Copyright (c) 2016 xiaomaihd and/or its affiliates.All Rights Reserved.
 *            http://www.xiaomaihd.com
 */
package com.glory.process.jwt;

import com.glory.process.service.impl.PermissionService;
import com.glory.process.vo.PermissionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YangFan on 2016/11/28 上午10:19.
 * <p/>
 */
@Component
public class JsonWebTokenAuthenticationProvider implements AuthenticationProvider {

    private JsonWebTokenUtility tokenService = new JsonWebTokenUtility();

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private UserAuthUtil userAuthUtil;

    private Logger log = LoggerFactory.getLogger(JsonWebTokenAuthentication.class);
    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        log.info("check token and user permission....");
        Authentication authenticatedUser = null;
        if (authentication.getClass().
                isAssignableFrom(PreAuthenticatedAuthenticationToken.class)
                && authentication.getPrincipal() != null) {
            String tokenHeader = (String) authentication.getPrincipal();
            UserDetails userDetails = parseToken(tokenHeader);
            if (userDetails != null) {
                authenticatedUser =
                        new JsonWebTokenAuthentication(userDetails, tokenHeader);
            }
        } else {
            authenticatedUser = authentication;
        }
        List<PermissionInfo> permissionIfs = permissionService.getAllPermission();
        // 判断资源是否启用权限约束
//        Stream<PermissionInfo> stream = getPermissionIfs(requestUri, method, permissionIfs);
//        List<PermissionInfo> result = stream.collect(Collectors.toList());
//        PermissionInfo[] permissions = result.toArray(new PermissionInfo[]{});
//        if (permissions.length > 0) {
//            if (checkUserPermission(permissions, serverWebExchange, user)) {
//                return getVoidMono(serverWebExchange, new TokenForbiddenResponse("User Forbidden!Does not has Permission!"));
//            }
//        }
        return authenticatedUser;
    }

    private UserDetails parseToken(String tokenHeader) {

        UserDetails principal = null;
        AuthTokenDetails authTokenDetails =
                tokenService.parseAndValidate(tokenHeader);

        if (authTokenDetails != null) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            // userId介入Spring Security
            principal = new User(authTokenDetails.getId().toString(), "",
                    authorities);
        }
        return principal;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return
                authentication.isAssignableFrom(
                        PreAuthenticatedAuthenticationToken.class)||
                        authentication.isAssignableFrom(
                                JsonWebTokenAuthentication.class);
    }

}
