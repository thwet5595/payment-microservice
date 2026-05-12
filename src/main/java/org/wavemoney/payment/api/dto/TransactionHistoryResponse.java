package org.wavemoney.payment.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wavemoney.payment.api.model.enums.TransactionStatus;
import org.wavemoney.payment.api.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionHistoryResponse {
        private String transactionId;

        private TransactionType type; // DEPOSIT / WITHDRAWAL / TRANSFER
        private TransactionStatus status; // PENDING / COMPLETED / FAILED

        private BigDecimal amount;
        private String currency;

        private String fromWalletId;
        private String toWalletId; // ⭐ ADD THIS (important for transfer role detection)

        private String fromPhoneNumber;
        private String toPhoneNumber;

        private String role; // SENT or RECEIVED (derived field)

        private LocalDateTime createdAt;
        private LocalDateTime completedAt;

}