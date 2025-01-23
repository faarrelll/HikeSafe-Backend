package com.haven.app.haven.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haven.app.haven.dto.request.SearchRequestTransaction;
import com.haven.app.haven.dto.request.TicketRequest;
import com.haven.app.haven.dto.request.TransactionsRequest;
import com.haven.app.haven.dto.request.TransactionsStatusRequest;
import com.haven.app.haven.dto.response.LoginResponse;
import com.haven.app.haven.dto.response.TransactionsResponse;
import com.haven.app.haven.service.TransactionsService;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionsControllerTest {

    @Mock
    private TransactionsService transactionsService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        TransactionsController transactionsController = new TransactionsController(transactionsService);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionsController).build();
    }

    @Test
    void createTransaction() throws Exception {
        TicketRequest ticketRequest = TicketRequest.builder()
                .hikerName("Hiker")
                .address("Address")
                .identificationType("NIK")
                .phoneNumber("08889129129")
                .build();
        List<TicketRequest> ticketRequests = Arrays.asList(ticketRequest);
        TransactionsRequest transactionsRequest = TransactionsRequest.builder()
                .startDate("2024-08-01")
                .endDate("2024-08-30")
                .tickets(ticketRequests)
                .build();

        TransactionsResponse transactionsResponse = TransactionsResponse.builder()
                .id("1")
                .user(LoginResponse.builder().id("user1").build())
                .startDate("2024-08-01")
                .endDate("2024-08-30")
                .status("PENDING")
                .totalAmount(1000.0)
                .build();

        when(transactionsService.createTransaction(any(TransactionsRequest.class)))
                .thenReturn(transactionsResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionsRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllTransactions() throws Exception {
        TransactionsResponse transactionsResponse = TransactionsResponse.builder()
                .id("1")
                .totalAmount(1000.0)
                .status("PENDING")
                .build();

        List<TransactionsResponse> transactionsList = Collections.singletonList(transactionsResponse);
        Page<TransactionsResponse> transactionsPage = new PageImpl<>(transactionsList);

        when(transactionsService.getTransactions(any(SearchRequestTransaction.class)))
                .thenReturn(transactionsPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transactions")
                        .param("page", "1")
                        .param("size", "10")
                        .param("pagination", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void getTransactionById() throws Exception {
        TransactionsResponse transactionsResponse = TransactionsResponse.builder()
                .id("1")
                .totalAmount(1000.0)
                .status("PENDING")
                .build();

        when(transactionsService.getTransactionById("1"))
                .thenReturn(transactionsResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transactions/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getTransactionsByUserId() throws Exception {
        TransactionsResponse transactionsResponse = TransactionsResponse.builder()
                .id("1")
                .totalAmount(1000.0)
                .status("PENDING")
                .build();

        List<TransactionsResponse> transactionsList = Collections.singletonList(transactionsResponse);
        Page<TransactionsResponse> transactionsPage = new PageImpl<>(transactionsList);

        when(transactionsService.getTransactionByUser(anyInt(), anyInt()))
                .thenReturn(transactionsPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transactions/user")
                        .param("page", "1")
                        .param("size", "10")
                        .param("pagination", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void updateTransactionStatus() throws Exception {
        TransactionsStatusRequest statusRequest = TransactionsStatusRequest.builder()
                .status("COMPLETED")
                .build();

        TransactionsResponse transactionsResponse = TransactionsResponse.builder()
                .id("1")
                .totalAmount(1000.0)
                .status("COMPLETED")
                .build();

        when(transactionsService.updateTransactionStatus(eq("1"), any(TransactionsStatusRequest.class)))
                .thenReturn(transactionsResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/transactions/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void deviceAssignment() throws Exception {
        doNothing().when(transactionsService).deviceAssignment("1", "device1");

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/transactions/1/device/device1"))
                .andExpect(status().isOk());
    }
}