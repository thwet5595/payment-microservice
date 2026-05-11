package org.wavemoney.payment.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wavemoney.payment.api.dto.TransactionResponseDto;
import org.wavemoney.payment.api.dto.WithdrawRequestDto;
import org.wavemoney.payment.api.services.TransactionService;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    @Autowired
    public TransactionService transactionService;

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDto> withdrawMoney(@RequestBody WithdrawRequestDto withdrawRequestDto)
    {
        return ResponseEntity.ok(transactionService.withdrawMoney(withdrawRequestDto));
    }

}
