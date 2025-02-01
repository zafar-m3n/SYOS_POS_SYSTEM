package test.command;

import builder.Bill;
import builder.BillBuilder;
import builder.BillItem;
import command.CheckoutCommand;
import observer.StockObserver;
import strategy.PaymentStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CheckoutCommandTest {
    private CheckoutCommand checkoutCommand;
    private BillBuilder mockBillBuilder;
    private PaymentStrategy mockPaymentStrategy;
    private StockObserver mockObserver;
    private Bill mockBill;

    @BeforeEach
    void setUp() {
        mockBillBuilder = mock(BillBuilder.class);
        mockPaymentStrategy = mock(PaymentStrategy.class);
        mockObserver = mock(StockObserver.class);
        mockBill = mock(Bill.class);

        when(mockBillBuilder.build()).thenReturn(mockBill);
        when(mockBill.getSerialNumber()).thenReturn(1);
        when(mockBill.getTotalAmount()).thenReturn(50.0);
        when(mockBill.getCashTendered()).thenReturn(100.0);
        when(mockBill.getItems()).thenReturn(Arrays.asList(new BillItem("ITM001", 2, 10.0)));

        checkoutCommand = new CheckoutCommand(mockBillBuilder, mockPaymentStrategy);
    }

    @Test
    void testExecute_CreatesBillAndProcessesPayment() {
        checkoutCommand.execute();

        // Verify that the bill was built
        verify(mockBillBuilder, times(1)).build();

        // Verify that payment was processed with the correct values
        verify(mockPaymentStrategy, times(1))
                .processPayment(1, 50.0, 100.0);
    }

    @Test
    void testExecute_NotifiesObservers() {
        checkoutCommand.registerObserver(mockObserver);

        checkoutCommand.execute();

        ArgumentCaptor<List<BillItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockObserver, times(1)).updateStock(captor.capture());

        List<BillItem> capturedItems = captor.getValue();
        assertEquals(1, capturedItems.size());
        assertEquals("ITM001", capturedItems.get(0).getItemCode());
    }

    @Test
    void testRegisterObserver() {
        StockObserver observer = mock(StockObserver.class);
        checkoutCommand.registerObserver(observer);

        checkoutCommand.execute();

        verify(observer, times(1)).updateStock(anyList());
    }

    @Test
    void testRemoveObserver() {
        StockObserver observer = mock(StockObserver.class);
        checkoutCommand.registerObserver(observer);
        checkoutCommand.removeObserver(observer);

        checkoutCommand.execute();

        verify(observer, times(0)).updateStock(anyList());
    }
}
