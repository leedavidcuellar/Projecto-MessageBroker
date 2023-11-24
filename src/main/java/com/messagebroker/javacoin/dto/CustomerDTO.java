package com.messagebroker.javacoin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTO {

    private String nameCustomer;

    private String dniCustomer;

    private String mailCustomer;

    private String passwordCustomer;
}
