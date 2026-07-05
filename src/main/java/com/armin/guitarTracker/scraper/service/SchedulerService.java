package com.armin.guitarTracker.scraper.service;

import com.armin.guitarTracker.scraper.entity.PriceRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final ScraperService scraperService;

    @Scheduled(fixedRate = 60000 * 10) // Schedule to run every 10 minutes (adjust as needed)
    public void scheduleCrape() {
        List<PriceRecord> priceRecords = scraperService.scrapeAllProducts();
        // add additional logic here to process the scraped price records if needed
        System.out.println("Scraped " + priceRecords.size() + " price records.");
    }
}
