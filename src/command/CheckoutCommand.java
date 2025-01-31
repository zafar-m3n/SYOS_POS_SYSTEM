package command;

import builder.Bill;
import builder.BillBuilder;
import observer.StockObserver;
import observer.StockSubject;
import strategy.PaymentStrategy;

import java.util.ArrayList;
import java.util.List;

public class CheckoutCommand implements Command, StockSubject {
    private BillBuilder billBuilder;
    private PaymentStrategy paymentStrategy;
    private List<StockObserver> observers = new ArrayList<>();

    public CheckoutCommand(BillBuilder billBuilder, PaymentStrategy paymentStrategy) {
        this.billBuilder = billBuilder;
        this.paymentStrategy = paymentStrategy;
    }

    @Override
    public void execute() {
        System.out.println("Finalizing Bill...");
        // Step 1: Build the Bill (Finalizes and stores it in the DB)
        Bill bill = billBuilder.build();
        System.out.println("Bill finalized with Serial Number: " + bill.getSerialNumber());

        // Step 2: Process Payment
        paymentStrategy.processPayment(bill.getSerialNumber(), bill.getTotalAmount(), bill.getCashTendered());
        System.out.println("Thank you!");

        // Step 3: Notify Observers (Update Stock)
        notifyObservers(bill.getItems());
    }

    // Implement StockSubject methods
    @Override
    public void registerObserver(StockObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(StockObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(List<builder.BillItem> items) {
        for (StockObserver observer : observers) {
            observer.updateStock(items);
        }
    }
}
