package com.example.tracking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentHashMap; // Added for in memory solution
import java.util.concurrent.ConcurrentMap;   // Added for in memory solution
import java.util.UUID;

@Service
public class TrackingNumberServiceImpl implements TrackingNumberService {

    private static final Logger logger = LoggerFactory.getLogger(TrackingNumberServiceImpl.class);

    private final TrackingNumberRepository trackingNumberRepository;
    private final ConcurrentMap<String, Boolean> generatedNumbersInMemory;

    @Value("${tracking.number.generation.strategy:database}") // Default to database if property not found
    private String generationStrategy;

    @Autowired
    public TrackingNumberServiceImpl(TrackingNumberRepository trackingNumberRepository) {
        this.trackingNumberRepository = trackingNumberRepository;
        this.generatedNumbersInMemory = new ConcurrentHashMap<>(); // Initialize for in-memory strategy
    }

    @Override
    public String generateTrackingNumber(
            String originCountryId,
            String destinationCountryId,
            Double weight,
            OffsetDateTime createdAt,
            String customerId,
            String customerName,
            String customerSlug) {

        if ("in-memory".equalsIgnoreCase(generationStrategy)) {
            logger.debug("Using in-memory strategy for tracking number generation.");
            String trackingNumber;
            // Loop to ensure uniqueness for the in-memory map
            do {
                trackingNumber = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(0, 16);
            } while (generatedNumbersInMemory.putIfAbsent(trackingNumber, Boolean.TRUE) != null);
            logger.info("Generated unique tracking number (in-memory): {}", trackingNumber);
            return trackingNumber;
        } else { // Default to database strategy
            logger.debug("Using database strategy for tracking number generation.");
            String candidateTrackingNumber;
            int maxRetries = 10; 
            int attempt = 0;

            while (attempt < maxRetries) {
                candidateTrackingNumber = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(0, 16);
                try {
                    GeneratedTrackingNumber newNumber = new GeneratedTrackingNumber(candidateTrackingNumber);
                    trackingNumberRepository.save(newNumber);
                    logger.info("Generated unique tracking number (database): {}", candidateTrackingNumber);
                    return candidateTrackingNumber;
                } catch (DataIntegrityViolationException e) {
                    logger.warn("Collision detected for tracking number (database): {}. Retrying... (Attempt {}/{})", candidateTrackingNumber, attempt + 1, maxRetries);
                    attempt++;
                }
            }
            logger.error("Failed to generate a unique tracking number (database) after {} attempts.", maxRetries);
            throw new RuntimeException("Failed to generate a unique tracking number (database) after " + maxRetries + " attempts.");
        }
    }
}
