package com.syber.banking.controller;

import com.syber.banking.dto.request.UpdateCustomerRequest;
import com.syber.banking.dto.response.CustomerResponse;
import com.syber.banking.exception.CustomerNotFoundException;
import com.syber.banking.service.CustomerService;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

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

    @Test
    void shouldGetCustomerById() throws Exception {
        CustomerResponse response = new CustomerResponse(
                1L,
                "Nigel",
                "Baxter",
                "Nigel@gmail.com"
        );
        when(customerService.getCustomer(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Nigel"))
                .andExpect((jsonPath("$.email").value("Nigel@gmail.com")));
    }

    @Test
    void shouldThrow404WhenCustomerIsNotFound() throws Exception {
        when(customerService.getCustomer(999L))
                .thenThrow(new CustomerNotFoundException("Customer not found"));

        mockMvc.perform(get("/api/v1/customers/999"))
                .andExpect(status().isNotFound());

        verify(customerService).getCustomer(999L);
    }

    @Test
    void shouldReturnAllCustomers() throws Exception {

        CustomerResponse customer1 = new CustomerResponse(
                1L,
                "Nigel",
                "Baxter",
                "Nigel@gmail.com"
        );

        CustomerResponse customer2 = new CustomerResponse(
                2L,
                "John",
                "Doe",
                "john@gmail.com"
        );

        when(customerService.getCustomers())
                .thenReturn(List.of(customer1, customer2));

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Nigel"))
                .andExpect(jsonPath("$[0].lastName").value("Baxter"))
                .andExpect(jsonPath("$[0].email").value("Nigel@gmail.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("John"))
                .andExpect(jsonPath("$[1].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].email").value("john@gmail.com"));

        verify(customerService).getCustomers();
    }

    @Test
    void shouldReturnEmptyCustomerList() throws Exception {

        when(customerService.getCustomers())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(customerService).getCustomers();
    }
}
