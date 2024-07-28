/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import Model.Product;

/**
 *
 * @author M AYAN LAPTOP
 */
public class PurchaseTransactionController {
    private static Connection con;
    private static PreparedStatement pst;
    private static ResultSet rs;
    public static boolean uploadTransaction(Product prod){
        // STORE TO PURCHASE TRANSACTION TABLE
        boolean success = false;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("insert into purchaseTransactions values(?,?,?,?,?,?)");
            pst.setTimestamp(1,new Timestamp(new Date().getTime()));
            pst.setString(2, prod.getProdID());
            pst.setDouble(3,prod.getQuantity());
            pst.setDouble(4, prod.getTotalCost());
            pst.setDouble(5,prod.getCostPerUnit());
            pst.setDouble(6,prod.getSalePerUnit());
            pst.execute();
            success=true;
        }catch(SQLException e){
            ManagementSystemCPU.errorAlert(null,"Error uploading purchase transaction", e.getMessage());
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
        return success;
    }
}
