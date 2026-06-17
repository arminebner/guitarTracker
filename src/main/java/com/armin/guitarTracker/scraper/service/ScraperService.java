package com.armin.guitarTracker.scraper.service;

import com.armin.guitarTracker.product.entity.Product;
import com.armin.guitarTracker.product.repository.ProductRepository;
import com.armin.guitarTracker.scraper.entity.PriceRecord;
import com.armin.guitarTracker.scraper.repository.PriceRecordRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScraperService {

    private final PriceRecordRepository priceRecordRepository;
    private final ProductRepository productRepository;

    private PriceRecord scrapeProduct(Product product) {
        try {
            final String html = getHtml(product.getProductUrl());
            final Document document = Jsoup.parse(html, product.getProductUrl());
            final Element priceElement = document.selectFirst(product.getPriceSelector());

            if (priceElement != null) {
                String priceText = priceElement.text().replaceAll("[^\\d.,]", ""); // Remove non-numeric characters
                priceText = priceText.replace(",", "."); // Replace comma with dot for decimal
                PriceRecord priceRecord = PriceRecord.builder()
                        .price(new BigDecimal(priceText))
                        .build();

                return priceRecordRepository.save(priceRecord);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to scrape product: " + e.getMessage());
        }
        return null;
    }

    public List<PriceRecord> scrapeAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::scrapeProduct)
                .toList();
    }

    private String getHtml(@NotBlank String productUrl) {
        // TODO move into env
        String userAgent = "Mozilla/5.0 (X11; Linux x86_64; rv:150.0) Gecko/20100101 Firefox/150.0";

        final Request request = new Request.Builder()
                .url(productUrl)
                .header("User-Agent", userAgent)
                .build();
        try (final Response response = new OkHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to fetch product page: " + response.code());
            }
            assert response.body() != null;
            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch product page: " + e.getMessage());
        }
    }
}
