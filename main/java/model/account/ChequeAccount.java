package model.account;

import model.Withdrawable;

public class ChequeAccount extends BankAccount implements Withdrawable {
    private double overdraftLimit;
    // employer info (added for persistence compatibility)
    private String employer;
    private String employerAddress;

    public ChequeAccount() {}

    public ChequeAccount(long id, long userId, String accountNumber, double balance, double overdraftLimit) {
        super(id, userId, accountNumber, balance);
        this.overdraftLimit = overdraftLimit;
    }

    public String getEmployer() { return employer; }
    public void setEmployer(String employer) { this.employer = employer; }

    public String getEmployerAddress() { return employerAddress; }
    public void setEmployerAddress(String employerAddress) { this.employerAddress = employerAddress; }

    public double getOverdraftLimit() { return overdraftLimit; }
    public void setOverdraftLimit(double overdraftLimit) { this.overdraftLimit = overdraftLimit; }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) return false;
        if (balance - amount < -overdraftLimit) return false;
        balance -= amount;
        return true;
    }
}
