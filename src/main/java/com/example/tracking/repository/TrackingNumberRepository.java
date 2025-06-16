package com.example.tracking.repository;

import com.example.tracking.entity.GeneratedTrackingNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackingNumberRepository extends JpaRepository<GeneratedTrackingNumber, String> {
}
