package com.armin.guitarTracker.guitar.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table
public class Guitar {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String productUrl;
    @NotBlank
    private String title;
    @NotBlank
    private String storeName;

}
