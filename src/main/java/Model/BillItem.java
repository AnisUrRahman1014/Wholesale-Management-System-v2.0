package Model;
public class BillItem {
    private final Product product;
    private double quantity;
    private double ratePerUnit;
    private String unitType;
    private int total;
    private int discount = 0;

    public BillItem(Product product, double quantity, double ratePerUnit,int discount, String unitType) {
        this.product = product;
        this.quantity = quantity;
        this.ratePerUnit = ratePerUnit;
        this.unitType = unitType;
        this.discount = discount;
        this.total = (int)(quantity * ratePerUnit - this.discount);
    }
    
    public BillItem(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getRatePerUnit() {
        return ratePerUnit;
    }

    public String getUnitType() {
        return unitType;
    }

    public int getTotal() {
        return total;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setRatePerUnit(double ratePerUnit) {
        this.ratePerUnit = ratePerUnit;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public void setTotal(int total) {
        this.total = total;
    }    

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }
    
    
}
