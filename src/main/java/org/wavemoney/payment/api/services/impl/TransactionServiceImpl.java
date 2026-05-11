package org.wavemoney.payment.api.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wavemoney.payment.api.dto.DepositRequestDto;
import org.wavemoney.payment.api.dto.DepositResponseDto;
import org.wavemoney.payment.api.dto.TransferRequestDto;
import org.wavemoney.payment.api.dto.TransferResponseDto;
import org.wavemoney.payment.api.exception.common.ResourceNotFoundException;
import org.wavemoney.payment.api.exception.validation.BadRequestException;
import org.wavemoney.payment.api.model.Transaction;
import org.wavemoney.payment.api.model.Wallet;
import org.wavemoney.payment.api.model.enums.TransactionStatus;
import org.wavemoney.payment.api.model.enums.TransactionType;
import org.wavemoney.payment.api.model.enums.WalletStatus;
import org.wavemoney.payment.api.repository.TransactionRepository;
import org.wavemoney.payment.api.repository.WalletCreateRepository;
import org.wavemoney.payment.api.services.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private WalletCreateRepository walletCreateRepository;

    @Override
    @Transactional
    public DepositResponseDto deposit(DepositRequestDto request) {
        // validate amount
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(
                    "Amount must be greater than zero"
            );
        }

        // find wallet
        Wallet wallet = walletCreateRepository
                .findByWalletId(request.getWalletId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Wallet not found with id: "
                                        + request.getWalletId()
                        )
                );

        // validate wallet status
        if (WalletStatus.valueOf(wallet.getStatus()) != WalletStatus.ACTIVE) {
            throw new BadRequestException(
                    "Wallet is not active"
            );
        }

        // validate currency
        if (!wallet.getCurrency().equals(request.getCurrency())) {
            throw new BadRequestException("Currency mismatch");
        }

        // create pending transaction
        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .toWalletId(wallet.getWalletId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.PENDING)
                .timestamp(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        // update wallet balance
        wallet.setBalance(
                wallet.getBalance().add(request.getAmount())
        );

        wallet.setUpdatedAt(LocalDateTime.now());

        walletCreateRepository.save(wallet);

        // update transaction status
        transaction.setStatus(TransactionStatus.COMPLETED);

        transactionRepository.save(transaction);

        // optional kafka event
//    kafkaTemplate.send(
//            "wallet-events",
//            "Deposit completed for wallet: " + wallet.getWalletId()
//    );

        // return response
        return DepositResponseDto.builder()
                .transactionId(transaction.getTransactionId())
                .walletId(wallet.getWalletId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .balance(wallet.getBalance())
                .status(transaction.getStatus().name())
                .timestamp(transaction.getTimestamp())
                .type(transaction.getType().name())
                .build();
    }

    @Override
    @Transactional
    public TransferResponseDto transferMoney(TransferRequestDto request){
        //here even if don't check request's amount dto handle it and throw badreqerror but not message included
        if(request.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new BadRequestException("Amount must be greater than zero");
        }

        // prevent self transfer
        if (request.getFromWalletId()
                .equals(request.getToWalletId())) {

            throw new BadRequestException(
                    "Cannot transfer to same wallet"
            );
        }

        // find both wallets
        Wallet sender = walletCreateRepository
                .findByWalletId(request.getFromWalletId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Sender wallet not found"
                        )
                );

        Wallet receiver = walletCreateRepository
                .findByWalletId(request.getToWalletId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Receiver wallet not found"
                        )
                );

        // both wallets must be active
        if ( WalletStatus.valueOf(sender.getStatus()) != WalletStatus.ACTIVE) {
            throw new BadRequestException(
                    "Sender wallet is not active"
            );
        }

        if (WalletStatus.valueOf(receiver.getStatus()) != WalletStatus.ACTIVE) {
            throw new BadRequestException(
                    "Receiver wallet is not active"
            );
        }

        // same currency
        if (!sender.getCurrency().equals(receiver.getCurrency())) {
            throw new BadRequestException(
                    "Wallet currencies do not match"
            );
        }

        if (!sender.getCurrency().equals(request.getCurrency())) {
            throw new BadRequestException(
                    "Currency mismatch"
            );
        }


        // check balance
        if (sender.getBalance()
                .compareTo(request.getAmount()) < 0) {

            throw new BadRequestException(
                    "Insufficient balance"
            );
        }

        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .fromWalletId(sender.getWalletId())
                .toWalletId(receiver.getWalletId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .timestamp(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        // deduct sender balance
        sender.setBalance(
                sender.getBalance()
                        .subtract(request.getAmount())
        );

        receiver.setBalance(
                receiver.getBalance()
                        .add(request.getAmount())
        );

        walletCreateRepository.save(sender);
        walletCreateRepository.save(receiver);

        transaction.setStatus(TransactionStatus.COMPLETED);

        transactionRepository.save(transaction);

        return TransferResponseDto.builder()
                .transactionId(transaction.getTransactionId())
                .fromWalletId(sender.getWalletId())
                .toWalletId(receiver.getWalletId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .type(TransactionType.TRANSFER)
                .status(transaction.getStatus().name())
                .senderBalance(sender.getBalance())
                .receiverBalance(receiver.getBalance())
                .timestamp(transaction.getTimestamp())
                .build();

    }
}
