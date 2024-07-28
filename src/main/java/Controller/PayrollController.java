package Controller;

import Model.Payroll;
import Model.Employee;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Anis Ur Rahman
 */
public class PayrollController {
    static Connection con;
    static PreparedStatement pst;
    static ResultSet rs;
    
    public static boolean addInvoice(Payroll payroll){
        boolean success = false;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("insert into payrolls values(?,?,?,?,?,?,?,?)");
            pst.setString(1,payroll.getEmp().getName());
            pst.setDate(2,payroll.getInvoiceDate());
            pst.setDate(3,payroll.getFromDate());
            pst.setDate(4,payroll.getToDate());
            pst.setInt(5, payroll.getTotalDays());
            pst.setInt(6,payroll.getTotalHrs());
            pst.setInt(7,payroll.getPayment());
            pst.setInt(8,payroll.getPayrollId());
            pst.execute();
            success = true;
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null,"SQL Error | Add invoice",e.getMessage());
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
    
    public static int getPayrollsCount(Employee emp){
        int count = 0;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("select count(employeeId) as payrollCount from payrolls where employeeId = ?");
            pst.setString(1,emp.getName());            
            rs = pst.executeQuery();
            if(rs.next()){
                count = rs.getInt(1);
            }
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null,"SQL Error | Payroll count",e.getMessage());
        }finally{
            try{
                if(con!=null){
                    con.close();
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return count;
    }
}
