package org.wavemoney.payment.api.services.impl;

import org.springframework.stereotype.Service;
import org.wavemoney.payment.api.dto.TransactionResponseDto;
import org.wavemoney.payment.api.dto.WithdrawRequestDto;
import org.wavemoney.payment.api.model.Transaction;
import org.wavemoney.payment.api.model.Wallet;
import org.wavemoney.payment.api.model.enums.TransactionStatus;
import org.wavemoney.payment.api.model.enums.TransactionType;
import org.wavemoney.payment.api.repository.TransactionRepository;
import org.wavemoney.payment.api.repository.WalletCreateRepository;
import org.wavemoney.payment.api.services.TransactionService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
public class TransactionServiceImpl implements TransactionService {

    public final TransactionRepository transactionRepository;
    public final WalletCreateRepository walletCreateRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, WalletCreateRepository walletCreateRepository) {
        this.transactionRepository = transactionRepository;
        this.walletCreateRepository = walletCreateRepository;
    }

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

        transactionResponseDto.setType(TransactionResponseDto.TransactionType.WITHDRAWAL);

        transactionResponseDto.setStatus(TransactionResponseDto.TransactionStatus.COMPLETED);

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


}
