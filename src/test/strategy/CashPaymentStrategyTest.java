package test.strategy;

import adapter.PaymentManagementAdapter;
import strategy.CashPaymentStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class CashPaymentStrategyTest {
    private CashPaymentStrategy cashPaymentStrategy;
    private PaymentManagementAdapter mockPaymentAdapter;

    @BeforeEach
    void setUp() {
        mockPaymentAdapter = mock(PaymentManagementAdapter.class);
        cashPaymentStrategy = new CashPaymentStrategy(mockPaymentAdapter);
    }

    @Test
    void testProcessPayment_Successful() {
        int transactionId = 1;
        double amountDue = 50.0;
        double cashTendered = 100.0;

        cashPaymentStrategy.processPayment(transactionId, amountDue, cashTendered);

        double expectedChange = cashTendered - amountDue;
        verify(mockPaymentAdapter, times(1))
                .insertPayment(transactionId, "Cash", cashTendered, expectedChange);
    }

    @Test
    void testProcessPayment_InsufficientCash() {
        int transactionId = 1;
        double amountDue = 50.0;
        double cashTendered = 30.0;

        cashPaymentStrategy.processPayment(transactionId, amountDue, cashTendered);

        // Ensure insertPayment is NOT called
        verify(mockPaymentAdapter, never()).insertPayment(anyInt(), anyString(), anyDouble(), anyDouble());
    }

    @Test
    void testProcessPayment_ExactCash() {
        int transactionId = 1;
        double amountDue = 50.0;
        double cashTendered = 50.0;

        cashPaymentStrategy.processPayment(transactionId, amountDue, cashTendered);

        verify(mockPaymentAdapter, times(1))
                .insertPayment(transactionId, "Cash", cashTendered, 0.0);
    }
}
