package com.sybernetica.syber_banking.utils;

import java.time.Year;
import java.util.Random;

public class AccountUtils {

    public static final String ACCOUNT_ALREADY_EXIST_CODE = "001";
    public static final String ACCOUNT_ALREADY_EXIST_MESSAGE = "This user already has an account";
    public static final String ACCOUNT_CREATION_SUCCESS = "002";
    public static final String ACCOUNT_CREATION_SUCCESS_MESSAGE = "Account has been successfully created";
    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "Account does not exist";
    public static final String ACCOUNT_EXIST_CODE = "004";
    public static final String ACCOUNT_EXIST_MESSAGE = "Account has been found";
    public static final String ACCOUNT_CREDIT_SUCCESS_CODE = "005";
    public static final String ACCOUNT_CREDIT_SUCCESS_MESSAGE = "Account has been credited successfully";
    public static final String ACCOUNT_DEBIT_SUCCESS_CODE = "006";
    public static final String ACCOUNT_DEBIT_SUCCESS_MESSAGE = "Account has been debited successfully";
    public static final String ACCOUNT_DEBIT_UNSUCCESSFUL_CODE = "007";
    public static final String ACCOUNT_DEBIT_UNSUCCESSFUL_MESSAGE = "Account was not debited successfully\nInsufficient Funds";



    public static String randomNumber(){
        Random rand = new Random();
        int result = rand.nextInt(99999 - 10000 +1) + 10000;
        return String.valueOf(result);
    }

    public static String generateAccountNumber(String branchCode){
        String currentYear = String.valueOf(Year.now().getValue());
        //return branchCode + currentYear + randomNumber();
        StringBuilder accountNumber = new StringBuilder();
        return accountNumber.append(branchCode).append(currentYear).append(randomNumber()).toString();

    }
}
