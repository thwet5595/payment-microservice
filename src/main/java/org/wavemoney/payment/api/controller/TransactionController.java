package org.wavemoney.payment.api.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wavemoney.payment.api.dto.*;
import org.wavemoney.payment.api.dto.DepositRequestDto;
import org.wavemoney.payment.api.dto.DepositResponseDto;
import org.wavemoney.payment.api.dto.TransferRequestDto;
import org.wavemoney.payment.api.dto.TransferResponseDto;
import org.wavemoney.payment.api.dto.TransactionResponseDto;
import org.wavemoney.payment.api.dto.WithdrawRequestDto;
import org.wavemoney.payment.api.services.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<DepositResponseDto> deposit(@Valid @RequestBody DepositRequestDto depositRequestDto) {
        return ResponseEntity.ok(transactionService.deposit(depositRequestDto));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponseDto> transfer(@Valid @RequestBody TransferRequestDto transferRequestDto) {
        return ResponseEntity.ok(transactionService.transferMoney(transferRequestDto));
    }


    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDto> withdrawMoney(@RequestBody WithdrawRequestDto withdrawRequestDto)
    {
        return ResponseEntity.ok(transactionService.withdrawMoney(withdrawRequestDto));
    }

    @GetMapping("/{walletId}/history")
    public ResponseEntity<List<TransactionHistoryResponse>> getTransactions(
            @PathVariable String walletId
    ) {
        return ResponseEntity.ok(
                transactionService.getTransactions(walletId)
        );
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDto> transactionDetails(@PathVariable String transactionId)
    {
        return ResponseEntity.ok(transactionService.transactionDetails(transactionId));
    }



}
