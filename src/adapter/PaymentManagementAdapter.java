package adapter;

public interface PaymentManagementAdapter {
    void insertPayment(int transactionId, String paymentMethod, double cashTendered, double changeAmount);
}
