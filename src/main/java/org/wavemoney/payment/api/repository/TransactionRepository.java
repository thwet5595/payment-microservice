package org.wavemoney.payment.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.wavemoney.payment.api.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.wavemoney.payment.api.model.Transaction;
import org.wavemoney.payment.api.model.Wallet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    Optional<Transaction> findByFromWalletId(String walletId);

    List<Transaction> findByFromWalletIdOrToWalletId(String fromWalletId, String toWalletId);

    Optional<Transaction> findByTransactionId(String transactionId);

    List<Transaction> findByCreatedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );


}
