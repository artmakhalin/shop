package com.ait.shop.security.service;

import com.ait.shop.constants.Constants;
import com.ait.shop.exceptions.types.AuthorizationException;
import com.ait.shop.security.dto.LoginRequestDto;
import com.ait.shop.security.dto.TokenResponseDto;
import com.ait.shop.service.interfaces.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ait.shop.constants.Constants.REFRESH_TOKEN_COOKIE_NAME;

@Service
public class AuthService {

    private final UserService userService;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Map<String, String> refreshStorage;

    public AuthService(UserService userService, TokenService tokenService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        refreshStorage = new ConcurrentHashMap<>();
    }

    public TokenResponseDto login(LoginRequestDto requestDto) {
        String email = requestDto.getEmail();
        UserDetails userDetails = userService.loadUserByUsername(email);

        if (!userDetails.isEnabled()) {
            throw new AuthorizationException("Email is not confirmed");
        }

        if (passwordEncoder.matches(requestDto.getPassword(), userDetails.getPassword())) {
            String accessToken = tokenService.generateAccessToken(email);
            String refreshToken = tokenService.generateRefreshToken(email);
            refreshStorage.put(email, refreshToken);

            return new TokenResponseDto(accessToken, refreshToken);
        } else {
            throw new AuthorizationException("Password is incorrect");
        }
    }

    public TokenResponseDto getAccessToken(HttpServletRequest request) {
        String refreshToken = tokenService.getTokenFromRequest(request, REFRESH_TOKEN_COOKIE_NAME);

        if (refreshToken != null && tokenService.validateRefreshToken(refreshToken)) {
            Claims claims = tokenService.getRefreshClaims(refreshToken);
            String email = claims.getSubject();
            String savedRefreshToken = refreshStorage.get(email);

            if (savedRefreshToken != null && savedRefreshToken.equals(refreshToken)) {
                String accessToken = tokenService.generateAccessToken(email);
                return new TokenResponseDto(accessToken);
            }
        }

        throw new AuthorizationException("Refresh token is invalid");
    }

    public void removeUserRefreshToken(HttpServletRequest request) {
        String refreshToken = tokenService.getTokenFromRequest(request, REFRESH_TOKEN_COOKIE_NAME);

        if (refreshToken != null && tokenService.validateRefreshToken(refreshToken)) {
            Claims claims = tokenService.getRefreshClaims(refreshToken);
            String email = claims.getSubject();
            refreshStorage.remove(email);
        }
    }
}
