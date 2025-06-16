package com.example.tracking.service;

import com.example.tracking.entity.GeneratedTrackingNumber;
import com.example.tracking.repository.TrackingNumberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackingNumberServiceImplTest {

    @Mock
    private TrackingNumberRepository trackingNumberRepository;

    @InjectMocks
    private TrackingNumberServiceImpl trackingNumberService;

    @BeforeEach
    void setUp() {
        lenient().when(trackingNumberRepository.save(any(GeneratedTrackingNumber.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void generateTrackingNumber_success_validInputs() {
        String originCountryId = "US";
        String destinationCountryId = "CA";

        String trackingNumber = trackingNumberService.generateTrackingNumber(originCountryId, destinationCountryId);

        assertNotNull(trackingNumber);
        assertEquals(16, trackingNumber.length());
        assertTrue(trackingNumber.startsWith(originCountryId));
        assertTrue(trackingNumber.endsWith(destinationCountryId));
        assertTrue(trackingNumber.matches("^[A-Z0-9]{16}$"));

        String middlePart = trackingNumber.substring(2, 14);
        assertTrue(middlePart.matches("^[A-Z0-9]{12}$"));

        ArgumentCaptor<GeneratedTrackingNumber> captor = ArgumentCaptor.forClass(GeneratedTrackingNumber.class);
        verify(trackingNumberRepository, times(1)).save(captor.capture());
        assertEquals(trackingNumber, captor.getValue().getTrackingNumber());
    }

    @Test
    void generateTrackingNumber_success_validNumericInputs() {
        String originCountryId = "01";
        String destinationCountryId = "23";

        String trackingNumber = trackingNumberService.generateTrackingNumber(originCountryId, destinationCountryId);

        assertNotNull(trackingNumber);
        assertTrue(trackingNumber.startsWith("01"));
        assertTrue(trackingNumber.endsWith("23"));
        assertTrue(trackingNumber.matches("^[A-Z0-9]{16}$"));
        verify(trackingNumberRepository, times(1)).save(any(GeneratedTrackingNumber.class));
    }

    @Test
    void generateTrackingNumber_concurrentGenerations_UUIDStrategy() throws InterruptedException {
        int numberOfThreads = 50;
        int operationsPerThread = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads * operationsPerThread);
        Set<String> generatedNumbers = new HashSet<>();

        for (int i = 0; i < numberOfThreads * operationsPerThread; i++) {
            executorService.submit(() -> {
                try {
                    String trackingNumber = trackingNumberService.generateTrackingNumber("MT", "TH");
                    assertNotNull(trackingNumber);
                    assertTrue(trackingNumber.matches("^[A-Z0-9]{16}$"));
                    synchronized (generatedNumbers) {
                        generatedNumbers.add(trackingNumber);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS), "Latch did not count down in time");
        executorService.shutdown();
        assertTrue(executorService.awaitTermination(5, TimeUnit.SECONDS), "Executor service did not terminate in time");

        assertEquals(numberOfThreads * operationsPerThread, generatedNumbers.size(), "Generated tracking numbers should be unique under concurrency.");
        verify(trackingNumberRepository, times(numberOfThreads * operationsPerThread)).save(any(GeneratedTrackingNumber.class));
    }
}
