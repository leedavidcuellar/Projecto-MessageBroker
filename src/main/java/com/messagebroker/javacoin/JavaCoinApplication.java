package com.messagebroker.javacoin;

import com.messagebroker.javacoin.dto.BankDTO;
import com.messagebroker.javacoin.dto.CustomerDTO;

import com.messagebroker.javacoin.dto.TransactionDTO;
import com.messagebroker.javacoin.exceptions.OperationException;
import com.messagebroker.javacoin.models.*;
import com.messagebroker.javacoin.services.Operations;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
@EntityScan(basePackages= "com.messagebroker.javacoin.models")
public class JavaCoinApplication {

    public static void main(String[] args){
        SpringApplication.run(JavaCoinApplication.class, args);
    }

    @Bean
    public CommandLineRunner intData(Operations operations){
        return (args) -> {
            Customer customer1 = operations.createCustomer(CustomerDTO.builder().nameCustomer("Julian Alvarez").mailCustomer("jalvarez@gmail.com").passwordCustomer("1234").dniCustomer("1").build());
            Customer customer2 = operations.createCustomer(CustomerDTO.builder().nameCustomer("Lionel Messi").mailCustomer("lmessi@gmail.com").passwordCustomer("1234").dniCustomer("2").build());
            Customer customer3 = operations.createCustomer(CustomerDTO.builder().nameCustomer("Cuti Romero").mailCustomer("cromero@gmail.com").passwordCustomer("1234").dniCustomer("3").build());
            Customer customer4 = operations.createCustomer(CustomerDTO.builder().nameCustomer("Enzo Fernandez").mailCustomer("efernandez@gmail.com").passwordCustomer("1234").dniCustomer("4").build());
            Customer customer5 = operations.createCustomer(CustomerDTO.builder().nameCustomer("Papu Gomez").mailCustomer("pgomez@gmail.com").passwordCustomer("1234").dniCustomer("5").build());
            Customer customer6 = operations.createCustomer(CustomerDTO.builder().nameCustomer("Lautaro Martinez").mailCustomer("lmartinez@gmail.com").passwordCustomer("1234").dniCustomer("6").build());

            Bank account1 = operations.createAccount(BankDTO.builder().dniAccount(customer1.getDniCustomer()).idCustomer(customer1.getIdCustomer()).build(),customer1);
            Bank account2 = operations.createAccount(BankDTO.builder().dniAccount(customer2.getDniCustomer()).idCustomer(customer2.getIdCustomer()).build(),customer2);
            Bank account3 = operations.createAccount(BankDTO.builder().dniAccount(customer3.getDniCustomer()).idCustomer(customer3.getIdCustomer()).build(),customer3);
            Bank account4 = operations.createAccount(BankDTO.builder().dniAccount(customer4.getDniCustomer()).idCustomer(customer4.getIdCustomer()).build(),customer4);
            Bank account5 = operations.createAccount(BankDTO.builder().dniAccount(customer5.getDniCustomer()).idCustomer(customer5.getIdCustomer()).build(),customer5);
            Bank account6 = operations.createAccount(BankDTO.builder().dniAccount(customer6.getDniCustomer()).idCustomer(customer6.getIdCustomer()).build(),customer6);

            operations.deposit(account1, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000));
            operations.deposit(account2, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000));
            operations.deposit(account3, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000));
            operations.deposit(account4,BigDecimal.valueOf(1000),BigDecimal.valueOf(1000));
            operations.deposit(account5,BigDecimal.valueOf(0),BigDecimal.valueOf(1));
            operations.deposit(account6,BigDecimal.valueOf(1),BigDecimal.valueOf(0));

            operations.addOperationToTestApi(account2);
            operations.addOperationToTestApi(account3);

            //Commission 0%
            operations.sendTransaction(customer3.getDniCustomer(), customer4.getDniCustomer());

            Thread.sleep(2000);
            //Commission 3%
            operations.sendTransaction(customer2.getDniCustomer(), customer4.getDniCustomer());

            Thread.sleep(2000);
            //Commission 5%
            operations.sendTransaction(customer1.getDniCustomer(), customer4.getDniCustomer());

            //Error Insufficient balance to operate
            operations.sendTransaction(customer5.getDniCustomer(), customer4.getDniCustomer());

        };
    }
}
