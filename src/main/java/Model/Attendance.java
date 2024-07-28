package Model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 *
 * @author Anis Ur Rahman
 */
public class Attendance {
    private Employee emp;
    private Date date;
    private Timestamp arrivalTime;
    private Timestamp leaveTime;
    private int workHrs = 0;
    private int payPerHrs = 0;
    private int totalPay=0;

    public Attendance(Employee emp, Date date) {
        this.emp = emp;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }    
    

    public void setArrivalTime(Timestamp arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setLeaveTime(Timestamp leaveTime) {
        this.leaveTime = leaveTime;
        calculateWorkHrsAndPay();
    }   
    
    private void calculateWorkHrsAndPay() {
        if (arrivalTime != null && leaveTime != null) {
            long milliseconds = leaveTime.getTime() - arrivalTime.getTime();
            double hours = milliseconds / (1000.0 * 60 * 60);

            // If the difference is more than 5 minutes, consider it as one hour
            if (milliseconds > 300000) {
                hours = Math.ceil(hours);
            }

            this.workHrs = (int) Math.round(hours);
            if (hours - this.workHrs > 0 && hours - this.workHrs <= 0.5) {
                this.workHrs = (int) Math.floor(hours);
            } else if (hours - this.workHrs > 0.5) {
                this.workHrs = (int) Math.ceil(hours);
            }
            this.totalPay = workHrs * this.payPerHrs;
        } else {
            this.workHrs = 0;
            this.totalPay = 0;
        }
    }


    public int getWorkHrs() {
        return workHrs;
    }

    public int getTotalPay() {
        return totalPay;
    }    

    public Employee getEmp() {
        return emp;
    }

    public Timestamp getArrivalTime() {
        return arrivalTime;
    }

    public Timestamp getLeaveTime() {
        return leaveTime;
    }

    public int getPayPerHrs() {
        return payPerHrs;
    }

    public void setPayPerHrs(int payPerHrs) {
        this.payPerHrs = payPerHrs;
    }
    
    
}
