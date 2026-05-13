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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private WalletCreateRepository walletCreateRepository;

    @CacheEvict(value = "wallets", key = "#request.phoneNumber")
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
                .findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Wallet not found with phone: "
                                        + request.getPhoneNumber()
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
                .fromWalletId(wallet.getWalletId())
                .toWalletId(wallet.getWalletId())
                .fromPhoneNumber(wallet.getPhoneNumber())
                .toPhoneNumber(wallet.getPhoneNumber())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.PENDING)
                .description("Deposit money to wallet")
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
                .phoneNumber(wallet.getPhoneNumber())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .balance(wallet.getBalance())
                .status(transaction.getStatus().name())
                .type(transaction.getType().name())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .completedAt(transaction.getCompletedAt())
                .build();
    }

    @Override
    @Transactional
    public TransferResponseDto transferMoney(TransferRequestDto request){
        //here even if don't check request's amount dto handle it and throw bad req error but not message included
        if(request.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new BadRequestException("Amount must be greater than zero");
        }

        // prevent self transfer
        if (request.getFromPhoneNumber()
                .equals(request.getToPhoneNumber())) {

            throw new BadRequestException(
                    "Cannot transfer to same wallet"
            );
        }

        // find both wallets
        Wallet sender = walletCreateRepository
                .findByPhoneNumber(request.getFromPhoneNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Sender wallet not found"
                        )
                );

        Wallet receiver = walletCreateRepository
                .findByPhoneNumber(request.getToPhoneNumber())
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
                .fromPhoneNumber(sender.getPhoneNumber())
                .toPhoneNumber(receiver.getPhoneNumber())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .description(request.getDescription())
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
                .fromPhoneNumber(sender.getPhoneNumber())
                .toPhoneNumber(receiver.getPhoneNumber())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .type(TransactionType.TRANSFER)
                .status(transaction.getStatus().name())
                .senderBalance(sender.getBalance())
                .receiverBalance(receiver.getBalance())
                .description(transaction.getDescription())
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
                .findByPhoneNumber(withdrawRequestDto.getPhoneNumber())
                .orElseThrow(() -> new BadRequestException("Wallet not found"));

        if (wallet.getBalance().compareTo(
                withdrawRequestDto.getAmount()) < 0) {

            throw new BadRequestException("Insufficient balance");
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

        transactionResponseDto.setFromPhoneNumber(savedWallet.getPhoneNumber());

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
        transaction.setFromPhoneNumber(savedWallet.getPhoneNumber());
        //transaction.setToPhoneNumber(savedWallet.getPhoneNumber());
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
    public List<TransactionHistoryResponse> getTransactions(String phoneNumber) {

        List<Transaction> transactions =
                transactionRepository.findByFromPhoneNumberOrToPhoneNumber(phoneNumber, phoneNumber);

        return transactions.stream()
                .map(tx -> {

                    String role = switch (tx.getType()) {

                        case DEPOSIT -> "RECEIVED";

                        case WITHDRAWAL -> "SENT";

                        case TRANSFER -> {
                            if (phoneNumber.equals(tx.getFromPhoneNumber())) {
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
                            .fromPhoneNumber(tx.getFromPhoneNumber())
                            .toPhoneNumber(tx.getToPhoneNumber())
                            .description(tx.getDescription())
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
                .orElseThrow(() -> new BadRequestException("Transaction not found"));

        // Map the transaction entity to your response DTO
        TransactionResponseDto responseDto = new TransactionResponseDto();
        responseDto.setTransactionId(transaction.getTransactionId());
        responseDto.setFromWalletId(transaction.getFromWalletId());
        responseDto.setToWalletId(transaction.getToWalletId());
        responseDto.setFromPhoneNumber(transaction.getFromPhoneNumber());
        responseDto.setToPhoneNumber(transaction.getToPhoneNumber());
        responseDto.setAmount(transaction.getAmount());
        responseDto.setFromPhoneNumber(transaction.getFromPhoneNumber());
        responseDto.setToPhoneNumber(transaction.getToPhoneNumber());
        responseDto.setCurrency(transaction.getCurrency());
        responseDto.setStatus(transaction.getStatus());
        responseDto.setType(transaction.getType());
        responseDto.setDescription(transaction.getDescription());
        responseDto.setCreatedAt(transaction.getCreatedAt());
        responseDto.setCompletedAt(transaction.getCompletedAt());

        return responseDto;
    }

    @Override
    public List<TransactionResponseDto> getDailyTransactions() {

        LocalDateTime startOfDay = LocalDateTime.now()
                .toLocalDate()
                .atStartOfDay();

        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<Transaction> transactions =
                transactionRepository.findByCreatedAtBetween(
                        startOfDay,
                        endOfDay
                );

        List<TransactionResponseDto> responseList = new ArrayList<>();

        for (Transaction transaction : transactions) {

            TransactionResponseDto dto =
                    new TransactionResponseDto();

            dto.setTransactionId(transaction.getTransactionId());

            dto.setFromWalletId(transaction.getFromWalletId());

            dto.setToWalletId(transaction.getToWalletId());

            dto.setFromPhoneNumber(transaction.getFromPhoneNumber());

            dto.setToPhoneNumber(transaction.getToPhoneNumber());

            dto.setAmount(transaction.getAmount());

            dto.setCurrency(transaction.getCurrency());

            dto.setFromPhoneNumber(transaction.getFromPhoneNumber());

            dto.setToPhoneNumber(transaction.getToPhoneNumber());

            dto.setStatus(transaction.getStatus());

            dto.setType(transaction.getType());

            dto.setDescription(transaction.getDescription());

            dto.setCreatedAt(transaction.getCreatedAt());

            dto.setCompletedAt(transaction.getCompletedAt());

            responseList.add(dto);
        }

        return responseList;
    }



}
