package com.example.tracking.service;

public interface TrackingNumberService {
    String generateTrackingNumber(
            String originCountryId,
            String destinationCountryId
    );
}
