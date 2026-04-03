package com.armin.guitarTracker.guitar.repository;

import com.armin.guitarTracker.guitar.entity.Guitar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GuitarRepository extends JpaRepository<Guitar, UUID> {
}
