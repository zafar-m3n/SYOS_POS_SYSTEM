package builder;

public class BillItem {
    private String itemCode;
    private int quantity;
    private double totalPrice;

    public BillItem(String itemCode, int quantity, double totalPrice) {
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public String getItemCode() {
        return itemCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
