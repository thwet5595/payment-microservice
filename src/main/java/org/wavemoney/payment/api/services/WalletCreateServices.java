package org.wavemoney.payment.api.services;

import org.wavemoney.payment.api.dto.WalletRequestDto;
import org.wavemoney.payment.api.dto.WalletResponseDto;

public interface WalletCreateServices {

    WalletResponseDto createWallet(WalletRequestDto walletRequestDto);

    WalletResponseDto getWalletByWalletId(String walletId);

}
