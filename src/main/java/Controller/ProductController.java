package Controller;

import Model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
public class ProductController {
    static Connection con;
    static PreparedStatement pst;
    static ResultSet rs;
    
    public static boolean storeProductToDB(Product newProd){
        boolean success = false;
        try{
            con = connect.connectDB();
            con.setAutoCommit(false); // Starting transaction
            // STORE TO PRODUCTS TABLE
            pst = con.prepareStatement("insert into products values(?,?,?,?,?,?,?,?)");
            pst.setString(1, newProd.getProdID());
            pst.setString(2, newProd.getProdName());
            pst.setDouble(3,newProd.getQuantity());
            pst.setDouble(4, newProd.getTotalCost());
            pst.setDouble(5,newProd.getCostPerUnit());
            pst.setDouble(6,newProd.getSalePerUnit());
            pst.setString(7,newProd.getUnit());
            pst.setBoolean(8, newProd.getStatus());
            pst.executeUpdate();
            pst.close();
            
            // STORE TO PURCHASE TRANSACTION TABLE
            pst = con.prepareStatement("insert into purchaseTransactions values(?,?,?,?,?,?)");
            pst.setTimestamp(1,new Timestamp(new Date().getTime()));
            pst.setString(2, newProd.getProdID());
            pst.setDouble(3,newProd.getQuantity());
            pst.setDouble(4, newProd.getTotalCost());
            pst.setDouble(5,newProd.getCostPerUnit());
            pst.setDouble(6,newProd.getSalePerUnit());
            pst.executeUpdate();
            
            con.commit();
            success=true;
        }catch(Exception e){
            try {
                if (con != null) {
                    con.rollback(); // Rollback transaction if any exception occurs
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
            ManagementSystemCPU.errorAlert(null, "Failed to upload product", e.getMessage());
            e.printStackTrace();
        }finally{
            try{
                if(con!=null){
                    con.setAutoCommit(true);
                    con.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return success;
    }
    
    public static boolean deleteProduct(String prodName){
        boolean success = false;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("update products set status = ? where productName='"+prodName+"'");
            pst.setBoolean(1,false);
            pst.executeUpdate();
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
    
    public static Product getProduct(String str){
        Product prod = null;
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("select * from products where productName='"+str+"' and status=?");
            pst.setBoolean(1, true);
            rs = pst.executeQuery();
            if(rs.next()){
                String id = rs.getString(1);
                String prodName = rs.getString(2);
                double quantity = rs.getDouble(3);
                double totalCost = rs.getDouble(4);
                double costPerUnit = rs.getDouble(5);
                double salePerUnit = rs.getDouble(6);
                String unit = rs.getString(7);
                boolean status = rs.getBoolean(8);
                prod = new Product(id,prodName,quantity,totalCost,salePerUnit,costPerUnit,unit,status);
                return prod;
            }
            
            pst = con.prepareStatement("select * from products where prodId='"+str+"'");
            rs = pst.executeQuery();
            if(rs.next()){
                String id = rs.getString("prodID");
                String prodName = rs.getString("productName");
                double quantity = rs.getDouble(3);
                double totalCost = rs.getDouble(4);
                double costPerUnit = rs.getDouble(5);
                double salePerUnit = rs.getDouble(6);
                String unit = rs.getString(7);
                boolean status = rs.getBoolean(8);
                prod = new Product(id,prodName,quantity,totalCost,salePerUnit,costPerUnit,unit,status);
                return prod;
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
        return prod;
    }
    
    public static boolean updateProduct(Product prod){
        boolean success = false;
        try{
            con = connect.connectDB();
            // STORE THE TRANSACTION IN THE PURCHASE TRANSACTION TABLE
            if(!PurchaseTransactionController.uploadTransaction(prod)){
                return false;
            }
            // CALCULATE THE AVERAGE FOR THE SALEPERPIECE, COSTPERPIECE AND TOTAL COST
            // UPDATE THE PRODUCT USING THESE AVERAGE VALUES
            pst = con.prepareStatement("update products set quantity=?, totalCost=?, salePerPiece=?, costPerPiece=? where productName=?");
            pst.setDouble(1, prod.getQuantity());
            pst.setDouble(2,prod.getTotalCost());
            pst.setDouble(3,prod.getSalePerUnit());
            pst.setDouble(4,prod.getCostPerUnit());
            pst.setString(5,prod.getProdName());
            pst.executeUpdate();
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
    
    public static ArrayList<Product> getProductList(){
        ArrayList<Product> products = new ArrayList<>();
        try{
            con = connect.connectDB();
            pst = con.prepareStatement("select * from products where status=?");
            pst.setBoolean(1, true);
            rs = pst.executeQuery();
            while (rs.next()){
                String id = rs.getString("prodID");
                String prodName = rs.getString("productName");
                double quantity = rs.getDouble(3);
                double totalCost = rs.getDouble(4);
                double costPerUnit = rs.getDouble(5);
                double salePerUnit = rs.getDouble(6);
                String unit = rs.getString(7);
                boolean status = rs.getBoolean(8);
                Product temp = new Product(id,prodName,quantity,totalCost,salePerUnit,costPerUnit,unit,status);
                products.add(temp);
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
        return products;
    }
}
