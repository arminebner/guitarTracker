package com.armin.guitarTracker.User.Service;

import com.armin.guitarTracker.User.Entities.Role;
import com.armin.guitarTracker.User.Entities.Token;
import com.armin.guitarTracker.User.Entities.User;
import com.armin.guitarTracker.User.Repository.TokenRepository;
import com.armin.guitarTracker.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Set default role if not provided
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        // TODO: add error handling, make transactional
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        saveToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(User user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );
        var foundUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + user.getEmail()));
        var jwtToken = jwtService.generateToken(foundUser);
        invalidateExistingTokens(foundUser);
        saveToken(foundUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    private void invalidateExistingTokens(User user) {
        var existingTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (existingTokens.isEmpty()) {
            return;
        }
        existingTokens.forEach(token -> {
            token.setIsExpired(true);
            token.setIsRevoked(true);
        });
        tokenRepository.saveAll(existingTokens);
    }

    private void saveToken(User savedUser, String jwtToken) {
        var token = Token.builder()
                .user(savedUser)
                .token(jwtToken)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(token);
    }
}
