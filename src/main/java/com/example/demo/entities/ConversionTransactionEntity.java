package com.example.demo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversionTransactionEntity {
    @Id
    @GeneratedValue
    private UUID id;

    private BigDecimal sourceAmount;
    private BigDecimal targetAmount;

    private String sourceCurrency;
    private String targetCurrency;

    private LocalDateTime timeStamp;
}
