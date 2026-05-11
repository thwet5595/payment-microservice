package org.wavemoney.payment.api.services;

import org.springframework.stereotype.Service;
import org.wavemoney.payment.api.dto.DepositRequestDto;
import org.wavemoney.payment.api.dto.DepositResponseDto;
import org.wavemoney.payment.api.dto.TransferRequestDto;
import org.wavemoney.payment.api.dto.TransferResponseDto;

import java.util.Optional;

@Service
public interface TransactionService{
    DepositResponseDto deposit(
            DepositRequestDto request
    );

    TransferResponseDto transferMoney(TransferRequestDto request);


}
