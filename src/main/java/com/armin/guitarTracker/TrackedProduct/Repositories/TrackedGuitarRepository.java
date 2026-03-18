package com.armin.guitarTracker.TrackedProduct.Repositories;

import com.armin.guitarTracker.TrackedProduct.Entities.TrackedGuitar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackedGuitarRepository extends JpaRepository<TrackedGuitar, Long> {
}
