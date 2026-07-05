package com.armin.guitarTracker.user.service;

import com.armin.guitarTracker.user.dto.AuthenticationResponse;
import com.armin.guitarTracker.user.entity.Role;
import com.armin.guitarTracker.user.entity.Token;
import com.armin.guitarTracker.user.entity.TokenType;
import com.armin.guitarTracker.user.entity.User;
import com.armin.guitarTracker.user.repository.TokenRepository;
import com.armin.guitarTracker.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${app.jwt.refresh-token-expiration}")
    private Long refreshTokenExpirationDuration;

    public AuthenticationResponse create(User user, HttpServletResponse response) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        // TODO: add error handling, make transactional
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(savedUser);
        var jwtRefreshToken = jwtService.generateRefreshToken(savedUser);
        saveToken(savedUser, jwtToken, TokenType.ACCESS);
        saveToken(savedUser, jwtRefreshToken, TokenType.REFRESH);
        setRefreshTokenCookie(response, jwtRefreshToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(User user, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );
        var foundUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + user.getEmail()));
        var jwtToken = jwtService.generateToken(foundUser);
        var jwtRefreshToken = jwtService.generateRefreshToken(foundUser);
        invalidateExistingTokens(foundUser, TokenType.ACCESS);
        invalidateExistingTokens(foundUser, TokenType.REFRESH);
        saveToken(foundUser, jwtToken, TokenType.ACCESS);
        saveToken(foundUser, jwtRefreshToken, TokenType.REFRESH);
        setRefreshTokenCookie(response, jwtRefreshToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    private void invalidateExistingTokens(User user, TokenType tokenType) {
        var existingTokens = tokenRepository.findAllValidTokensByUserAndType(user.getId(), tokenType);
        if (existingTokens.isEmpty()) {
            return;
        }
        existingTokens.forEach(token -> {
            token.setIsExpired(true);
            token.setIsRevoked(true);
        });
        tokenRepository.saveAll(existingTokens);
    }

    private void saveToken(User user, String jwtToken, TokenType tokenType) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(tokenType)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Refresh token is missing");
            return;
        }
        final String userEmail = jwtService.extractUserEmail(refreshToken);
        if (userEmail == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid refresh token");
            return;
        }
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // Validate the refresh token against the DB record
        var isRefreshTokenValid = tokenRepository.findByToken(refreshToken)
                .map(t -> t.getTokenType() == TokenType.REFRESH && !t.getIsExpired() && !t.getIsRevoked())
                .orElse(false);

        if (!jwtService.isTokenValid(refreshToken, user) || !isRefreshTokenValid) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired refresh token");
            return;
        }

        // Rotate: invalidate old tokens, issue new pair
        invalidateExistingTokens(user, TokenType.ACCESS);
        invalidateExistingTokens(user, TokenType.REFRESH);

        var newAccessToken = jwtService.generateToken(user);
        var newRefreshToken = jwtService.generateRefreshToken(user);

        saveToken(user, newAccessToken, TokenType.ACCESS);
        saveToken(user, newRefreshToken, TokenType.REFRESH);
        setRefreshTokenCookie(response, newRefreshToken);

        var authResponse = AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .build();

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/api/auth")
                .sameSite("Lax")
                .maxAge(refreshTokenExpirationDuration / 1000)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
