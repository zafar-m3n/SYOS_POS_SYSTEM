package facade;

import adapter.BillManagementSQLAdapter;
import adapter.PaymentManagementSQLAdapter;
import adapter.ReportManagementSQLAdapter;
import adapter.StockManagementSQLAdapter;
import builder.BillBuilder;
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
}
