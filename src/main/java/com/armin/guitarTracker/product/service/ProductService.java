package com.armin.guitarTracker.product.service;

import com.armin.guitarTracker.product.entity.Product;
import com.armin.guitarTracker.product.repository.ProductRepository;
import com.armin.guitarTracker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;

    public Product create(Product product, User user) {
        product.setUser(user);
        return repository.save(product);
    }

    public List<Product> getAllByUserId(User user) {
        return repository.findAllByUserId(user.getId());
    }
}

