package org.wavemoney.payment.api.services;

import org.springframework.stereotype.Service;
import org.wavemoney.payment.api.dto.TransactionResponseDto;
import org.wavemoney.payment.api.dto.WithdrawRequestDto;


@Service
public interface TransactionService {

    TransactionResponseDto withdrawMoney(WithdrawRequestDto withdrawRequestDto);

}
