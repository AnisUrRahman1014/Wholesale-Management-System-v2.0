package Controller;

import java.util.ArrayList;
import Model.Role;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Anis Ur Rahman
 */
public class RoleController {
    static Connection con;
    static PreparedStatement pst;
    static ResultSet rs;
    
    public static ArrayList<Role> getRolesList(){
        ArrayList<Role> roles = new ArrayList<>();
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("select * from employeeroles");
            rs = pst.executeQuery();
            while(rs.next()){
                String roleName = rs.getString(1);
                int payPerDay = rs.getInt(2);
                Role role = new Role(roleName, payPerDay);
                roles.add(role);
            }
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null,"SQLError | Get Roles", e.getMessage());
        }finally{
            try{
                if(con!=null){
                    con.close();
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return roles;
    }
    
    public static boolean addRole(Role role){
        boolean success =false;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("insert into employeeroles values(?,?)");
            pst.setString(1,role.getRoleName());
            pst.setInt(2, role.getPayPerHour());
            pst.execute();
            success=true;
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null,"SQLError | Get Roles", e.getMessage());
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
    
    public static boolean updateRole(Role role){
        boolean success =false;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("update employeeroles set payPerDay = ? where roleName=?");
            pst.setInt(1, role.getPayPerHour());
            pst.setString(2,role.getRoleName());
            pst.executeUpdate();
            success=true;
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null,"SQLError | Get Roles", e.getMessage());
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
    
    public static Role getRole(String roleName){
        Role role = null;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("select * from employeeroles where roleName=?");
            pst.setString(1, roleName);
            rs = pst.executeQuery();
            if(rs.next()){
                int payPerDay = rs.getInt(2);
                role = new Role(roleName,payPerDay);
            }
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null,"SQLError | Get Role", e.getMessage());
            e.printStackTrace();
        }finally{
            try{
                if(con!=null){
                    con.close();
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return role;
    }
}
