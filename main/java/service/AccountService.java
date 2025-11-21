package service;

import dao.BankAccountDAO;
import dao.jdbc.BankAccountDAOImpl;
import model.account.BankAccount;
import model.Withdrawable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AccountService {
    private final BankAccountDAO accountDAO = new BankAccountDAOImpl();

    public List<BankAccount> getAccountsForUser(long userId) {
        return accountDAO.findByUserId(userId).stream()
                .filter(acc -> "APPROVED".equals(acc.getStatus()))
                .collect(Collectors.toList());
    }

    public Optional<BankAccount> findById(long id) {
        return accountDAO.findById(id);
    }

    public BankAccount createAccount(BankAccount account) {
        return accountDAO.save(account);
    }

    public boolean withdraw(long accountId, double amount) {
        Optional<BankAccount> acc = accountDAO.findById(accountId);
        if (acc.isEmpty()) return false;
        BankAccount account = acc.get();
        if (account instanceof Withdrawable w) {
            boolean ok = w.withdraw(amount);
            accountDAO.update(account);
            return ok;
        }
        return false;
    }

    public List<BankAccount> getPendingAccounts() {
        return accountDAO.findByStatus("PENDING");
    }
}
