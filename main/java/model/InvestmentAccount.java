package model;

/**
 * Investment account: pays higher interest and allows withdrawals.
 * Business rule: must be opened with a minimum initial deposit.
 */
public class InvestmentAccount extends Account implements Withdrawable {
    public static final double MIN_OPENING = 500.0;
    private String investmentType;

    public InvestmentAccount() { super(); }

    public InvestmentAccount(long id, long customerId, String accountNumber, double balance, String investmentType) {
        super(id, customerId, accountNumber, balance);
        if (balance < MIN_OPENING) throw new IllegalArgumentException("Investment accounts require a minimum opening deposit of " + MIN_OPENING);
        this.investmentType = investmentType;
    }

    public String getInvestmentType() { return investmentType; }
    public void setInvestmentType(String investmentType) { this.investmentType = investmentType; }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) return false;
        balance -= amount;
        return true;
    }
}
