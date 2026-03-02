package com.ait.shop.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class TokenService {

    private final SecretKey accessKey;
    private final SecretKey refreshKey;

    public TokenService(
            @Value("${KEY_PHRASE_ACCESS}") String accessPhrase,
            @Value("${KEY_PHRASE_REFRESH}") String refreshPhrase
    ) {
        accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessPhrase));
        refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshPhrase));
    }

    public String generateAccessToken(String email) {
        return generateToken(email, accessKey, 15 * 60 * 1000);
    }

    public String generateRefreshToken(String email) {
        return generateToken(email, refreshKey, 24 * 60 * 60 * 1000);
    }

    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, accessKey);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken,refreshKey);
    }

    public Claims getAccessClaims(String accessToken) {
        return getClaims(accessToken, accessKey);
    }

    public Claims getRefreshClaims(String refreshToken) {
        return getClaims(refreshToken, refreshKey);
    }

    public String getTokenFromRequest(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private String generateToken(String email, SecretKey key, int expirationMillis) {
        Date now = new Date(); // now
        Date expiration = new Date(now.getTime() + expirationMillis); //future

        return Jwts.builder()
                .subject(email)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    private boolean validateToken(String token, SecretKey key) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
