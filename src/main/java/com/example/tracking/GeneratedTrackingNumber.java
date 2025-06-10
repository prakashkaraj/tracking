package com.example.tracking;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "generated_tracking_numbers")
public class GeneratedTrackingNumber {

    @Id
    @Column(name = "tracking_number", nullable = false, unique = true, length = 16)
    private String trackingNumber;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    // JPA requires a no-arg constructor
    public GeneratedTrackingNumber() {
    }

    public GeneratedTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
        this.createdAt = OffsetDateTime.now();
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
