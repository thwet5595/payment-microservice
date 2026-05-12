package org.wavemoney.payment.api.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class DepositResponseDto implements Serializable {
    private String transactionId;

    private String walletId;

    private BigDecimal amount;

    private String currency;

    private String status;

    private String type;

    private BigDecimal balance;

    private String phoneNumber;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;
}
