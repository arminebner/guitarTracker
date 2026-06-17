package com.armin.guitarTracker.scraper.repository;

import com.armin.guitarTracker.scraper.entity.PriceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PriceRecordRepository extends JpaRepository<PriceRecord, UUID> {
}
