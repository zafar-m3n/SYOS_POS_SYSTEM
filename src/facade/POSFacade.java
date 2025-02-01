package facade;

import adapter.BillManagementSQLAdapter;
import adapter.PaymentManagementSQLAdapter;
import adapter.ReportManagementSQLAdapter;
import adapter.StockManagementSQLAdapter;
import builder.*;
import command.CheckoutCommand;
import observer.StockObserver;
import observer.StockUpdater;
import observer.OnlineInventoryUpdater;
import strategy.CashPaymentStrategy;
import strategy.PaymentStrategy;
import template.*;
import java.util.Scanner;
import java.util.Map;

public class POSFacade {
    private final Scanner scanner;
    private final BillManagementSQLAdapter billAdapter;
    private final PaymentManagementSQLAdapter paymentAdapter;
    private final StockManagementSQLAdapter stockAdapter;
    private final ReportManagementSQLAdapter reportAdapter;
    private CheckoutCommand checkoutCommand;
    private String transactionType;

    public POSFacade() {
        this.scanner = new Scanner(System.in);
        this.billAdapter = new BillManagementSQLAdapter();
        this.paymentAdapter = new PaymentManagementSQLAdapter();
        this.stockAdapter = new StockManagementSQLAdapter();
        this.reportAdapter = new ReportManagementSQLAdapter();
    }

    public void startPOS() {
        while (true) {
            System.out.println("\nSYOS POS System");
            System.out.println("1 - Checkout");
            System.out.println("2 - Generate Report");
            System.out.println("0 - Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    handleCheckout();
                    break;
                case 2:
                    generateReport();
                    break;
                case 0:
                    System.out.println("Exiting SYOS POS System.");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void handleCheckout() {
        BillBuilder billBuilder = new BillBuilder(billAdapter, paymentAdapter);
        System.out.println("\n--- Checkout ---");

        while (true) {
            System.out.print("Enter transaction type (Online or In-Store): ");
            transactionType = scanner.nextLine().trim();
            if (transactionType.equalsIgnoreCase("Online") || transactionType.equalsIgnoreCase("In-Store")) {
                billBuilder.setTransactionType(transactionType);
                break;
            } else {
                System.out.println("Invalid transaction type. Please enter 'Online' or 'In-Store'.");
            }
        }

        double totalBillAmount = 0.0;

        while (true) {
            System.out.print("Enter item code (or 'done' to finish): ");
            String itemCode = scanner.nextLine();

            if (itemCode.equalsIgnoreCase("done")) {
                break;
            }

            Map<String, Object> item = stockAdapter.getItemByCode(itemCode);
            if (item == null) {
                System.out.println("Invalid item code. Please enter a valid item code.");
                continue;
            }

            String itemName = (String) item.get("name");
            double itemPrice = (double) item.get("price");

            System.out.print("Enter quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (stockAdapter.isItemInStock(itemCode, quantity)) {
                double price = itemPrice * quantity;
                billBuilder.addItem(itemCode, quantity, itemPrice);
                totalBillAmount += price;
                System.out.println("Added: " + itemName + " | Quantity: " + quantity + " | Subtotal: $" + price);
            } else {
                System.out.println("Item not available in stock.");
            }
        }


        // Show total bill amount before proceeding with payment
        System.out.println("\nTotal Bill Amount: $" + totalBillAmount);

        // Ask for payment method selection
        PaymentStrategy paymentStrategy;
        while (true) {
            System.out.println("\nSelect Payment Method:");
            System.out.println("1 - Cash");
            System.out.print("Enter payment method: ");
            int paymentChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (paymentChoice == 1) {
                double cashTendered;
                System.out.print("Enter amount tendered: ");
                cashTendered = scanner.nextDouble();
                scanner.nextLine();

                billBuilder.setCashTendered(cashTendered);
                paymentStrategy = new CashPaymentStrategy(paymentAdapter);
                break;
            } else {
                System.out.println("This payment method is not implemented yet. Please select 1 for Cash.");
            }
        }

        checkoutCommand = new CheckoutCommand(billBuilder, paymentStrategy);
        registerObservers();
        checkoutCommand.execute();
        printBill(billBuilder.build());
    }

    private void registerObservers() {
        if (transactionType.equalsIgnoreCase("In-Store")) {
            checkoutCommand.registerObserver(new StockUpdater(stockAdapter));
        } else {
            checkoutCommand.registerObserver(new OnlineInventoryUpdater(stockAdapter));
        }
    }

    private void generateReport() {
        System.out.println("\n--- Generate Report ---");
        System.out.println("1 - Total Sales Report");
        System.out.println("2 - Reshelving Report");
        System.out.println("3 - Reorder Level Report");
        System.out.println("4 - Stock Report");
        System.out.println("5 - Bill Report");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        ReportTemplate report = null;

        switch (choice) {
            case 1:
                report = new TotalSalesReport(reportAdapter);
                break;
            case 2:
                report = new ReshelvingReport(reportAdapter);
                break;
            case 3:
                report = new ReorderLevelReport(reportAdapter);
                break;
            case 4:
                report = new StockReport(reportAdapter);
                break;
            case 5:
                report = new BillReport(reportAdapter);
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        report.generateReport();
    }

    private void printBill(Bill bill) {
        System.out.println("\n=================================");
        System.out.println("         SYOS POS SYSTEM         ");
        System.out.println("=================================");
        System.out.printf("Bill Serial Number: %d%n", bill.getSerialNumber());
        System.out.printf("Date: %s%n", bill.getBillDate());
        System.out.printf("Transaction Type: %s%n", bill.getTransactionType());
        System.out.println("---------------------------------");
        System.out.printf("%-20s %-10s %-10s%n", "Item Name", "Quantity", "Price");
        System.out.println("---------------------------------");

        for (BillItem item : bill.getItems()) {
            Map<String, Object> itemData = stockAdapter.getItemByCode(item.getItemCode());
            String itemName = (String) itemData.get("name"); // Get the item name
            System.out.printf("%-20s %-10d $%-10.2f%n", itemName, item.getQuantity(), item.getTotalPrice());
        }

        System.out.println("---------------------------------");
        System.out.printf("Total Amount: $%.2f%n", bill.getTotalAmount());
        System.out.printf("Cash Tendered: $%.2f%n", bill.getCashTendered());
        System.out.printf("Change: $%.2f%n", bill.getChangeAmount());
        System.out.println("=================================");
        System.out.println("  Thank You for Shopping at SYOS ");
        System.out.println("=================================\n");
    }

}
