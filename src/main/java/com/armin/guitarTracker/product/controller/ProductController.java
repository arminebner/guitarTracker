package com.armin.guitarTracker.product.controller;

import com.armin.guitarTracker.product.entity.Product;
import com.armin.guitarTracker.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public Product create(@RequestBody Product product) {
        return productService.create(product);
    }

    @GetMapping
    public List<Product> getAllByUserId(@RequestParam UUID userId) {
        return productService.getAllByUserId(userId);
    }
}

