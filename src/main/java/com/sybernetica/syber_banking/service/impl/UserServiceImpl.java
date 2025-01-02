package com.sybernetica.syber_banking.service.impl;

import com.sybernetica.syber_banking.dto.BankResponse;
import com.sybernetica.syber_banking.dto.UserRequest;
import com.sybernetica.syber_banking.entity.User;

public class UserServiceImpl implements UserService{

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        /**
         * save new user to database
         */
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName((userRequest.getLastName()))
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address((userRequest.getAddress()))
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber()
                .build();
    }
}
