package com.armin.guitarTracker.user.repository;

import com.armin.guitarTracker.user.entity.Token;
import com.armin.guitarTracker.user.entity.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND t.tokenType = :tokenType AND t.isExpired = false AND t.isRevoked = false")
    List<Token> findAllValidTokensByUserAndType(UUID userId, TokenType tokenType);

    Optional<Token> findByToken(String token);
}