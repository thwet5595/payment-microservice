package org.wavemoney.payment.api.services.impl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.wavemoney.payment.api.dto.WalletRequestDto;
import org.wavemoney.payment.api.dto.WalletResponseDto;
import org.wavemoney.payment.api.dto.WalletSummaryResponseDto;
import org.wavemoney.payment.api.exception.common.ResourceNotFoundException;
import org.wavemoney.payment.api.exception.validation.BadRequestException;
import org.wavemoney.payment.api.model.Transaction;
import org.wavemoney.payment.api.model.Wallet;
import org.wavemoney.payment.api.model.enums.TransactionType;
import org.wavemoney.payment.api.repository.TransactionRepository;
import org.wavemoney.payment.api.repository.WalletCreateRepository;
import org.wavemoney.payment.api.services.WalletCreateServices;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class WalletCreateServicesImpl implements WalletCreateServices {

    private final WalletCreateRepository walletCreateRepository;
    private final TransactionRepository transactionRepository;

    public WalletCreateServicesImpl(WalletCreateRepository walletCreateRepository, TransactionRepository transactionRepository) {
        this.walletCreateRepository = walletCreateRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public WalletResponseDto createWallet(WalletRequestDto walletRequestDto) {

        // Check existing wallet by phone number
        boolean walletExists =
                walletCreateRepository.existsByPhoneNumber(
                        walletRequestDto.getPhoneNumber());

        if (walletExists) {
            throw new BadRequestException(
                    "Wallet already exists for phone number: "
                            + walletRequestDto.getPhoneNumber());
        }

        Wallet wallet = new Wallet();

        wallet.setWalletId(UUID.randomUUID().toString());
        wallet.setUserId(walletRequestDto.getUserId());
        wallet.setPhoneNumber(walletRequestDto.getPhoneNumber());
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency("MMK");
        wallet.setStatus("ACTIVE");
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setUpdatedAt(LocalDateTime.now());

        // Save to MongoDB
        Wallet savedWallet = walletCreateRepository.save(wallet);

        // Entity -> DTO
        WalletResponseDto dto = new WalletResponseDto();

        dto.setWalletId(savedWallet.getWalletId());
        dto.setUserId(savedWallet.getUserId());
        dto.setPhoneNumber(savedWallet.getPhoneNumber());
        dto.setBalance(savedWallet.getBalance());
        dto.setCurrency(savedWallet.getCurrency());
        dto.setStatus(savedWallet.getStatus());

        return dto;
    }

    @Override
    @Cacheable(value="wallets", key="#phoneNumber")
    public WalletResponseDto getWallet(String phoneNumber) {

        Wallet wallet= walletCreateRepository.findByPhoneNumber(phoneNumber)  .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with phone number: " + phoneNumber));;

        WalletResponseDto dto= new WalletResponseDto();

        dto.setWalletId(wallet.getWalletId());
        dto.setUserId(wallet.getUserId());
        dto.setBalance(wallet.getBalance());
        dto.setPhoneNumber(wallet.getPhoneNumber());
        dto.setCurrency(wallet.getCurrency());
        dto.setStatus(wallet.getStatus());

        return dto;

    }

    @Override
    public WalletSummaryResponseDto getWalletSummary(String phoneNumber) {

        List<Transaction> transactions =
                transactionRepository.findByFromPhoneNumberOrToPhoneNumber(phoneNumber, phoneNumber);

        BigDecimal totalDeposits = BigDecimal.ZERO;
        BigDecimal totalWithdrawals = BigDecimal.ZERO;

        long totalTx = transactions.size();

        for (Transaction tx : transactions) {

            // ✅ Deposit (money coming INTO wallet)
            if (phoneNumber.equals(tx.getToPhoneNumber())
                    && tx.getType() == TransactionType.DEPOSIT) {

                totalDeposits = totalDeposits.add(tx.getAmount());
            }

            // ✅ Withdrawal (money going OUT of wallet)
            if (phoneNumber.equals(tx.getFromPhoneNumber())
                    && tx.getType() == TransactionType.WITHDRAWAL) {

                totalWithdrawals = totalWithdrawals.add(tx.getAmount());
            }

            // 🔥 If you support TRANSFER type (recommended)
            if (tx.getType() == TransactionType.TRANSFER) {

                if (phoneNumber.equals(tx.getToPhoneNumber())) {
                    totalDeposits = totalDeposits.add(tx.getAmount());
                }

                if (phoneNumber.equals(tx.getFromPhoneNumber())) {
                    totalWithdrawals = totalWithdrawals.add(tx.getAmount());
                }
            }
        }

        BigDecimal currentBalance = totalDeposits.subtract(totalWithdrawals);

        WalletSummaryResponseDto dto = new WalletSummaryResponseDto();
        dto.setPhoneNumber(phoneNumber);
        dto.setTotalDeposits(totalDeposits);
        dto.setTotalWithdrawals(totalWithdrawals);
        dto.setCurrentBalance(currentBalance);
        dto.setNetFlow(currentBalance);
        dto.setTotalTransactions(totalTx);

        return dto;
    }


}