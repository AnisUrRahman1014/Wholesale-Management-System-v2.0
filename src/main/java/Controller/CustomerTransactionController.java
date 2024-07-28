package Controller;

/**
 *
 * @author Anis Ur Rahman
 */
import Model.Customer;
import Model.Transaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
public class CustomerTransactionController {
    static Connection con;
    static PreparedStatement pst;
    static ResultSet rs;
    
    public static Transaction getLastDeposit(Customer ctm){
        Transaction tr = null;
        try{
            con = connect.connectDB();
            String query = "SELECT * FROM customerTransactions WHERE customerID = ? ORDER BY date DESC FETCH FIRST ROW ONLY";
            pst = con.prepareStatement(query);
            pst.setString(1, ctm.getName());

            rs = pst.executeQuery();
            if(rs.next()){
                Timestamp date = rs.getTimestamp(2);
                int oldCredit = rs.getInt(3);
                int deposit = rs.getInt(4);
                int remainingCredit = rs.getInt(5);
                tr = new Transaction(ctm, date, oldCredit, deposit, remainingCredit);
            }
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null, "SQLError: Customer Transaction Controller", e.getMessage());
        }finally{
            try{
                if(con!=null){
                    con.close();
                }
            }catch(SQLException e){
                ManagementSystemCPU.errorAlert(null, "SQLError: Customer Transaction Controller", e.getMessage());
            }
        }
        return tr;
    }
    
    public static boolean addTransaction(Transaction tr){
        boolean success = false;
        try{
            con = connect.connectDB();
            String query = "INSERT INTO customerTransactions VALUES(?,?,?,?,?)";
            pst = con.prepareStatement(query);
            pst.setString(1,tr.getCustomer().getName());
            pst.setTimestamp(2, tr.getDate());
            pst.setInt(3, tr.getOldCredit());
            pst.setInt(4, tr.getDeposit());
            pst.setInt(5,tr.getRemainingCredit());
            pst.execute();
            success=true;
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null, "SQLError: Customer Transaction Controller", e.getMessage());
        }finally{
            try{
                if(con!=null){
                    con.close();
                }
            }catch(SQLException e){
                ManagementSystemCPU.errorAlert(null, "SQLError: Customer Transaction Controller", e.getMessage());
            }
        }
        return success;
    }
    
    public static int getTotalDeposit(Customer ctm){
        int totalDeposit = 0;
        try{
            con = connect.connectDB();
            String query = "select SUM(deposit) as totalDeposit from customerTransactions where customerID = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, ctm.getName());
            rs = pst.executeQuery();
            if(rs.next()){
                totalDeposit = rs.getInt(1);
            }
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null, "SQLError: Customer Transaction Controller", e.getMessage());
        }finally{
            try{
                if(con!=null){
                    con.close();
                }
            }catch(SQLException e){
                ManagementSystemCPU.errorAlert(null, "SQLError: Customer Transaction Controller", e.getMessage());
            }
        }
        return totalDeposit;
    }
    
    public static ArrayList<Transaction> getTransactionList(Customer ctm){
        ArrayList<Transaction> transactions = new ArrayList();
        try{
            con = connect.connectDB();
            String query = "select * from customerTransactions where customerID = ?";
            pst = con.prepareStatement(query);    
            pst.setString(1,ctm.getName());
            rs = pst.executeQuery();
            while(rs.next()){
                Timestamp date = rs.getTimestamp(2);
                int oldCredit = rs.getInt(3);
                int deposit = rs.getInt(4);
                int remainingCredit = rs.getInt(5);
                Transaction tr = new Transaction(ctm, date, oldCredit, deposit, remainingCredit);
                transactions.add(tr);
            }
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null, "SQLError: Customer Transaction Controller", e.getMessage());
        }finally{
            try{
                if(con!=null){
                    con.close();
                }
            }catch(SQLException e){
                ManagementSystemCPU.errorAlert(null, "SQLError: Customer Transaction Controller", e.getMessage());
            }
        }
        return transactions;
    }
}
