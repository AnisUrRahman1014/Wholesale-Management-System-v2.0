package Controller;

import Model.Attendance;
import Model.Employee;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 *
 * @author Anis Ur Rahman
 */
public class AttendanceController {
    static Connection con;
    static PreparedStatement pst;
    static ResultSet rs;
    
    public static boolean addAttendance(Attendance attendance){
        boolean success = false;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("insert into attendance values(?,?,?,?,?,?,?)");
            pst.setString(1,attendance.getEmp().getName());
            pst.setDate(2,attendance.getDate());
            pst.setTimestamp(3,attendance.getArrivalTime());
            pst.setTimestamp(4,attendance.getLeaveTime());
            pst.setInt(5,attendance.getWorkHrs());
            pst.setInt(6,attendance.getPayPerHrs());
            pst.setInt(7,attendance.getTotalPay());
            pst.execute();
            success = true;
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null,"SQL Error | Add Attendance",e.getMessage());
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
    
    public static boolean updateArrival(Attendance attendance){
        boolean success = false;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("update attendance set arrivalTime=? where employeeId=? and date=?");
            pst.setTimestamp(1,attendance.getArrivalTime());
            pst.setString(2,attendance.getEmp().getName());
            pst.setDate(3, attendance.getDate());
            pst.executeUpdate();
            success = true;
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null,"SQL Error | update arrival",e.getMessage());
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
    
    public static boolean updateLeave(Attendance attendance){
        boolean success = false;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("update attendance set leaveTime=?,workHrs=?, payPerHour=?,totalPay=? where employeeId=? and date=?");
            pst.setTimestamp(1,attendance.getLeaveTime());
            pst.setInt(2,attendance.getWorkHrs());
            pst.setInt(3,attendance.getPayPerHrs());
            pst.setInt(4,attendance.getTotalPay());
            pst.setString(5,attendance.getEmp().getName());
            pst.setDate(6, attendance.getDate());
            pst.executeUpdate();
            success = true;
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null,"SQL Error | update leave",e.getMessage());
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
    
    public static Attendance getAttendance(Employee emp, Date date){
        Attendance attendance = null;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("select * from attendance where employeeId=? and date=?");
            pst.setString(1,emp.getName());
            pst.setDate(2, date);
            rs = pst.executeQuery();
            if(rs.next()){
                attendance = new Attendance(emp,date);
                Timestamp arrivalTime = rs.getTimestamp(3);
                Timestamp leaveTime = rs.getTimestamp(4);
                int payPerHour = rs.getInt(6);
                attendance.setArrivalTime(arrivalTime);
                attendance.setPayPerHrs(payPerHour);
                attendance.setLeaveTime(leaveTime);
            }
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null,"SQL Error | Get Attendance (EMP,Date)",e.getMessage());
        }finally{
            try{
                if(con!=null){
                    con.close();
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return attendance;
    }
    
    public static ArrayList<Attendance> getAttendanceList(Date date){
        ArrayList<Attendance> attendanceList = new ArrayList<>();
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("select * from attendance where date=?");
            pst.setDate(1, date);
            rs = pst.executeQuery();
            while(rs.next()){
                Employee emp = EmployeeController.getEmployee(rs.getString(1));
                Attendance attendance = new Attendance(emp,date);
                Timestamp arrivalTime = rs.getTimestamp(3);
                Timestamp leaveTime = rs.getTimestamp(4);
                int payPerHour = rs.getInt(6);
                attendance.setArrivalTime(arrivalTime);
                attendance.setPayPerHrs(payPerHour);
                attendance.setLeaveTime(leaveTime);
                attendanceList.add(attendance);
            }
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null,"SQL Error | Get Attendance List (Date)",e.getMessage());
        }finally{
            try{
                if(con!=null){
                    con.close();
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return attendanceList;
    }
    
    public static ArrayList<Attendance> getAttendanceRangeList(Date fromDate,Date toDate){
        ArrayList<Attendance> attendanceList = new ArrayList<>();
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("select * from attendance where date between ? and ?");
            pst.setDate(1, fromDate);
            pst.setDate(2,toDate);
            rs = pst.executeQuery();
            while(rs.next()){
                Employee emp = EmployeeController.getEmployee(rs.getString(1));
                Date date = rs.getDate(2);
                Attendance attendance = new Attendance(emp,date);
                Timestamp arrivalTime = rs.getTimestamp(3);
                Timestamp leaveTime = rs.getTimestamp(4);
                int payPerHour = rs.getInt(6);
                attendance.setArrivalTime(arrivalTime);
                attendance.setPayPerHrs(payPerHour);
                attendance.setLeaveTime(leaveTime);
                attendanceList.add(attendance);
            }
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null,"SQL Error | Get Attendance Range List (Date,Date)",e.getMessage());
        }finally{
            try{
                if(con!=null){
                    con.close();
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return attendanceList;
    }
    
    public static ArrayList<Attendance> getAttendanceRangeList(Employee emp,Date fromDate,Date toDate){
        ArrayList<Attendance> attendanceList = new ArrayList<>();
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("select * from attendance where date between ? and ? and employeeId = ?");
            pst.setDate(1, fromDate);
            pst.setDate(2,toDate);
            pst.setString(3,emp.getName());
            rs = pst.executeQuery();
            while(rs.next()){
                Date date = rs.getDate(2);
                Attendance attendance = new Attendance(emp,date);
                Timestamp arrivalTime = rs.getTimestamp(3);
                Timestamp leaveTime = rs.getTimestamp(4);
                int payPerHour = rs.getInt(6);
                attendance.setArrivalTime(arrivalTime);
                attendance.setPayPerHrs(payPerHour);
                attendance.setLeaveTime(leaveTime);
                attendanceList.add(attendance);
            }
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null,"SQL Error | Get Attendance Range List (EMP,Date)",e.getMessage());
        }finally{
            try{
                if(con!=null){
                    con.close();
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return attendanceList;
    }
    
    public static ArrayList<Attendance> getAttendanceList(Employee emp){
        ArrayList<Attendance> attendanceList = new ArrayList<>();
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("select * from attendance where employeeId=?");
            pst.setString(1,emp.getName());
            rs = pst.executeQuery();
            while(rs.next()){
                Date date = rs.getDate(2);
                Attendance attendance = new Attendance(emp,date);
                Timestamp arrivalTime = rs.getTimestamp(3);
                Timestamp leaveTime = rs.getTimestamp(4);                
                int payPerHour = rs.getInt(6);
                attendance.setArrivalTime(arrivalTime);
                attendance.setPayPerHrs(payPerHour);
                attendance.setLeaveTime(leaveTime);
                attendanceList.add(attendance);
            }
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null,"SQL Error | Get Attendance List (Emp)",e.getMessage());
        }finally{
            try{
                if(con!=null){
                    con.close();
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return attendanceList;
    }
}
