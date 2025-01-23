package com.haven.app.haven.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.haven.app.haven.dto.request.MidtransWebhookRequest;
import com.haven.app.haven.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        PaymentController controller = new PaymentController(paymentService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void webhookNotificaction() throws Exception {
        MidtransWebhookRequest request = MidtransWebhookRequest.builder()
                .transaction_time(LocalDateTime.now())
                .transaction_status("settlement")
                .transaction_id("trans123")
                .order_id("order123")
                .gross_amount("100000")
                .build();

        doNothing().when(paymentService).webhookNotification(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/payments/notification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void createPaymentLink() throws Exception {
        String transactionId = "transaction123";
        String expectedPaymentUrl = "https://midtrans.com/payment-link/123";

        when(paymentService.createPaymentLink(transactionId))
                .thenReturn(expectedPaymentUrl);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/payments/" + transactionId + "/create-payment-link"))
                .andExpect(status().isOk());
    }
}