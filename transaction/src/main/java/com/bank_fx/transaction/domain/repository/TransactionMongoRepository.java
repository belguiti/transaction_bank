package com.bank_fx.transaction.domain.repository;



import com.bank_fx.transaction.domain.model.TransactionMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionMongoRepository extends MongoRepository<TransactionMongo, String> {

    List<TransactionMongo> findByFromAccountOrToAccount(String fromAccount, String toAccount);
}
