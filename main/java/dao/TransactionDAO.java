package dao;

import model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionDAO {
    Optional<Transaction> findById(long id);
    List<Transaction> findByAccountId(long accountId);
    Transaction save(Transaction tx);
    void update(Transaction tx);
}
