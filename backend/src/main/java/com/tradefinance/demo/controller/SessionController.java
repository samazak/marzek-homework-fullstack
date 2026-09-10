package com.tradefinance.demo.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {
    public static final String CSRF_PATH = "/api/csrf";
    public static final String SESSION_PATH = "/api/session";

    public record Token(String headerName, String token) {}

    @GetMapping(CSRF_PATH)
    public Token csrf(CsrfToken token) { return new Token(token.getHeaderName(), token.getToken()); }

    @GetMapping(SESSION_PATH)
    public void checkSession() { }
}
