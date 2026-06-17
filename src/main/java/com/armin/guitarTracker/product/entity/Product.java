package com.armin.guitarTracker.product.entity;

import com.armin.guitarTracker.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String productUrl;

    @NotBlank
    private String title;

    @NotBlank
    private String storeName;
    
    @NotBlank
    private String priceSelector;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    // prevents error because of lazy-loading tokens in user entity
    @JsonIgnore
    private User user;

}

