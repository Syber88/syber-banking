package com.sybernetica.syber_banking;


import com.sybernetica.syber_banking.dto.BankResponse;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;


public class BankResponseTests {

    @Test
    public void testBankResponseBuilder(){
        BankResponse response = BankResponse.builder()
                .responseCode("200")
                .responseMessage("everything is rosy")
                .build();
        assertNotNull(response, "response should not be null");
        assertEquals("200",response.getResponseCode());
        assertEquals("everything is rosy", response.getResponseMessage());
    }

}
