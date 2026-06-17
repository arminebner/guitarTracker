package com.armin.guitarTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GuitarTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuitarTrackerApplication.class, args);
    }

}
