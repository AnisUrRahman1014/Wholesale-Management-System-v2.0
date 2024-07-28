package Model;

import java.sql.Timestamp;

/**
 *
 * @author Anis Ur Rahman
 */
public class Transaction {
    private Customer customer;
    private Timestamp date;
    private int oldCredit;
    private int deposit;
    private int remainingCredit;

    public Transaction(Customer customer) {
        this.customer = customer;
    }

    public Transaction(Customer customer, Timestamp date, int oldCredit, int deposit, int remainingCredit) {
        this.customer = customer;
        this.date = date;
        this.oldCredit = oldCredit;
        this.deposit = deposit;
        this.remainingCredit = remainingCredit;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Timestamp getDate() {
        return date;
    }

    public int getOldCredit() {
        return oldCredit;
    }

    public int getDeposit() {
        return deposit;
    }

    public int getRemainingCredit() {
        return remainingCredit;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public void setOldCredit(int oldCredit) {
        this.oldCredit = oldCredit;
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

    public void setRemainingCredit(int remainingCredit) {
        this.remainingCredit = remainingCredit;
    }
    
    
}
