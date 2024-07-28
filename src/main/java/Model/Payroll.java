package Model;

import java.sql.Date;

/**
 *
 * @author Anis Ur Rahman
 */
public class Payroll {
    private Employee emp;
    private Date invoiceDate, fromDate, toDate;
    private int totalDays,totalHrs,payment, payrollId;

    public Payroll(Employee emp, Date invoiceDate, Date fromDate, Date toDate, int totalDays, int totalHrs, int payment, int payrollId) {
        this.emp = emp;
        this.invoiceDate = invoiceDate;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.totalDays = totalDays;
        this.totalHrs = totalHrs;
        this.payment = payment;
        this.payrollId = payrollId;
    }

    public int getPayrollId() {
        return payrollId;
    }
    
    

    public Employee getEmp() {
        return emp;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public int getTotalHrs() {
        return totalHrs;
    }

    public int getPayment() {
        return payment;
    }
    
    
}
