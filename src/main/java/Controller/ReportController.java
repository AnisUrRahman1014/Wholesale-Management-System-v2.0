package Controller;

import Components.ReportManagement.ReportMenu;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import Model.Report;
import Model.Product;
import static Model.Report.COMPLETE_REPORT;
import static Model.Report.PRODUCT_REPORT;
import java.sql.Date;
import java.util.ArrayList;

/**
 *
 * @author Anis Ur Rahman
 */
public class ReportController {
    static Connection con;
    static PreparedStatement pst;
    static ResultSet rs;
    static Report summaryReport;
    public static Report getReportSummary(){
        return summaryReport;
    }
    
    public static ArrayList<Report> generateReport(Report report) throws Exception{
        ArrayList<Report> reportList = new ArrayList<>();
        con = connect.connectDB();
        double totalQuantitySold;
        double avgSalePerUnit;
        double totalSale;
        switch(report.getReportType()){
            case COMPLETE_REPORT->{
                switch(report.getReportRange()){
                    case ReportMenu.TODAY->{
                        // GET CURRENT INVENTORY DETAIL
                        int totalProducts = 0;
                        // CREATING AN ARRAYLIST AND STORING THE PRODUCTS SOLD FOR THE CURRENT DATE SO WE CAN GENERATE REPORT FOR EACH PRODUCT
                        ArrayList<Product> billProducts = new ArrayList<>();
                        pst = con.prepareStatement("select prodId from BillItems INNER JOIN bills ON billItems.billId = bills.billId where DATE(bills.date) = ?");
                        pst.setDate(1,report.getReportDate());
                        rs = pst.executeQuery();
                        while(rs.next()){
                            String prodId = rs.getString(1);
                            Product prod = ProductController.getProduct(prodId);
                            billProducts.add(prod);
                        }
                        // ITERATING THROUGH THE PRODUCTS AND SEPERATELY GENERATING A REPORT FOR EACH PRODUCT SOLD TODAY
                        for(Product prod: billProducts){
                            Report tempReport = new Report(PRODUCT_REPORT);
                            
                            tempReport.setAvailableQuantity(prod.getQuantity());
                            tempReport.setAvailableCost(prod.getTotalCost());
                            tempReport.setAvgCostPerUnit(prod.getCostPerUnit());

                            // Calculate total sale of this product for today
                            pst = con.prepareStatement("SELECT SUM(quantity), AVG(ratePerUnit), SUM(total) " +
                                    "FROM billItems " +
                                    "INNER JOIN bills ON billItems.billId = bills.billId " +
                                    "WHERE billItems.productId = ? AND DATE(bills.date) = ?");
                            pst.setString(1,prod.getProdID());
                            pst.setDate(2,tempReport.getReportDate());
                            rs = pst.executeQuery();
                            if (rs.next()) {
                                totalQuantitySold = rs.getDouble(1);
                                avgSalePerUnit = rs.getDouble(2);
                                totalSale = rs.getDouble(3);
                                tempReport.setTotalQuantitySold(totalQuantitySold);
                                tempReport.setAvgSalePerUnit(avgSalePerUnit);
                                tempReport.setTotalSale(totalSale);
                            }
                            tempReport.calculateSoldCost();
                            tempReport.calculateGrossProfit();
                            reportList.add(tempReport);
                        }
                    }
                    case ReportMenu.LAST_WEEK->{
                        
                    }
                    case ReportMenu.LAST_MONTH->{
                        
                    }
                    case ReportMenu.RANGE->{
                        
                    }
                    case ReportMenu.NO_RANGE_SELECTED->{
                        throw new Exception("No range selected");
                    }
                }
            }
            case PRODUCT_REPORT->{
                switch(report.getReportRange()){
                    case ReportMenu.TODAY->{
                        // GET CURRENT INVENTORY DETAIL
                        Product prod = ProductController.getProduct(report.getProductName());
                        report.setAvailableQuantity(prod.getQuantity());
                        report.setAvailableCost(prod.getTotalCost());
                        report.setAvgCostPerUnit(prod.getCostPerUnit());
                        
                        // Calculate total sale of this product for today
                        pst = con.prepareStatement("SELECT SUM(quantity), AVG(ratePerUnit), SUM(total) " +
                                "FROM billItems " +
                                "INNER JOIN bills ON billItems.billId = bills.billId " +
                                "WHERE billItems.productId = ? AND DATE(bills.date) = ?");
                        pst.setString(1,prod.getProdID());
                        pst.setDate(2,report.getReportDate());
                        rs = pst.executeQuery();
                        if (rs.next()) {
                            totalQuantitySold = rs.getDouble(1);
                            avgSalePerUnit = rs.getDouble(2);
                            totalSale = rs.getDouble(3);
                            report.setTotalQuantitySold(totalQuantitySold);
                            report.setAvgSalePerUnit(avgSalePerUnit);
                            report.setTotalSale(totalSale);
                        }
                        report.calculateSoldCost();
                        report.calculateGrossProfit();
                        reportList.add(report);
                        summaryReport = report;
                    }
                    case ReportMenu.LAST_WEEK->{
                        // GET CURRENT INVENTORY DETAIL
                        Product prod = ProductController.getProduct(report.getProductName());
                        report.setAvailableQuantity(prod.getQuantity());
                        report.setAvailableCost(prod.getTotalCost());
                        report.setAvgCostPerUnit(prod.getCostPerUnit());
                        
                        // Calculate total sale of this product for last 7 days
                        pst = con.prepareStatement("SELECT SUM(quantity), AVG(ratePerUnit), SUM(total) " +
                            "FROM billItems " +
                            "INNER JOIN bills ON billItems.billId = bills.billId " +
                            "WHERE billItems.productId = ? " +
                            "AND CAST(bills.date AS DATE) >= CAST({fn TIMESTAMPADD(SQL_TSI_DAY, -7, CURRENT_TIMESTAMP)} AS DATE) " +
                            "AND CAST(bills.date AS DATE) < CAST({fn TIMESTAMPADD(SQL_TSI_DAY, 1, CURRENT_TIMESTAMP)} AS DATE)");
                        pst.setString(1,prod.getProdID());
                        rs = pst.executeQuery();
                        if(rs.next()) {
                            totalQuantitySold = rs.getDouble(1);
                            avgSalePerUnit = rs.getDouble(2);
                            totalSale = rs.getDouble(3);
                            report.setTotalQuantitySold(totalQuantitySold);
                            report.setAvgSalePerUnit(avgSalePerUnit);
                            report.setTotalSale(totalSale);
                        }
                        report.calculateSoldCost();
                        report.calculateGrossProfit();
                        reportList.add(report);
                        summaryReport = report;
                    }
                    case ReportMenu.LAST_MONTH->{
                        // GET CURRENT INVENTORY DETAIL
                        Product prod = ProductController.getProduct(report.getProductName());
                        report.setAvailableQuantity(prod.getQuantity());
                        report.setAvailableCost(prod.getTotalCost());
                        report.setAvgCostPerUnit(prod.getCostPerUnit());
                        
                        // Calculate total sale of this product for last 30 days
                        pst = con.prepareStatement("SELECT SUM(quantity), AVG(ratePerUnit), SUM(total) " +
                            "FROM billItems " +
                            "INNER JOIN bills ON billItems.billId = bills.billId " +
                            "WHERE billItems.productId = ? " +
                            "AND CAST(bills.date AS DATE) >= CAST({fn TIMESTAMPADD(SQL_TSI_MONTH, -1, CURRENT_TIMESTAMP)} AS DATE) " +
                            "AND CAST(bills.date AS DATE) < CAST({fn TIMESTAMPADD(SQL_TSI_MONTH, 1, CURRENT_TIMESTAMP)} AS DATE)");
                        pst.setString(1,prod.getProdID());
                        rs = pst.executeQuery();
                        if(rs.next()) {
                            totalQuantitySold = rs.getDouble(1);
                            avgSalePerUnit = rs.getDouble(2);
                            totalSale = rs.getDouble(3);
                            report.setTotalQuantitySold(totalQuantitySold);
                            report.setAvgSalePerUnit(avgSalePerUnit);
                            report.setTotalSale(totalSale);
                        }
                        report.calculateSoldCost();
                        report.calculateGrossProfit();
                        reportList.add(report);
                        summaryReport = report;
                    }
                    case ReportMenu.RANGE->{
                        // GET CURRENT INVENTORY DETAIL
                        Product prod = ProductController.getProduct(report.getProductName());
                        report.setAvailableQuantity(prod.getQuantity());
                        report.setAvailableCost(prod.getTotalCost());
                        report.setAvgCostPerUnit(prod.getCostPerUnit());
                        
                        // Calculate total sale of this product for last 30 days
                        pst = con.prepareStatement("SELECT SUM(quantity), AVG(ratePerUnit), SUM(total) " +
                            "FROM billItems " +
                            "INNER JOIN bills ON billItems.billId = bills.billId " +
                            "WHERE billItems.productId = ? " +
                            "AND CAST(bills.date AS DATE) >= ? " +
                            "AND CAST(bills.date AS DATE) < ?");
                        pst.setString(1, prod.getProdID());
                        pst.setDate(2, report.getFromDate());
                        pst.setDate(3, report.getToDate());
                        rs = pst.executeQuery();
                        if(rs.next()) {
                            totalQuantitySold = rs.getDouble(1);
                            avgSalePerUnit = rs.getDouble(2);
                            totalSale = rs.getDouble(3);
                            report.setTotalQuantitySold(totalQuantitySold);
                            report.setAvgSalePerUnit(avgSalePerUnit);
                            report.setTotalSale(totalSale);
                        }
                        report.calculateSoldCost();
                        report.calculateGrossProfit();
                        reportList.add(report);
                        summaryReport = report;
                    }
                    case ReportMenu.NO_RANGE_SELECTED->{
                        throw new Exception("No range selected");
                    }
                }
            }
        }
        return reportList;
    }
}
