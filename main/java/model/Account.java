package model;

import java.util.Objects;

/**
 * Abstract account model used across the application.
 */
public abstract class Account {
    protected long id;
    protected long customerId;
    protected String accountNumber;
    protected double balance;
    protected String branch;
    protected String status; // PENDING / APPROVED

    public Account() {}

    public Account(long id, long customerId, String accountNumber, double balance) {
        this.id = id;
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.status = "PENDING";
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getCustomerId() { return customerId; }
    public void setCustomerId(long customerId) { this.customerId = customerId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit must be positive");
        this.balance += amount;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + accountNumber + ", balance=" + balance + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() { return Objects.hash(accountNumber); }
}
