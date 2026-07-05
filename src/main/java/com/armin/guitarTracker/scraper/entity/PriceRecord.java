package com.armin.guitarTracker.scraper.entity;

import com.armin.guitarTracker.product.entity.Product;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table
@Builder
public class PriceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private BigDecimal price;

    @ManyToOne()
    @JoinColumn(name = "product_id")
    private Product product;

}
