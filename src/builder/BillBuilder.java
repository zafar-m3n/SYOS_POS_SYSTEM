package builder;

import adapter.BillManagementAdapter;
import adapter.PaymentManagementAdapter;
import strategy.CashPaymentStrategy;
import strategy.PaymentStrategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BillBuilder {
    private List<BillItem> items = new ArrayList<>();
    private double totalAmount = 0.0;
    private double cashTendered;
    private double changeAmount;
    private String transactionType;
    private BillManagementAdapter billAdapter;
    private PaymentManagementAdapter paymentAdapter;
    private int serialNumber;
    private Date billDate;

    public BillBuilder(BillManagementAdapter billAdapter, PaymentManagementAdapter paymentAdapter) {
        this.billAdapter = billAdapter;
        this.paymentAdapter = paymentAdapter;
        this.billDate = new Date();  // Set bill date to current timestamp
    }

    public BillBuilder addItem(String itemCode, int quantity, double pricePerUnit) {
        double totalPrice = quantity * pricePerUnit;
        items.add(new BillItem(itemCode, quantity, totalPrice));
        totalAmount += totalPrice;
        return this;
    }

    public BillBuilder setCashTendered(double cashTendered) {
        this.cashTendered = cashTendered;
        return this;
    }

    public BillBuilder setTransactionType(String transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    public Bill build() {
        // Insert into transactions table
        serialNumber = billAdapter.insertTransaction(transactionType, totalAmount);
        if (serialNumber == -1) {
            throw new RuntimeException("Failed to create transaction.");
        }

        // Insert each item into transaction_items table
        for (BillItem item : items) {
            billAdapter.insertTransactionItem(serialNumber, item.getItemCode(), item.getQuantity(), item.getTotalPrice());
        }

        // Return the finalized bill
        return new Bill(serialNumber, billDate, items, totalAmount, cashTendered, changeAmount, transactionType);
    }
}
