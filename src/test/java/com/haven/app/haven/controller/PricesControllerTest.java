package com.haven.app.haven.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haven.app.haven.dto.request.SearchRequestTransaction;
import com.haven.app.haven.dto.response.PricesResponse;
import com.haven.app.haven.service.PricesService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PricesControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private PricesService pricesService;

    @BeforeEach
    void setUp() {
        PricesController pricesController = new PricesController(pricesService);
        mockMvc = MockMvcBuilders.standaloneSetup(new PricesController(pricesService)).build();
    }

    @Test
    void createPrices() throws Exception {
        PricesResponse pricesResponse = PricesResponse
                .builder()
                .id("1")
                .price(10000.0)
                .priceType("WNA")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(pricesResponse)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllPrice_Success() throws Exception {
        PricesResponse pricesResponse = PricesResponse.builder().id("1").build();
        List<PricesResponse> pricesResponseList = Arrays.asList(pricesResponse);
        Page<PricesResponse> pricesResponsePage = new PageImpl<>(pricesResponseList);
        when(pricesService.getPrices(any(SearchRequestTransaction.class))).thenReturn(pricesResponsePage);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/price")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void updatePrice_Success() throws Exception {
        PricesResponse pricesResponse = PricesResponse.builder()
                .id("1")
                .price(15000.0)
                .priceType("WNA")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/price/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(pricesResponse)))
                .andExpect(status().isOk());
    }

    @Test
    void deletePrice_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/price/1"))
                .andExpect(status().isOk());
    }
}