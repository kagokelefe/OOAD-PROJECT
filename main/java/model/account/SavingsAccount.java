package model.account;

import model.Withdrawable;

public class SavingsAccount extends BankAccount implements Withdrawable {
    private double interestRate;

    public SavingsAccount() {}

    public SavingsAccount(long id, long userId, String accountNumber, double balance, double interestRate) {
        super(id, userId, accountNumber, balance);
        this.interestRate = interestRate;
    }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) return false;
        balance -= amount;
        return true;
    }
}
