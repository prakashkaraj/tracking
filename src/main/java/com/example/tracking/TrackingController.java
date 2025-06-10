package com.example.tracking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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
public class TrackingController {

    private final TrackingNumberService trackingNumberService;

    @Autowired
    public TrackingController(TrackingNumberService trackingNumberService) {
        this.trackingNumberService = trackingNumberService;
    }

    @GetMapping("/next-tracking-number")
    public ResponseEntity<TrackingNumberResponse> getNextTrackingNumber(
            @RequestParam @NotBlank @Size(min = 2, max = 2) @Pattern(regexp = "[A-Z]{2}") String origin_country_id,
            @RequestParam @NotBlank @Size(min = 2, max = 2) @Pattern(regexp = "[A-Z]{2}") String destination_country_id,
            @RequestParam @NotNull @DecimalMin(value = "0.001") @DecimalMax(value = "999.999") Double weight, // Assuming max weight and precision
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime created_at,
            @RequestParam @NotBlank @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$") String customer_id,
            @RequestParam @NotBlank String customer_name,
            @RequestParam @NotBlank @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$") String customer_slug) {

        String trackingNumber = trackingNumberService.generateTrackingNumber(
                origin_country_id,
                destination_country_id,
                weight,
                created_at,
                customer_id,
                customer_name,
                customer_slug);

        TrackingNumberResponse response = new TrackingNumberResponse(trackingNumber, OffsetDateTime.now());
        return ResponseEntity.ok(response);
    }
}
