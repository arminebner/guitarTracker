package com.armin.guitarTracker.User.Service;

import com.armin.guitarTracker.User.Entities.Role;
import com.armin.guitarTracker.User.Entities.User;
import com.armin.guitarTracker.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
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
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
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
        var foundUser = repository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + user.getEmail()));
        var jwtToken = jwtService.generateToken(foundUser);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }
}
