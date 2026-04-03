package com.armin.guitarTracker.guitar.service;

import com.armin.guitarTracker.guitar.entity.Guitar;
import com.armin.guitarTracker.guitar.repository.GuitarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuitarService {
    private final GuitarRepository repository;

    public Guitar create(Guitar guitar) {
        return repository.save(guitar);
    }

    public List<Guitar> getAll() {
        return repository.findAll();
    }
}
