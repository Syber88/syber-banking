package com.sybernetica.syber_banking.service.impl;

import com.sybernetica.syber_banking.dto.BankResponse;
import com.sybernetica.syber_banking.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
}
