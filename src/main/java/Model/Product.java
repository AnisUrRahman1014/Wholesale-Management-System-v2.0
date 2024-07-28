package Model;

import java.io.File;



/**
 *
 * @author Anis Ur Rahman
 */
public class Product {
    private String prodID;
    private String prodName;
    private double quantity;
    private double totalCost;
    private double salePerUnit;
    private double costPerUnit;
    private String unit;
    private boolean status;

    public Product(String prodID, String prodName, double quantity, double totalCost, double salePerUnit, double costPerUnit,String currentUnit,boolean status) {
        this.prodID = prodID;
        this.prodName = prodName;
        this.quantity = quantity;
        this.totalCost = roundToTwoDecimalPlaces(totalCost);
        this.salePerUnit = roundToTwoDecimalPlaces(salePerUnit);
        this.costPerUnit = roundToTwoDecimalPlaces(costPerUnit);
        this.unit = currentUnit;
        this.status = status;
    }

    public Product(String prodID, String prodName, double quantity, int totalCost, int salePerUnit, String currentUnit) {
        this.prodID = prodID;
        this.prodName = prodName;
        this.quantity = quantity;
        this.totalCost = totalCost;
        this.salePerUnit = salePerUnit;
        this.costPerUnit = calculateCostPerUnit();
        this.unit = currentUnit;
    }
    
    private double roundToTwoDecimalPlaces(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
    
    private double calculateCostPerUnit(){
        double r = this.totalCost / this.quantity;
        return roundToTwoDecimalPlaces(r);
    }

    public String getProdID() {
        return prodID;
    }

    public String getProdName() {
        return prodName;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public double getSalePerUnit() {
        return salePerUnit;
    }

    public double getCostPerUnit() {
        return costPerUnit;
    }

    public String getUnit() {
        return unit;
    }

    public boolean getStatus() {
        return status;
    }
    
    
    
}
