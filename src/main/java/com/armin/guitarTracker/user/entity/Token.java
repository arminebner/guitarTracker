package com.armin.guitarTracker.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    @Id
    @GeneratedValue()
    private Integer id;

    private String token;

    private Boolean isExpired;

    private Boolean isRevoked;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
