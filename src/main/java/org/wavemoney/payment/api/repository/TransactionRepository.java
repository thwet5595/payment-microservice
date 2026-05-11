package org.wavemoney.payment.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.wavemoney.payment.api.model.Transaction;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

}
