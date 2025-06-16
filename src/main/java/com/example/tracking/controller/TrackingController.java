package com.example.tracking.controller;

import com.example.tracking.dto.TrackingNumberResponse;
import com.example.tracking.service.TrackingNumberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@RestController
@Validated
public class TrackingController {

    private static final Logger logger = LoggerFactory.getLogger(TrackingController.class);
    private final TrackingNumberService trackingNumberService;

    @Autowired
    public TrackingController(TrackingNumberService trackingNumberService) {
        this.trackingNumberService = trackingNumberService;
    }

    @GetMapping("/next-tracking-number")
    public ResponseEntity<TrackingNumberResponse> getNextTrackingNumber(
            @RequestParam @NotBlank(message = "Origin country ID must be provided and not blank.") @Size(min = 2, max = 2, message = "Origin country ID must be exactly 2 characters long.") @Pattern(regexp = "^[a-zA-Z0-9]{2}$", message = "Origin country ID must be 2 alphanumeric characters") String origin_country_id,
            @RequestParam @NotBlank(message = "Destination country ID must be provided and not blank.") @Size(min = 2, max = 2, message = "Destination country ID must be exactly 2 characters long.") @Pattern(regexp = "^[a-zA-Z0-9]{2}$", message = "Destination country ID must be 2 alphanumeric characters") String destination_country_id,
            @RequestParam @NotNull(message = "Weight must be provided.") @DecimalMin(value = "0.001", message = "Weight must be a minimum of 0.001.") @DecimalMax(value = "999.999", message = "Weight must be a maximum of 999.999.") Double weight,
            @RequestParam @NotNull(message = "Creation timestamp must be provided.") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime created_at,
            @RequestParam @NotBlank(message = "Customer ID must be provided and not blank.") @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Customer ID must be a valid UUID format (e.g., xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx).") String customer_id,
            @RequestParam @NotBlank(message = "Customer name must be provided and not blank.") String customer_name,
            @RequestParam @NotBlank(message = "Customer slug must be provided and not blank.") @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Customer slug must consist of lowercase alphanumeric characters and hyphens, and cannot start or end with a hyphen (e.g., 'valid-slug').") String customer_slug) {

        logger.info("Received request for new tracking number with origin_country_id: {}, destination_country_id: {}, weight: {}, customer_id: {}, customer_name: {}, customer_slug: {}",
                origin_country_id, destination_country_id, weight, customer_id, customer_name, customer_slug);

        String trackingNumber = trackingNumberService.generateTrackingNumber(
                origin_country_id,
                destination_country_id);
        logger.info("Generated tracking number: {}", trackingNumber);

        TrackingNumberResponse response = new TrackingNumberResponse(trackingNumber, OffsetDateTime.now());
        return ResponseEntity.ok(response);
    }
}
