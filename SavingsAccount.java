public class SavingsAccount extends account {

    private double interestRate;

    public SavingsAccount() {
    }

    public SavingsAccount(Long id, String accountNumber, double balance, double interestRate) {
        super(id, accountNumber, balance);
        this.interestRate = interestRate;
    }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
}
