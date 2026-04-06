package com.armin.guitarTracker.user.controller;

import com.armin.guitarTracker.user.dto.AuthenticationResponse;
import com.armin.guitarTracker.user.entity.User;
import com.armin.guitarTracker.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
// TODO: Versioning
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        System.out.println("Health check endpoint hit");
        return ResponseEntity.ok("Auth service is running");
    }

    @PostMapping("/register")
    public AuthenticationResponse create(@RequestBody User user) {
        System.out.println("Received register request for email: " + user.getEmail());
        return service.create(user);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody User user) {
        System.out.println("========== AUTHENTICATE ENDPOINT REACHED ==========");
        System.out.println("Received authentication request for email: " + user.getEmail());
        System.out.println("User object: " + user);
        System.out.println("=============================================");
        try {
            AuthenticationResponse response = service.authenticate(user);
            System.out.println("Authentication successful");
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        } catch (Exception e) {
            System.out.println("Unexpected error during authentication: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("Received refresh token request");
        try {
            service.refreshToken(request, response);
            System.out.println("Refresh token successful");
        } catch (AuthenticationException e) {
            System.out.println("Refresh token failed: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Invalid refresh token: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error during refresh token: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write("Error refreshing token: " + e.getMessage());
        }

    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        System.out.println("Exception handler called for: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Authentication failed: " + e.getMessage());
    }
}
