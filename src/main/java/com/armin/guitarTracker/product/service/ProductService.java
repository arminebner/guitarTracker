package com.armin.guitarTracker.product.service;

import com.armin.guitarTracker.product.entity.Product;
import com.armin.guitarTracker.product.repository.ProductRepository;
import com.armin.guitarTracker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;

    public Product create(Product product) {
        // TODO safe casting ?
        User user = (User) Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                .getPrincipal();
        product.setUser(user);
        return repository.save(product);
    }

    public List<Product> getAllByUserId(UUID userId) {
        return repository.findAllByUserId(userId);
    }
}

