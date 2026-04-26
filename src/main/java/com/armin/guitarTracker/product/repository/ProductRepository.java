package com.armin.guitarTracker.product.repository;

import com.armin.guitarTracker.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p WHERE p.user.id = :id")
    List<Product> findAllByUserId(UUID id);
}
