package com.armin.guitarTracker.guitar.controller;

import com.armin.guitarTracker.guitar.entity.Guitar;
import com.armin.guitarTracker.guitar.service.GuitarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracked-guitars")
@RequiredArgsConstructor
public class GuitarController {
    private final GuitarService guitarService;

    @PostMapping
    public Guitar create(@RequestBody Guitar guitar) {
        return guitarService.create(guitar);
    }

    @GetMapping
    public List<Guitar> getAll() {
        return guitarService.getAll();
    }
}
