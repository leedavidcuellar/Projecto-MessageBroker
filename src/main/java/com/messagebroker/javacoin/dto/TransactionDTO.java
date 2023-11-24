package com.messagebroker.javacoin.dto;

import com.messagebroker.javacoin.models.StatusTransaction;
import com.messagebroker.javacoin.models.TypeTransaction;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO implements Serializable {

    private String description;

    private String dniDestination;

    private String dniOrigin;

    private Double commission;

    private String currency;

    private BigDecimal price;

    private BigDecimal amount;

    private StatusTransaction status;

    private TypeTransaction type;

}
