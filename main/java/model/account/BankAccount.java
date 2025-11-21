package model.account;

import model.Account;

public abstract class BankAccount extends Account {

    public BankAccount() { super(); }

    public BankAccount(long id, long userId, String accountNumber, double balance) {
        super(id, userId, accountNumber, balance);
    }

    // Compatibility helpers: previously code used getUserId()/setUserId()
    public long getUserId() { return getCustomerId(); }
    public void setUserId(long userId) { setCustomerId(userId); }

    // accountNumber, balance and status handled by parent Account
}
