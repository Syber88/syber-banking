package com.syber.banking.controller;

import com.syber.banking.dto.response.CustomerResponse;
import com.syber.banking.service.CustomerService;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
public class CustomerConrollerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Test
    void shouldCreateACustomer() throws Exception {
        CustomerResponse response = new CustomerResponse(
                1L,
                "Siyabonga",
                "Syber",
                "siya@gmail.com"
        );

        when(customerService.createCustomer(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "firstName": "Siyabonga",
                          "lastName": "Syber",
                          "nationalId": "12341234",
                          "email": "siya@gmail.com"
                        }
                        """)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Siyabonga"))
                .andExpect(jsonPath("$.email").value("siya@gmail.com"));

        verify(customerService).createCustomer(any());
    }




}
