package model;

/**
 * Cheque account: allows deposits and withdrawals and may include overdraft.
 * Business rule: can only be opened for working customers; employer information
 * should be provided.
 */
public class ChequeAccount extends Account implements Withdrawable {
    private double overdraftLimit;
    private String employer;
    private String employerAddress;

    public ChequeAccount() { super(); }

    public ChequeAccount(long id, long customerId, String accountNumber, double balance, double overdraftLimit) {
        super(id, customerId, accountNumber, balance);
        this.overdraftLimit = overdraftLimit;
    }

    public double getOverdraftLimit() { return overdraftLimit; }
    public void setOverdraftLimit(double overdraftLimit) { this.overdraftLimit = overdraftLimit; }

    public String getEmployer() { return employer; }
    public void setEmployer(String employer) { this.employer = employer; }

    public String getEmployerAddress() { return employerAddress; }
    public void setEmployerAddress(String employerAddress) { this.employerAddress = employerAddress; }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) return false;
        if (balance - amount < -overdraftLimit) return false;
        balance -= amount;
        return true;
    }
}
