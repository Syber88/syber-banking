package com.sybernetica.syber_banking.service.impl;

import com.sybernetica.syber_banking.dto.BankResponse;
import com.sybernetica.syber_banking.dto.CreditDebitRequest;
import com.sybernetica.syber_banking.dto.EnquiryRequest;
import com.sybernetica.syber_banking.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);
}
