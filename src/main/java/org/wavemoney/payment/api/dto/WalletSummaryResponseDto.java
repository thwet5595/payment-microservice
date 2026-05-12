package org.wavemoney.payment.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletSummaryResponseDto {
    private String phoneNumber;

    private BigDecimal totalDeposits;
    private BigDecimal totalWithdrawals;
    private BigDecimal currentBalance;

    private BigDecimal netFlow; // deposits - withdrawals (optional but useful)

    private long totalTransactions;
}
