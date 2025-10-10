public class investmentaccount extends account {

    private double interest;

    public investmentaccount() {
    }

    public investmentaccount(Long id, String accountNumber, double balance, double interest) {
        super(id, accountNumber, balance);
        this.interest = interest;
    }

    public double getInterest() {
         return interest; }
    public void setInterest(double interest) { 
        this.interest = interest; }
}
