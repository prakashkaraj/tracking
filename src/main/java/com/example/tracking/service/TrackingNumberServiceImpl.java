package com.example.tracking.service;

import com.example.tracking.entity.GeneratedTrackingNumber;
import com.example.tracking.repository.TrackingNumberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TrackingNumberServiceImpl implements TrackingNumberService {

    private static final Logger logger = LoggerFactory.getLogger(TrackingNumberServiceImpl.class);

    private final TrackingNumberRepository trackingNumberRepository;

    @Autowired
    public TrackingNumberServiceImpl(TrackingNumberRepository trackingNumberRepository) {
        this.trackingNumberRepository = trackingNumberRepository;
        logger.info("TrackingNumberService initialized with UUID generation strategy.");
    }

    @Override
    public String generateTrackingNumber(String originCountryId, String destinationCountryId) {
        logger.info("Attempting to generate tracking number for origin: {}, destination: {}", originCountryId, destinationCountryId);
        logger.debug("Using UUID strategy for tracking number generation.");

        String candidateTrackingNumber;
        int maxRetries = 10;
        int attempt = 0;

        while (attempt < maxRetries) {
            String baseTrackingNumber = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(0, 12);
            candidateTrackingNumber = originCountryId + baseTrackingNumber + destinationCountryId;

            try {
                GeneratedTrackingNumber newNumber = new GeneratedTrackingNumber(candidateTrackingNumber);
                trackingNumberRepository.save(newNumber);
                logger.info("Generated unique tracking number (UUID): {}", candidateTrackingNumber);
                return candidateTrackingNumber;
            } catch (DataIntegrityViolationException e) {
                logger.warn("Collision detected for tracking number (UUID): {}. Retrying... (Attempt {}/{})", candidateTrackingNumber, attempt + 1, maxRetries);
                attempt++;
            }
        }

        logger.error("Failed to generate a unique tracking number (UUID) after {} attempts.", maxRetries);
        throw new RuntimeException("Failed to generate a unique tracking number (UUID) after " + maxRetries + " attempts.");
    }
}
