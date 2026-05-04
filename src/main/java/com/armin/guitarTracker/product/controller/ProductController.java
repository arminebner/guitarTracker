package com.armin.guitarTracker.product.controller;

import com.armin.guitarTracker.product.entity.Product;
import com.armin.guitarTracker.product.service.ProductService;
import com.armin.guitarTracker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public Product create(@RequestBody Product product, @AuthenticationPrincipal User user) {
        return productService.create(product, user);
    }

    @GetMapping
    public List<Product> getAllByUserId(@AuthenticationPrincipal User user) {
        return productService.getAllByUserId(user);
    }
}

