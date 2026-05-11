package org.wavemoney.payment.api.services;

import org.springframework.stereotype.Service;
import org.wavemoney.payment.api.dto.*;

import java.util.Optional;

@Service
public interface TransactionService{
    DepositResponseDto deposit(
            DepositRequestDto request
    );

    TransferResponseDto transferMoney(TransferRequestDto request);

    TransactionResponseDto withdrawMoney(WithdrawRequestDto withdrawRequestDto);

    TransactionResponseDto transactionDetails(String transactionId);

}
