package Model;
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

    public void setProdID(String prodID) {
        this.prodID = prodID;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public void setSalePerUnit(double salePerUnit) {
        this.salePerUnit = salePerUnit;
    }

    public void setCostPerUnit(double costPerUnit) {
        this.costPerUnit = costPerUnit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }    
}
