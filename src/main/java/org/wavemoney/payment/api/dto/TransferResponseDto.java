package org.wavemoney.payment.api.dto;
import lombok.Builder;
import lombok.Data;
import org.wavemoney.payment.api.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransferResponseDto {

    private String transactionId;

    private String fromWalletId;

    private String toWalletId;

    private BigDecimal amount;

    private String currency;

    private String status;

    private TransactionType type;

    private BigDecimal senderBalance;

    private BigDecimal receiverBalance;

    private LocalDateTime timestamp;
}