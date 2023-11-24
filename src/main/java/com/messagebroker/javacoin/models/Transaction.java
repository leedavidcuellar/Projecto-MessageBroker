package com.messagebroker.javacoin.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Transaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTransaction;

    private String description;

    private String dniDestination;

    private String dniOrigin;

    private Double commission;

    private String currency;

    private BigDecimal price;

    private BigDecimal amount;

    private StatusTransaction status;

    private TypeTransaction type;

    private LocalDateTime creationDate;

    @Override
    public String toString() {
        return "Transaction{" +
                "idTransaction=" + idTransaction +
                ", description='" + description + '\'' +
                ", dniDestination='" + dniDestination + '\'' +
                ", dniOrigin='" + dniOrigin + '\'' +
                ", commission=" + commission +
                ", currency='" + currency + '\'' +
                ", price=" + price +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }
}
