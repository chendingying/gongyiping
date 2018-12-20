package com.glory.process.service;

import com.glory.process.jwt.JwtAuthenticationRequest;

import javax.servlet.http.HttpSession;

/**
 * Created by CDZ on 2018/11/27.
 */
public interface AuthService {
    String login(JwtAuthenticationRequest authenticationRequest, HttpSession session) throws Exception;
}
