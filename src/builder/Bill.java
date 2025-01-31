package builder;

import java.util.Date;
import java.util.List;

public class Bill {
    private int serialNumber;
    private Date billDate;
    private List<BillItem> items;
    private double totalAmount;
    private double cashTendered;
    private double changeAmount;
    private String transactionType;

    public Bill(int serialNumber, Date billDate, List<BillItem> items, double totalAmount,
                double cashTendered, double changeAmount, String transactionType) {
        this.serialNumber = serialNumber;
        this.billDate = billDate;
        this.items = items;
        this.totalAmount = totalAmount;
        this.cashTendered = cashTendered;
        this.changeAmount = changeAmount;
        this.transactionType = transactionType;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public Date getBillDate() {
        return billDate;
    }

    public List<BillItem> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getCashTendered() {
        return cashTendered;
    }

    public double getChangeAmount() {
        return changeAmount;
    }

    public String getTransactionType() {
        return transactionType;
    }
}
