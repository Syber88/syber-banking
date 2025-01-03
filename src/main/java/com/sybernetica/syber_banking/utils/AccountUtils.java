package com.sybernetica.syber_banking.utils;

import java.time.Year;
import java.util.Random;

public class AccountUtils {
    /**
     * the accoount number should have the branch code along with the
     * current year of creation of the account
     * then random numbers
     */


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
