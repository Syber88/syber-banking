package com.syber.banking;

import java.math.BigDecimal;

public class App
{
    public static void main( String[] args )
    {
        BankAccount account = new BankAccount(BigDecimal.valueOf(1000));
        System.out.println("Balance after creating Account: R" + account.getBalance());

        account.deposit(BigDecimal.valueOf(3000));
        System.out.println("Balance after deposit: R" + account.getBalance());

        account.withdraw(BigDecimal.valueOf(1400));
        System.out.println("Balance after withdrawal: R" + account.getBalance());

        try{
            account.withdraw(BigDecimal.valueOf(10000));
        } catch (InsufficientFundsException e) {
            System.out.println("Failed withdrawal: " + e.getMessage());

        }

    }

}
