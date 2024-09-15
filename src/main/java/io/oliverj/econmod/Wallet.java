package io.oliverj.econmod;

public class Wallet {
    private double balance;

    public double getBalance() { return balance; }

    public Wallet(double balance) {
        this.balance = balance;
    }

    public void setBalance(double balance) { this.balance = balance; }
}
