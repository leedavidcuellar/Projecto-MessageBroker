package com.messagebroker.javacoin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankDTO {

    private String dniAccount;

    private Long idCustomer;
}
