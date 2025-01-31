package strategy;

import adapter.PaymentManagementAdapter;

public class CashPaymentStrategy implements PaymentStrategy {
    private final PaymentManagementAdapter paymentAdapter;

    public CashPaymentStrategy(PaymentManagementAdapter paymentAdapter) {
        this.paymentAdapter = paymentAdapter;
    }

    @Override
    public void processPayment(int transactionId, double amountDue, double cashTendered) {
        if (cashTendered < amountDue) {
            System.out.println("Insufficient cash provided!");
            return;
        }

        double changeAmount = cashTendered - amountDue;
        System.out.println("Payment successful! Change to return: " + changeAmount);

        paymentAdapter.insertPayment(transactionId, "Cash", cashTendered, changeAmount);
    }
}
