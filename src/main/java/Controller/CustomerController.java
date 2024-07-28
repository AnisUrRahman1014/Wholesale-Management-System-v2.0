package Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Model.Customer;

/**
 *
 * @author Anis Ur Rahman
 */
public class CustomerController {
    static Connection con;
    static PreparedStatement pst;
    static ResultSet rs;
    
    public static boolean updateCustomer(Customer ctm){
        boolean success=false;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("update customer set address=?, contact=? where name=?");
            pst.setString(1,ctm.getAddress());
            pst.setString(2, ctm.getContact());
            pst.setString(3,ctm.getName());
            pst.executeUpdate();
            success=true;
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null, "SQL Error | Updating Customer", e.getMessage());
        }finally{
            try{
                if(con!=null){
                    con.close();
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return success;
    }
}
