package org.wavemoney.payment.api.services;

import org.wavemoney.payment.api.dto.WalletRequestDto;
import org.wavemoney.payment.api.dto.WalletResponseDto;
import org.wavemoney.payment.api.dto.WalletSummaryResponseDto;

public interface WalletCreateServices {

    WalletResponseDto createWallet(WalletRequestDto walletRequestDto);

    WalletResponseDto getWalletByWalletId(String walletId);

    WalletSummaryResponseDto getWalletSummary(String walletId);
}
