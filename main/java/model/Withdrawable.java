package model;

public interface Withdrawable {
    /** Attempt to withdraw amount; return true if successful. */
    boolean withdraw(double amount);
}
