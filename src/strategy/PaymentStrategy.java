package strategy;

public interface PaymentStrategy {
    void processPayment(int transactionId, double amountDue, double cashTendered);
}
