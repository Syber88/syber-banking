package com.sybernetica.syber_banking.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class UserRequest {
    private String firstName;
    private String lastName;
    private String otherName;
    private String gender;
    private String address;
    private String stateOfOrigin;
    private String accountNumber;
    private BigDecimal accountBalance;
    private String email;
    private String phoneNumber;
    private String alternativePhoneNumber;

}
