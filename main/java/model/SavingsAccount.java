package model;

/**
 * Savings account: intended for future saving. By business rule this account
 * does not permit withdrawals (only deposits and interest accrual).
 */
public class SavingsAccount extends Account {
    private double interestRate;

    public SavingsAccount() { super(); }

    public SavingsAccount(long id, long customerId, String accountNumber, double balance, double interestRate) {
        super(id, customerId, accountNumber, balance);
        this.interestRate = interestRate;
    }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    // No withdrawals allowed for SavingsAccount per business rule
    public boolean withdraw(double amount) { return false; }

    public void applyMonthlyInterest() {
        if (interestRate > 0) {
            this.balance += this.balance * interestRate / 12.0;
        }
    }
}
