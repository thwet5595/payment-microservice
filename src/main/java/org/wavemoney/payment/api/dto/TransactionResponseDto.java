package org.wavemoney.payment.api.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class TransactionResponseDto {

    private String transactionId;

    private String fromWalletId;

    private String currency;

    private BigDecimal amount;

    private TransactionType type;

    private TransactionStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    private BigDecimal withdrawAmount;

    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER
    }

    public enum TransactionStatus {
        PENDING,
        COMPLETED,
        FAILED
    }

}
