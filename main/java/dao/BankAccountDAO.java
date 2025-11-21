package dao;

import model.account.BankAccount;

import java.util.List;
import java.util.Optional;

public interface BankAccountDAO {
    Optional<BankAccount> findById(long id);
    List<BankAccount> findByUserId(long userId);
    List<BankAccount> findByStatus(String status);
    List<BankAccount> findAll();
    BankAccount save(BankAccount account);
    void update(BankAccount account);
    void delete(long id);
}
