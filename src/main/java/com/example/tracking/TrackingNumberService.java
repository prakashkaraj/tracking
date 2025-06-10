package com.example.tracking;

import java.time.OffsetDateTime;

public interface TrackingNumberService {
    String generateTrackingNumber(
            String originCountryId,
            String destinationCountryId,
            Double weight,
            OffsetDateTime createdAt,
            String customerId,
            String customerName,
            String customerSlug
    );
}
