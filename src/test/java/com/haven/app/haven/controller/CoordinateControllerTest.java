package com.haven.app.haven.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haven.app.haven.dto.request.CoordinateRequest;
import com.haven.app.haven.dto.response.CoordinateResponse;
import com.haven.app.haven.service.CoordinateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CoordinateControllerTest {
    @Mock
    private CoordinateService service;
    MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        CoordinateController controller = new CoordinateController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void addCoordinate() throws Exception {
        // Prepare request
        CoordinateRequest coordinateRequest = CoordinateRequest.builder()
                .latitude("-6.2088")
                .longitude("106.8456")
                .build();

        // Prepare response
        CoordinateResponse coordinateResponse = CoordinateResponse.builder()
                .id("1")
                .latitude("-6.2088")
                .longitude("106.8456")
                .build();

        // Mock service method
        when(service.addCoordinate(any(CoordinateRequest.class)))
                .thenReturn(coordinateResponse);

        // Perform POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/coordinate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(coordinateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void getCoordinate() throws Exception {
        // Prepare coordinate responses
        CoordinateResponse coordinateResponse = CoordinateResponse.builder()
                .id("1")
                .latitude("-6.2088")
                .longitude("106.8456")
                .build();

        List<CoordinateResponse> coordinatesList = Collections.singletonList(coordinateResponse);
        Page<CoordinateResponse> coordinatesPage = new PageImpl<>(coordinatesList);

        // Mock service method
        when(service.getCoordinate(eq("transaction1"), eq(1), eq(10)))
                .thenReturn(coordinatesPage);

        // Perform GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/coordinate/transaction1")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

}