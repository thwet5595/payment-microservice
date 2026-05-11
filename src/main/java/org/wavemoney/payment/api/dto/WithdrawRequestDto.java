package org.wavemoney.payment.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawRequestDto {

    //private String phoneNumber;

    private String fromWalletId;

    private BigDecimal amount;

    private String currency;

}