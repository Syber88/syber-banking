package com.sybernetica.syber_banking.utils;

import java.time.Year;
import java.util.Random;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account";
    public static final String ACCOUNT_CREATION_SUCCESS = "002";
    public static final String ACCOUNT_CREATION_SUCCESSS_MESSAGE = "Account has been successfully created";



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
