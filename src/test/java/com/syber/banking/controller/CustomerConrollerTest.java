package com.syber.banking.controller;

import com.syber.banking.dto.request.UpdateCustomerRequest;
import com.syber.banking.dto.response.CustomerResponse;
import com.syber.banking.exception.CustomerEmailAlreadyExistsException;
import com.syber.banking.exception.CustomerNotFoundException;
import com.syber.banking.service.CustomerService;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    void shouldReturnBadRequestIfRequestInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "firstName: "",
                            "lastName": "Syber",
                            "nationalId": "12341234",
                            "email": "siya@gmail.com"
                        }
                        """)
        ).andExpect(status().isBadRequest());

        verify(customerService, never()).createCustomer(any());
    }

    @Test
    void shouldUpdateCustomer() throws Exception {
        CustomerResponse response = new CustomerResponse(
                1L,
                "Ronaldo",
                "Syber",
                "siya@gmail.com"
        );

        when(customerService.updateCustomer(eq(1L), any(UpdateCustomerRequest.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/v1/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "firstName": "Ronaldo"
                        }
                        """
                )
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ronaldo"));
    }

    @Test
    void shouldNotUpdateCustomerIfCustomerNotFound() throws Exception {
        when(customerService.updateCustomer(eq(999L), any(UpdateCustomerRequest.class)))
                .thenThrow(new CustomerNotFoundException("Customer not found"));
        mockMvc.perform(patch("/api/v1/customers/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "firstName": "Ronaldo"
                        }
                        """
                )
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteCustomer() throws Exception {
        doNothing().when(customerService).deleteCustomer(1L);
        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpect(status().isNoContent());
        verify(customerService).deleteCustomer(1L);
    }

    @Test
    void shouldReturn404WhenCustomerDoesNotExist() throws Exception {
        doThrow(new CustomerNotFoundException("Customer not found"))
                .when(customerService).deleteCustomer(999L);
        mockMvc.perform(delete("/api/v1/customers/999"))
                .andExpect(status().isNotFound());

        verify(customerService).deleteCustomer(999L);
    }
}
