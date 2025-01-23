package com.haven.app.haven.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haven.app.haven.constant.Gender;
import com.haven.app.haven.constant.Role;
import com.haven.app.haven.dto.request.SearchRequest;
import com.haven.app.haven.dto.request.UpdateUserRequest;
import com.haven.app.haven.dto.response.LoginResponse;
import com.haven.app.haven.service.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class UsersControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UsersService usersService;

    @BeforeEach
    void setUp() {
        UsersController usersController = new UsersController(usersService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(usersController)
                .build();
    }

    @Test
    @WithMockUser
    void updateUserDetail_Success() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("John Doe");

        doNothing().when(usersService).updateUserDetails(request);

        mockMvc.perform(patch("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success update user"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getStaffById_Success() throws Exception {
        LoginResponse staff = LoginResponse.builder()
                .id("1")
                .fullName("staff")
                .email("staff@gmail.com")
                .role(Role.ROLE_CUSTOMER)
                .phone("08918291281")
                .gender(Gender.MALE)
                .address("Malang")
                .birthDate(LocalDate.now())
                .nik("1234567890192837")
                .imageUrl("facebook.com")
                .build();

        when(usersService.getStaffById("1"))
                .thenReturn(staff);

        mockMvc.perform(get("/api/v1/users/staffs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success Get Staff"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.fullName").value("staff"))
                .andExpect(jsonPath("$.data.email").value("staff@gmail.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCustomerById_Success() throws Exception {
        LoginResponse staff = LoginResponse.builder()
                .id("1")
                .fullName("hiker")
                .email("hiker@gmail.com")
                .role(Role.ROLE_CUSTOMER)
                .phone("08918291281")
                .gender(Gender.MALE)
                .address("Malang")
                .birthDate(LocalDate.now())
                .nik("1234567890192837")
                .imageUrl("facebook.com")
                .build();

        when(usersService.getCustomerById("1"))
                .thenReturn(staff);

        mockMvc.perform(get("/api/v1/users/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success Get Customer"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.fullName").value("hiker"))
                .andExpect(jsonPath("$.data.email").value("hiker@gmail.com"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllStaff_Success() throws Exception {
        // Prepare mock data
        LoginResponse staff1 = LoginResponse.builder()
                .id("1")
                .fullName("Staff One")
                .build();
        LoginResponse staff2 = LoginResponse.builder()
                .id("2")
                .fullName("Staff Two")
                .build();
        List<LoginResponse> staffList = Arrays.asList(staff1, staff2);
        Page<LoginResponse> staffPage = new PageImpl<>(staffList);

        // Mock service method
        when(usersService.getAllStaff(any(SearchRequest.class)))
                .thenReturn(staffPage);

        // Perform test
        mockMvc.perform(get("/api/v1/users/staffs")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success Get Staff"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getStaff_Success() throws Exception {
        LoginResponse staff = LoginResponse.builder()
                .id("staff123")
                .fullName("John Doe")
                .build();

        when(usersService.getStaffById("staff123"))
                .thenReturn(staff);

        mockMvc.perform(get("/api/v1/users/staffs/staff123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success Get Staff"))
                .andExpect(jsonPath("$.data.id").value("staff123"));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void getAllCustomer_Success() throws Exception {
        LoginResponse customer1 = LoginResponse.builder()
                .id("1")
                .fullName("Customer One")
                .build();
        LoginResponse customer2 = LoginResponse.builder()
                .id("2")
                .fullName("Customer Two")
                .build();
        List<LoginResponse> customerList = Arrays.asList(customer1, customer2);
        Page<LoginResponse> customerPage = new PageImpl<>(customerList);

        when(usersService.getAllCustomer(any(SearchRequest.class)))
                .thenReturn(customerPage);

        mockMvc.perform(get("/api/v1/users/customers")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success Get Customer"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void resetPassword_Success() throws Exception {
        doNothing().when(usersService).resetPassword("staff123");

        mockMvc.perform(patch("/api/v1/users/staffs/staff123/reset-password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success reset password"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteStaff_Success() throws Exception {
        doNothing().when(usersService).deleteStaff("staff123");

        mockMvc.perform(delete("/api/v1/users/staffs/staff123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success delete staff"));
    }


}