package com.armin.guitarTracker.TrackedProduct.Controller;

import com.armin.guitarTracker.TrackedProduct.Entities.TrackedGuitar;
import com.armin.guitarTracker.TrackedProduct.Services.TrackedGuitarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracked-guitars")
@RequiredArgsConstructor
public class TrackedGuitarController {
    private final TrackedGuitarService trackedGuitarService;

    @PostMapping
    public TrackedGuitar create(@RequestBody TrackedGuitar trackedGuitar) {
        return trackedGuitarService.create(trackedGuitar);
    }

    @GetMapping
    public List<TrackedGuitar> getAll() {
        return trackedGuitarService.getAll();
    }
}
