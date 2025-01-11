package com.sybernetica.syber_banking.service.impl;

import com.sybernetica.syber_banking.dto.AccountInfo;
import com.sybernetica.syber_banking.dto.BankResponse;
import com.sybernetica.syber_banking.dto.UserRequest;
import com.sybernetica.syber_banking.entity.User;
import com.sybernetica.syber_banking.repository.UserRepository;
import com.sybernetica.syber_banking.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        /**
         * save new user to database
         */
        if (userRepository.existsByEmail(userRequest.getEmail())){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName((userRequest.getLastName()))
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address((userRequest.getAddress()))
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber("442"))
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_SUCCESSS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                        .accountNumber()
                        .build())
                .build();
    }}



