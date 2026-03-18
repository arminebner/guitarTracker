package com.armin.guitarTracker.TrackedProduct.Services;

import com.armin.guitarTracker.TrackedProduct.Entities.TrackedGuitar;
import com.armin.guitarTracker.TrackedProduct.Repositories.TrackedGuitarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackedGuitarService {
    private final TrackedGuitarRepository repository;

    public TrackedGuitar create(TrackedGuitar guitar) {
        return repository.save(guitar);
    }

    public List<TrackedGuitar> getAll() {
        return repository.findAll();
    }
}
