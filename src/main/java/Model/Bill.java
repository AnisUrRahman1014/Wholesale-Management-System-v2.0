package Model;

import Controller.BillController;
import Controller.ProductController;
import Controller.connect;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Bill {
    private String billId;
    private Date date;
    private Customer customer;
    private ArrayList<BillItem> billItems;
    private int totalBill;
    private int totalFinalBill;
    private int discountOnTotal;
    private int tax;
    private int deposit;
    private int credit;

    static Connection con;
    static PreparedStatement pst;
    static ResultSet rs;
    
    public Bill(String billId, Date date, Customer customer, ArrayList<BillItem> billItems, int totalBill, int totalFinalBill, int discountOnTotal, int tax, int deposit) {
        this.billId = billId;
        this.date = date;
        this.customer = customer;
        this.billItems = billItems;
        this.totalBill = totalBill;
        this.totalFinalBill = totalFinalBill;
        this.discountOnTotal = discountOnTotal;
        this.tax = tax;
        this.deposit = deposit;
        this.credit = this.totalFinalBill-this.deposit;
    }
    
    
    
    public String getBillId() {
        return billId;
    }

    public Date getDate() {
        return date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public ArrayList<BillItem> getBillItems() {
        return billItems;
    }

    public int getTotalBill() {
        return totalBill;
    }

    public int getTotalFinalBill() {
        return totalFinalBill;
    }

    public int getDiscountOnTotal() {
        return discountOnTotal;
    }

    public int getTax() {
        return tax;
    }

    public int getDeposit() {
        return deposit;
    }

    public int getCredit() {
        return credit;
    }
    
    public static ArrayList<Bill> getBillsList(Customer ctm){
        ArrayList<Bill> bills= new ArrayList<>();
        try{
            con = connect.connectDB();
            pst= con.prepareStatement("select * from bills where customer='"+ctm.getName()+"'");
            rs = pst.executeQuery();
            while(rs.next()){
                String billId = rs.getString(1);
                Bill bill = BillController.getBill(billId, ctm);
                bills.add(bill);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(con!=null){
                    con.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return bills;
    }
    
    
}
