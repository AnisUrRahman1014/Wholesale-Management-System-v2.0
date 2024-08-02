package Controller;

/**
 *
 * @author Anis Ur Rahman
 */
import Model.Bill;
import Model.BillItem;
import Model.Customer;
import Model.Product;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
public class BillController {
    static Connection con;
    static PreparedStatement pst;
    static ResultSet rs;
    public static boolean recordBill(Bill bill){
        boolean success = false;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("insert into bills values(?,?,?,?,?,?,?,?,?)");
            pst.setString(1, bill.getBillId());
            pst.setString(2, bill.getCustomer().getName());   
            pst.setDate(3, bill.getDate());
            pst.setInt(4, bill.getTotalBill());
            pst.setInt(5, bill.getTotalFinalBill());
            pst.setInt(6, bill.getDiscountOnTotal());
            pst.setInt(7,bill.getDeposit());
            pst.setInt(8,bill.getCredit());
            pst.setInt(9,bill.getTax());
            
            pst.execute();
            pst.close();
            pst = con.prepareStatement("insert into billItems values(?,?,?,?,?,?)");
            for(BillItem item: bill.getBillItems()){                
                pst.setString(1, bill.getBillId());
                pst.setString(2, item.getProduct().getProdID());
                pst.setDouble(3,item.getQuantity());
                pst.setInt(4,item.getRatePerUnit());
                pst.setInt(5,item.getDiscount());
                pst.setInt(6,item.getTotal());
                pst.execute();
            }
            
            // UPDATE PRODUCT QUANTITY
            for(BillItem item: bill.getBillItems()){
                Product prod = item.getProduct();
                ProductController.handleProductSale(prod, item.getQuantity());
            }
            success=true;
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
        return success;
    }
    
    public static Bill getBill(String billId, Customer ctm){
        Bill bill = null;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("select * from bills where billId='"+billId+"'");
            rs = pst.executeQuery();{
            if(rs.next()){
                Date date=rs.getDate(3);
                int totalBill = rs.getInt(4);
                int totalFinalBill = rs.getInt(5);
                int discountOnTotal = rs.getInt(6);
                int depost = rs.getInt(7);
                int tax = rs.getInt(9);
                ArrayList<BillItem> billItems = new ArrayList<>();
                PreparedStatement pst2 = con.prepareStatement("select * from billItems where billId = '"+billId+"'");
                ResultSet rs2 = pst2.executeQuery();
                while(rs2.next()){
                    String prodId = rs2.getString(2);
                    double quantity = rs2.getInt(3);
                    int ratePerUnit = rs2.getInt(4);
                    int discount = rs2.getInt(5);
                    int total = rs2.getInt(6);
                    Product prod = ProductController.getProduct(prodId);
                    BillItem billItem = new BillItem(prod);
                    billItem.setQuantity(quantity);
                    billItem.setDiscount(discount);
                    billItem.setRatePerUnit(ratePerUnit);
                    billItem.setUnitType(prod.getUnit());
                    billItem.setTotal(total);
                    billItems.add(billItem);
                }
                bill = new Bill(billId, date, ctm, billItems, totalBill, totalFinalBill, discountOnTotal, tax, depost);
            }
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
        return bill;
    }
    
    public static int getTodaysBillCount(){
        int count = -1;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("""
                                       SELECT COUNT(billId) AS todaysBillCount 
                                       FROM bills 
                                       WHERE date = CURRENT_DATE""");
            rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt("todaysBillCount"); // Get the count from the ResultSet
                count+=1;
            }else{
                count=0;
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
        return count;
    }
    
    public static int getBillCount(Date date){
        int count = -1;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("""
                                       SELECT COUNT(billId) AS todaysBillCount 
                                       FROM bills 
                                       WHERE date = ?""");
            pst.setDate(1,date);
            rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt("todaysBillCount"); // Get the count from the ResultSet
                count+=1;
            }else{
                count=0;
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
        return count;
    }
    
    public static boolean deleteBill(String billId, int billCredit, String ctmId){
        boolean success = false;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("Delete from billItems where billId='"+billId+"'");
            pst.execute();
            pst = con.prepareStatement("Delete from bills where billId='"+billId+"'");
            pst.execute();
            
            String getLastTransactionQuery = "SELECT * FROM customerTransactions WHERE customerID = ? ORDER BY date DESC FETCH FIRST ROW ONLY";
            pst = con.prepareStatement(getLastTransactionQuery);
            pst.setString(1, ctmId);
            rs = pst.executeQuery();
            
            if(rs.next()){
                Timestamp date=rs.getTimestamp("date");
                int oldRemainingCredit = rs.getInt("remainingCredit");
                int newRemainingCredit=oldRemainingCredit-billCredit;
                PreparedStatement updateLastTransaction=con.prepareStatement("update customerTransactions set remainingCredit = ? where customerId = ? and date = ?");
                updateLastTransaction.setInt(1, newRemainingCredit);
                updateLastTransaction.setString(2, ctmId);
                updateLastTransaction.setTimestamp(3, date);
                updateLastTransaction.executeUpdate();
            }
            success=true;
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
        return success;
    }
}
