package org.wavemoney.payment.api.dto;

import lombok.Data;
import org.wavemoney.payment.api.model.enums.TransactionStatus;
import org.wavemoney.payment.api.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponseDto {

    private String transactionId;

    private String fromWalletId;

    private String toWalletId;

    private String currency;

    private BigDecimal amount;

    private TransactionType type;

    private TransactionStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    private BigDecimal withdrawAmount; // only used for withdraw
}