package Controller;

/**
 *
 * @author Anis Ur Rahman
 */
import Model.Employee;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
public class EmployeeController {
    static Connection con;
    static PreparedStatement pst;
    static ResultSet rs;
    
    public static boolean addEmployee(Employee emp){
        boolean success = false;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("insert into employees values(?,?,?,?,?,?,?)");
            pst.setString(1,emp.getName());
            pst.setString(2,emp.getContact());
            pst.setString(3,emp.getAddress());
            pst.setString(4, emp.getCnic());
            pst.setString(5, emp.getRole());
            pst.setInt(6, emp.getPayPerHour());
            pst.setInt(7,ManagementSystemCPU.ACTIVE);
            pst.execute();
            success = true;
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null,"SQLError | Add Employee",e.getMessage());
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
    
    public static boolean deleteEmployee(String empId){
        boolean success = false;
        try{
            con = connect.connectDB();           
            pst = con.prepareStatement("update employees set status = ? where employeeId = ?");
            pst.setInt(1, ManagementSystemCPU.NON_ACTIVE);
            pst.setString(2,empId);
            pst.executeUpdate();
            success = true;
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null,"SQLError | Add Employee",e.getMessage());
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
    
    public static ArrayList<Employee> getEmployeesList(){
        ArrayList<Employee> employees = new ArrayList();
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("select * from employees where status = ?");
            pst.setInt(1,ManagementSystemCPU.ACTIVE);
            rs = pst.executeQuery();
            while (rs.next()){
                String name = rs.getString(1);
                String contact = rs.getString(2);
                String address = rs.getString(3);
                String cnic = rs.getString(4);
                String roleName = rs.getString(5);
                int payPerHour = rs.getInt(6);
                Employee emp = new Employee(name, contact, address, cnic, roleName, payPerHour);
                employees.add(emp);
            }
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null,"SQLError | Add Employee",e.getMessage());
        }finally{
            try{
                if(con!=null){
                    con.close();
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return employees;
    }
    
    public static Employee getEmployee(String empId){
        Employee emp = null;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("select * from employees where employeeId=?");
            pst.setString(1, empId);
            rs = pst.executeQuery();
            if (rs.next()){
                String name = rs.getString(1);
                String contact = rs.getString(2);
                String address = rs.getString(3);
                String cnic = rs.getString(4);
                String roleName = rs.getString(5);
                int payPerHour = rs.getInt(6);
                emp = new Employee(name, contact, address, cnic, roleName, payPerHour);
            }
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null,"SQLError | Add Employee",e.getMessage());
        }finally{
            try{
                if(con!=null){
                    con.close();
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return emp;
    }
    
    public static boolean updateEmployee(Employee emp){
        boolean success = false;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("update employees set contact=?,address=?,cnic=?,role=?,payPerDay=? where employeeId=?");
            
            pst.setString(1,emp.getContact());
            pst.setString(2,emp.getAddress());
            pst.setString(3, emp.getCnic());
            pst.setString(4, emp.getRole());
            pst.setInt(5, emp.getPayPerHour());
            pst.setString(6,emp.getName());
            pst.executeUpdate();
            success = true;
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null,"SQLError | Add Employee",e.getMessage());
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
