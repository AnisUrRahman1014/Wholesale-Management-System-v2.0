package Model;

import Components.ReportManagement.ReportMenu;
import java.sql.Date;

/**
 *
 * @author Anis Ur Rahman
 */
public class Report {    
    public static final String PRODUCT_REPORT = "Single Product";
    public static final String COMPLETE_REPORT = "All Products";
    private final String reportType;
    private Date reportDate;
    private int reportRange;
    private Date fromDate;
    private Date toDate;
    private double availableQuantity;
    private double avgCostPerUnit;
    private double availableCost;
    private double totalQuantitySold;
    private double avgSalePerUnit;
    private double totalSale;
    private double soldCost;
    private double grossProfit;
    private String productName;

    public Report(String reportType) {
        this.reportType = reportType;
        this.reportDate = new Date(new java.util.Date().getTime());
    }
    
    public void calculateSoldCost(){
        this.soldCost = this.totalQuantitySold*this.avgCostPerUnit;
    }
    
    public void calculateGrossProfit(){
        System.out.println("SOLD: "+totalSale);
        double c=(double)(this.totalSale-this.soldCost);
        this.grossProfit=(double)(c/this.totalSale);
        System.out.println(grossProfit);
    }

    public double getAvailableCost() {
        return availableCost;
    }

    public void setAvailableCost(double availableCost) {
        this.availableCost = availableCost;
    }

    public double getSoldCost() {
        return soldCost;
    }

    public void setSoldCost(double soldCost) {
        this.soldCost = soldCost;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public String getReportType() {
        return reportType;
    }
    
    

    public int getReportRange() {
        return reportRange;
    }

    public void setReportRange(int reportRange) {
        this.reportRange = reportRange;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public double getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(double availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public double getAvgCostPerUnit() {
        return avgCostPerUnit;
    }

    public void setAvgCostPerUnit(double avgCostPerUnit) {
        this.avgCostPerUnit = avgCostPerUnit;
    }

    public double getTotalCost() {
        return availableCost;
    }

    public void setTotalCost(double totalCost) {
        this.availableCost = totalCost;
    }

    public double getTotalQuantitySold() {
        return totalQuantitySold;
    }

    public void setTotalQuantitySold(double totalQuantitySold) {
        this.totalQuantitySold = totalQuantitySold;
    }

    public double getAvgSalePerUnit() {
        return avgSalePerUnit;
    }

    public void setAvgSalePerUnit(double avgSalePerUnit) {
        this.avgSalePerUnit = avgSalePerUnit;
    }

    public double getTotalSale() {
        return totalSale;
    }

    public void setTotalSale(double totalSale) {
        this.totalSale = totalSale;
    }

    public double getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(double grossProfit) {
        this.grossProfit = grossProfit;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
    
}
