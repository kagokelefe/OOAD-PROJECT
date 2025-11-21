package model.account;

public class InvestmentAccount extends BankAccount {
    private String investmentType;

    public InvestmentAccount() {}

    public InvestmentAccount(long id, long userId, String accountNumber, double balance, String investmentType) {
        super(id, userId, accountNumber, balance);
        this.investmentType = investmentType;
    }

    public String getInvestmentType() { return investmentType; }
    public void setInvestmentType(String investmentType) { this.investmentType = investmentType; }
}
