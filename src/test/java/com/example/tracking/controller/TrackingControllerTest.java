package com.example.tracking.controller;

import com.example.tracking.service.TrackingNumberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrackingController.class)
class TrackingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrackingNumberService trackingNumberService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String validOriginCountryId = "US";
    private final String validDestinationCountryId = "CA";
    private final String validWeight = "10.5";
    private final String validCreatedAt = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    private final String validCustomerId = UUID.randomUUID().toString();
    private final String validCustomerName = "Test Customer";
    private final String validCustomerSlug = "test-customer";

    @Test
    void getNextTrackingNumber_success_validParameters() throws Exception {
        String expectedTrackingNumber = "USMOCKTRACK123CA";
        when(trackingNumberService.generateTrackingNumber(validOriginCountryId, validDestinationCountryId))
                .thenReturn(expectedTrackingNumber);

        mockMvc.perform(get("/next-tracking-number")
                        .param("origin_country_id", validOriginCountryId)
                        .param("destination_country_id", validDestinationCountryId)
                        .param("weight", validWeight)
                        .param("created_at", validCreatedAt)
                        .param("customer_id", validCustomerId)
                        .param("customer_name", validCustomerName)
                        .param("customer_slug", validCustomerSlug))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.trackingNumber").value(expectedTrackingNumber))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void getNextTrackingNumber_fail_missingOriginCountryId() throws Exception {
        mockMvc.perform(get("/next-tracking-number")
                        .param("destination_country_id", validDestinationCountryId)
                        .param("weight", validWeight)
                        .param("created_at", validCreatedAt)
                        .param("customer_id", validCustomerId)
                        .param("customer_name", validCustomerName)
                        .param("customer_slug", validCustomerSlug))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNextTrackingNumber_fail_invalidOriginCountryId_length() throws Exception {
        mockMvc.perform(get("/next-tracking-number")
                        .param("origin_country_id", "USA")
                        .param("destination_country_id", validDestinationCountryId)
                        .param("weight", validWeight)
                        .param("created_at", validCreatedAt)
                        .param("customer_id", validCustomerId)
                        .param("customer_name", validCustomerName)
                        .param("customer_slug", validCustomerSlug))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNextTrackingNumber_fail_invalidOriginCountryId_pattern() throws Exception {
        mockMvc.perform(get("/next-tracking-number")
                        .param("origin_country_id", "U$")
                        .param("destination_country_id", validDestinationCountryId)
                        .param("weight", validWeight)
                        .param("created_at", validCreatedAt)
                        .param("customer_id", validCustomerId)
                        .param("customer_name", validCustomerName)
                        .param("customer_slug", validCustomerSlug))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNextTrackingNumber_fail_missingDestinationCountryId() throws Exception {
        mockMvc.perform(get("/next-tracking-number")
                        .param("origin_country_id", validOriginCountryId)
                        .param("weight", validWeight)
                        .param("created_at", validCreatedAt)
                        .param("customer_id", validCustomerId)
                        .param("customer_name", validCustomerName)
                        .param("customer_slug", validCustomerSlug))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNextTrackingNumber_fail_missingWeight() throws Exception {
        mockMvc.perform(get("/next-tracking-number")
                        .param("origin_country_id", validOriginCountryId)
                        .param("destination_country_id", validDestinationCountryId)
                        .param("created_at", validCreatedAt)
                        .param("customer_id", validCustomerId)
                        .param("customer_name", validCustomerName)
                        .param("customer_slug", validCustomerSlug))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNextTrackingNumber_fail_invalidWeight_tooLow() throws Exception {
        mockMvc.perform(get("/next-tracking-number")
                        .param("origin_country_id", validOriginCountryId)
                        .param("destination_country_id", validDestinationCountryId)
                        .param("weight", "0.0001")
                        .param("created_at", validCreatedAt)
                        .param("customer_id", validCustomerId)
                        .param("customer_name", validCustomerName)
                        .param("customer_slug", validCustomerSlug))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNextTrackingNumber_fail_missingCreatedAt() throws Exception {
        mockMvc.perform(get("/next-tracking-number")
                        .param("origin_country_id", validOriginCountryId)
                        .param("destination_country_id", validDestinationCountryId)
                        .param("weight", validWeight)
                        .param("customer_id", validCustomerId)
                        .param("customer_name", validCustomerName)
                        .param("customer_slug", validCustomerSlug))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNextTrackingNumber_fail_invalidCreatedAt_format() throws Exception {
        mockMvc.perform(get("/next-tracking-number")
                        .param("origin_country_id", validOriginCountryId)
                        .param("destination_country_id", validDestinationCountryId)
                        .param("weight", validWeight)
                        .param("created_at", "2023-01-01T10:00:00")
                        .param("customer_id", validCustomerId)
                        .param("customer_name", validCustomerName)
                        .param("customer_slug", validCustomerSlug))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNextTrackingNumber_fail_missingCustomerId() throws Exception {
        mockMvc.perform(get("/next-tracking-number")
                        .param("origin_country_id", validOriginCountryId)
                        .param("destination_country_id", validDestinationCountryId)
                        .param("weight", validWeight)
                        .param("created_at", validCreatedAt)
                        .param("customer_name", validCustomerName)
                        .param("customer_slug", validCustomerSlug))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNextTrackingNumber_fail_invalidCustomerId_format() throws Exception {
        mockMvc.perform(get("/next-tracking-number")
                        .param("origin_country_id", validOriginCountryId)
                        .param("destination_country_id", validDestinationCountryId)
                        .param("weight", validWeight)
                        .param("created_at", validCreatedAt)
                        .param("customer_id", "invalid-uuid-format")
                        .param("customer_name", validCustomerName)
                        .param("customer_slug", validCustomerSlug))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNextTrackingNumber_fail_missingCustomerName() throws Exception {
        mockMvc.perform(get("/next-tracking-number")
                        .param("origin_country_id", validOriginCountryId)
                        .param("destination_country_id", validDestinationCountryId)
                        .param("weight", validWeight)
                        .param("created_at", validCreatedAt)
                        .param("customer_id", validCustomerId)
                        .param("customer_slug", validCustomerSlug))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNextTrackingNumber_fail_missingCustomerSlug() throws Exception {
        mockMvc.perform(get("/next-tracking-number")
                        .param("origin_country_id", validOriginCountryId)
                        .param("destination_country_id", validDestinationCountryId)
                        .param("weight", validWeight)
                        .param("created_at", validCreatedAt)
                        .param("customer_id", validCustomerId)
                        .param("customer_name", validCustomerName))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNextTrackingNumber_fail_invalidCustomerSlug_format() throws Exception {
        mockMvc.perform(get("/next-tracking-number")
                        .param("origin_country_id", validOriginCountryId)
                        .param("destination_country_id", validDestinationCountryId)
                        .param("weight", validWeight)
                        .param("created_at", validCreatedAt)
                        .param("customer_id", validCustomerId)
                        .param("customer_name", validCustomerName)
                        .param("customer_slug", "Invalid Slug With Spaces"))
                .andExpect(status().isBadRequest());
    }
}
