package com.fashionstore.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fashionstore.controller.ApiResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApiContractTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testSuccessApiResponseContractStructure() throws Exception {
        // Construct standard success payload
        ApiResponse<String> apiResponse = ApiResponse.success("Operation complete", "Custom payload data");

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(apiResponse);

        // Verify key structural API contract properties exist in the serialized payload
        assertTrue(json.contains("\"success\":true"), "API response should indicate success");
        assertTrue(json.contains("\"message\":\"Operation complete\""), "API response should contain the message field");
        assertTrue(json.contains("\"data\":\"Custom payload data\""), "API response should contain the payload data field");
        assertFalse(json.contains("\"errors\""), "Success payload should not contain errors list");
    }

    @Test
    public void testFailureApiResponseContractStructure() throws Exception {
        // Construct standard error payload
        ApiResponse<Void> apiResponse = ApiResponse.error("Validation failed");

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(apiResponse);

        // Verify key error properties are inside the JSON contract
        assertTrue(json.contains("\"success\":false"), "Error response should indicate success is false");
        assertTrue(json.contains("\"message\":\"Validation failed\""), "Error response should contain validation message");
    }
}
