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
                        // CREATING AN ARRAYLIST AND STORING THE PRODUCTS
                        ArrayList<Product> products = ProductController.getProductList();

                        // ITERATING THROUGH THE PRODUCTS AND SEPERATELY GENERATING A REPORT FOR EACH PRODUCT SOLD TODAY
                        for(Product prod: products){
                            Report tempReport = new Report(PRODUCT_REPORT);
                            tempReport.setProductName(prod.getProdName());
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
                        
                        //CREATE SUMMARY REPORT
                        createSummaryReport(reportList);
                    }
                    case ReportMenu.LAST_WEEK->{
                        // CREATING AN ARRAYLIST AND STORING THE PRODUCTS
                        ArrayList<Product> products = ProductController.getProductList();

                        // ITERATING THROUGH THE PRODUCTS AND SEPERATELY GENERATING A REPORT FOR EACH PRODUCT SOLD TODAY
                        for(Product prod: products){
                            Report tempReport = new Report(PRODUCT_REPORT);
                            tempReport.setProductName(prod.getProdName());
                            tempReport.setAvailableQuantity(prod.getQuantity());
                            tempReport.setAvailableCost(prod.getTotalCost());
                            tempReport.setAvgCostPerUnit(prod.getCostPerUnit());

                            // Calculate total sale of this product for last 7 days
                            pst = con.prepareStatement("SELECT SUM(quantity), AVG(ratePerUnit), SUM(total) " +
                                "FROM billItems " +
                                "INNER JOIN bills ON billItems.billId = bills.billId " +
                                "WHERE billItems.productId = ? " +
                                "AND CAST(bills.date AS DATE) >= CAST({fn TIMESTAMPADD(SQL_TSI_DAY, -7, CURRENT_TIMESTAMP)} AS DATE) " +
                                "AND CAST(bills.date AS DATE) < CAST({fn TIMESTAMPADD(SQL_TSI_DAY, 1, CURRENT_TIMESTAMP)} AS DATE)");
                            pst.setString(1,prod.getProdID());
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
                        
                        //CREATE SUMMARY REPORT
                        createSummaryReport(reportList);
                    }
                    case ReportMenu.LAST_MONTH->{
                        // CREATING AN ARRAYLIST AND STORING THE PRODUCTS
                        ArrayList<Product> products = ProductController.getProductList();

                        // ITERATING THROUGH THE PRODUCTS AND SEPERATELY GENERATING A REPORT FOR EACH PRODUCT SOLD TODAY
                        for(Product prod: products){
                            Report tempReport = new Report(PRODUCT_REPORT);
                            tempReport.setProductName(prod.getProdName());
                            tempReport.setAvailableQuantity(prod.getQuantity());
                            tempReport.setAvailableCost(prod.getTotalCost());
                            tempReport.setAvgCostPerUnit(prod.getCostPerUnit());

                            // Calculate total sale of this product for last 30 days
                            pst = con.prepareStatement("SELECT SUM(quantity), AVG(ratePerUnit), SUM(total) " +
                                "FROM billItems " +
                                "INNER JOIN bills ON billItems.billId = bills.billId " +
                                "WHERE billItems.productId = ? " +
                                "AND CAST(bills.date AS DATE) >= CAST({fn TIMESTAMPADD(SQL_TSI_MONTH, -1, CURRENT_TIMESTAMP)} AS DATE) " +
                                "AND CAST(bills.date AS DATE) < CAST({fn TIMESTAMPADD(SQL_TSI_MONTH, 1, CURRENT_TIMESTAMP)} AS DATE)");
                            pst.setString(1,prod.getProdID());
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
                        
                        //CREATE SUMMARY REPORT
                        createSummaryReport(reportList);
                    }
                    case ReportMenu.RANGE->{
                        // CREATING AN ARRAYLIST AND STORING THE PRODUCTS
                        ArrayList<Product> products = ProductController.getProductList();

                        // ITERATING THROUGH THE PRODUCTS AND SEPERATELY GENERATING A REPORT FOR EACH PRODUCT SOLD TODAY
                        for(Product prod: products){
                            Report tempReport = new Report(PRODUCT_REPORT);
                            tempReport.setProductName(prod.getProdName());
                            tempReport.setAvailableQuantity(prod.getQuantity());
                            tempReport.setAvailableCost(prod.getTotalCost());
                            tempReport.setAvgCostPerUnit(prod.getCostPerUnit());

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
                        
                        //CREATE SUMMARY REPORT
                        createSummaryReport(reportList);
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
    
    private static void createSummaryReport(ArrayList<Report> reportList){
        if(reportList != null && !reportList.isEmpty()){
            double totalAvailableQuantity = 0;
            double totalAvailableCost = 0;
//            double totalQuantitySold = 0;
            double totalSoldCost = 0;
            double totalSale = 0;
            int productCountWithSales = 0;
            
            for(Report report : reportList){
                totalAvailableQuantity += report.getAvailableQuantity();
                totalAvailableCost += report.getAvailableCost();
                
                if(report.getTotalQuantitySold() > 0){
                    productCountWithSales++;
//                    totalQuantitySold += report.getTotalQuantitySold();
                    totalSoldCost += report.getSoldCost();
                    totalSale += report.getTotalSale();
                }
            }
            
            summaryReport = new Report(COMPLETE_REPORT);
            summaryReport.setAvailableQuantity(totalAvailableQuantity);
            summaryReport.setAvailableCost(totalAvailableCost);
            summaryReport.setAvgCostPerUnit(Double.NaN); // Set to NaN
            
            summaryReport.setTotalQuantitySold(productCountWithSales); // Count of products with sales
            summaryReport.setSoldCost(totalSoldCost);
            summaryReport.setTotalSale(totalSale);
            summaryReport.setAvgSalePerUnit(Double.NaN); // Set to NaN
            
            summaryReport.calculateGrossProfit();
        }
    }
}
