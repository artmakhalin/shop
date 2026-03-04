package com.ait.shop.security.controller;

import com.ait.shop.security.dto.LoginRequestDto;
import com.ait.shop.security.dto.TokenResponseDto;
import com.ait.shop.security.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import static com.ait.shop.constants.Constants.ACCESS_TOKEN_COOKIE_NAME;
import static com.ait.shop.constants.Constants.REFRESH_TOKEN_COOKIE_NAME;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public void login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        TokenResponseDto tokens = service.login(requestDto);

        Cookie  accessCookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, tokens.getAccessToken());
        accessCookie.setPath("/");
        accessCookie.setHttpOnly(true);
        response.addCookie(accessCookie);

        Cookie  refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, tokens.getRefreshToken());
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        response.addCookie(refreshCookie);
    }

    @PostMapping("/access")
    public void getNewAccessToken(HttpServletRequest request, HttpServletResponse response) {
        TokenResponseDto tokens = service.getAccessToken(request);

        Cookie  accessCookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, tokens.getAccessToken());
        accessCookie.setPath("/");
        accessCookie.setHttpOnly(true);
        response.addCookie(accessCookie);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        service.removeUserRefreshToken(request);
        
        Cookie  accessCookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, null);
        accessCookie.setPath("/");
        accessCookie.setHttpOnly(true);
        accessCookie.setMaxAge(0);
        response.addCookie(accessCookie);

        Cookie  refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
    }

    @GetMapping("/csrf")
    public CsrfToken csrfToken(CsrfToken csrfToken) {
        return csrfToken;
    }
}
