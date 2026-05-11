package org.wavemoney.payment.api.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wavemoney.payment.api.dto.*;
import org.wavemoney.payment.api.exception.common.ResourceNotFoundException;
import org.wavemoney.payment.api.exception.validation.BadRequestException;
import org.springframework.stereotype.Service;
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
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private WalletCreateRepository walletCreateRepository;

    @CacheEvict(value = "wallets", key = "#request.walletId")
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
                .createdAt(LocalDateTime.now())
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
        transaction.setCompletedAt(LocalDateTime.now());

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
                .type(transaction.getType().name())
                .createdAt(transaction.getCreatedAt())
                .completedAt(transaction.getCompletedAt())
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
                .createdAt(LocalDateTime.now())
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
        transaction.setCompletedAt(LocalDateTime.now());
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
                .createdAt(transaction.getCreatedAt())
                .completedAt(transaction.getCompletedAt())
                .build();

    }

// @Service
// public class TransactionServiceImpl implements TransactionService {

//     public final TransactionRepository transactionRepository;
//     public final WalletCreateRepository walletCreateRepository;

//     public TransactionServiceImpl(TransactionRepository transactionRepository, WalletCreateRepository walletCreateRepository) {
//         this.transactionRepository = transactionRepository;
//         this.walletCreateRepository = walletCreateRepository;
//     }

    @Override
    public TransactionResponseDto withdrawMoney(WithdrawRequestDto withdrawRequestDto) {

        Wallet wallet = walletCreateRepository
                .findByWalletId(withdrawRequestDto.getFromWalletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(
                withdrawRequestDto.getAmount()) < 0) {

            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(
                wallet.getBalance().subtract(
                        withdrawRequestDto.getAmount())
        );



        // 5. Save transaction to DB


        Wallet savedWallet = walletCreateRepository.save(wallet);

        TransactionResponseDto transactionResponseDto = new TransactionResponseDto();

        transactionResponseDto.setTransactionId(UUID.randomUUID().toString());

        transactionResponseDto.setFromWalletId(savedWallet.getWalletId());

        transactionResponseDto.setAmount(savedWallet.getBalance());

        transactionResponseDto.setCurrency(savedWallet.getCurrency());

        transactionResponseDto.setPhoneNumber(savedWallet.getPhoneNumber());

        transactionResponseDto.setType(TransactionType.WITHDRAWAL);

        transactionResponseDto.setStatus(TransactionStatus.COMPLETED);

        transactionResponseDto.setCreatedAt(LocalDateTime.now());

        transactionResponseDto.setCompletedAt(LocalDateTime.now());

        transactionResponseDto.setWithdrawAmount(withdrawRequestDto.getAmount());

        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setFromWalletId(savedWallet.getWalletId());
        transaction.setToWalletId(null); // null for withdrawal
        transaction.setAmount(withdrawRequestDto.getAmount());
        transaction.setCurrency(savedWallet.getCurrency());
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setDescription("Withdrawal from wallet");
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setCompletedAt(LocalDateTime.now());

        transactionRepository.save(transaction);
        return transactionResponseDto;
    }

    // get transaction history
    @Override
    public List<TransactionHistoryResponse> getTransactions(String walletId) {

        List<Transaction> transactions =
                transactionRepository.findByFromWalletIdOrToWalletId(walletId, walletId);

        return transactions.stream()
                .map(tx -> {

                    String role = switch (tx.getType()) {

                        case DEPOSIT -> "RECEIVED";

                        case WITHDRAWAL -> "SENT";

                        case TRANSFER -> {
                            if (walletId.equals(tx.getFromWalletId())) {
                                yield "SENT";
                            } else {
                                yield "RECEIVED";
                            }
                        }
                    };

                    return TransactionHistoryResponse.builder()
                            .transactionId(tx.getTransactionId())
                            .type(tx.getType())
                            .status(tx.getStatus())
                            .amount(tx.getAmount())
                            .currency(tx.getCurrency())
                            .fromWalletId(tx.getFromWalletId())
                            .toWalletId(tx.getToWalletId())
                            .role(role)
                            .createdAt(tx.getCreatedAt())
                            .completedAt(tx.getCompletedAt())
                            .build();
                })
                .toList();
    }

    @Override
    public TransactionResponseDto transactionDetails(String transactionId) {

        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Map the transaction entity to your response DTO
        TransactionResponseDto responseDto = new TransactionResponseDto();
        responseDto.setTransactionId(transaction.getTransactionId());
        responseDto.setFromWalletId(transaction.getFromWalletId());
        responseDto.setToWalletId(transaction.getToWalletId());
        responseDto.setAmount(transaction.getAmount());
        responseDto.setCurrency(transaction.getCurrency());
        responseDto.setStatus(transaction.getStatus());
        responseDto.setType(transaction.getType());
        responseDto.setCreatedAt(transaction.getCreatedAt());
        responseDto.setCompletedAt(transaction.getCompletedAt());

        return responseDto;
    }



    }
