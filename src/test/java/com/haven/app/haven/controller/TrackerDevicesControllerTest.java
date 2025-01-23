package com.haven.app.haven.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haven.app.haven.constant.TrackerStatus;
import com.haven.app.haven.dto.request.SearchTrackerDeviceRequest;
import com.haven.app.haven.dto.request.TrackerDevicesRequest;
import com.haven.app.haven.dto.request.TrackerDevicesStatusRequest;
import com.haven.app.haven.dto.response.TrackerDevicesResponse;
import com.haven.app.haven.entity.TrackerDevices;
import com.haven.app.haven.service.TrackerDevicesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class TrackerDevicesControllerTest {
    @Mock
    private TrackerDevicesService trackerDevicesService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        TrackerDevicesController controller = new TrackerDevicesController(trackerDevicesService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createTrackerDevices() throws Exception {
        TrackerDevicesRequest trackerDevicesRequest = TrackerDevicesRequest.builder()
                .serialNumber("T1")
                .build();

        TrackerDevicesResponse trackerDevicesResponse = TrackerDevicesResponse.builder()
                .id("1")
                .SerialNumber("T1")
                .status("Not_Used")
                .build();

        when(trackerDevicesService.createTracker(any(TrackerDevicesRequest.class)))
                .thenReturn(trackerDevicesResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tracker-devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trackerDevicesRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllTrackerDevices() throws Exception {
        TrackerDevicesResponse trackerDevicesResponse = TrackerDevicesResponse.builder()
                .id("1")
                .SerialNumber("T1")
                .status("Not_Used")
                .build();

        List<TrackerDevicesResponse> trackerDevicesResponseList = Collections.singletonList(trackerDevicesResponse);
        Page<TrackerDevicesResponse> trackerDevicesResponsePage = new PageImpl<>(trackerDevicesResponseList);

        when(trackerDevicesService.getTrackerDevices(any(SearchTrackerDeviceRequest.class)))
                .thenReturn(trackerDevicesResponsePage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tracker-devices")
                        .param("page", "1")
                        .param("size", "10")
                        .param("pagination", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void getTrackerDevices() throws Exception {
        TrackerDevicesResponse trackerDevicesResponse = TrackerDevicesResponse.builder()
                .id("1")
                .SerialNumber("T1")
                .status("Not_Used")
                .build();

        when(trackerDevicesService.getTrackerById("1"))
                .thenReturn(trackerDevicesResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tracker-devices/1"))
                .andExpect(status().isOk());
    }

    @Test
    void updateTrackerDevices() throws Exception {

        TrackerDevicesRequest trackerDevicesRequest = TrackerDevicesRequest.builder()
                .serialNumber("T1_UPDATED")
                .build();


        TrackerDevicesResponse trackerDevicesResponse = TrackerDevicesResponse.builder()
                .id("1")
                .SerialNumber("T1_UPDATED")
                .status("Not_Used")
                .build();


        when(trackerDevicesService.updateTracker(eq("1"), any(TrackerDevicesRequest.class)))
                .thenReturn(trackerDevicesResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/tracker-devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trackerDevicesRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void updateStatusTracker() throws Exception {
        TrackerDevicesStatusRequest trackerDevicesStatusRequest = TrackerDevicesStatusRequest.builder()
                .status("Used")
                .build();

        TrackerDevicesResponse trackerDevicesResponse = TrackerDevicesResponse.builder()
                .id("1")
                .SerialNumber("T1")
                .status("Used")
                .build();

        when(trackerDevicesService.updateStatus(eq("1"), any(TrackerDevicesStatusRequest.class)))
                .thenReturn(trackerDevicesResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/tracker-devices/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trackerDevicesStatusRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTrackerDevices() throws Exception {
        doNothing().when(trackerDevicesService).deleteTracker("1");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/tracker-devices/1"))
                .andExpect(status().isOk());
    }
}