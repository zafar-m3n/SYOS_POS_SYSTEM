package test.builder;

import builder.Bill;
import builder.BillBuilder;
import builder.BillItem;
import adapter.BillManagementAdapter;
import adapter.PaymentManagementAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BillBuilderTest {
    private BillBuilder billBuilder;
    private BillManagementAdapter mockBillAdapter;
    private PaymentManagementAdapter mockPaymentAdapter;

    @BeforeEach
    void setUp() {
        mockBillAdapter = mock(BillManagementAdapter.class);
        mockPaymentAdapter = mock(PaymentManagementAdapter.class);
        billBuilder = new BillBuilder(mockBillAdapter, mockPaymentAdapter);
    }

    @Test
    void testAddItem() {
        billBuilder.addItem("ITM001", 2, 3.50);
        billBuilder.addItem("ITM002", 1, 2.00);

        List<BillItem> items = billBuilder.build().getItems();

        assertEquals(2, items.size(), "Bill should contain 2 items");
        assertEquals("ITM001", items.get(0).getItemCode(), "First item should be ITM001");
        assertEquals(2, items.get(0).getQuantity(), "First item quantity should be 2");
        assertEquals(7.00, items.get(0).getTotalPrice(), "First item total price should be 7.00");
    }

    @Test
    void testSetCashTendered() {
        billBuilder.setCashTendered(50.00);
        Bill bill = billBuilder.build();
        assertEquals(50.00, bill.getCashTendered(), "Cash tendered should be set correctly");
    }

    @Test
    void testSetTransactionType() {
        billBuilder.setTransactionType("In-Store");
        Bill bill = billBuilder.build();
        assertEquals("In-Store", bill.getTransactionType(), "Transaction type should be 'In-Store'");
    }

    @Test
    void testBuildBill() {
        when(mockBillAdapter.insertTransaction(anyString(), anyDouble())).thenReturn(1);
        billBuilder.addItem("ITM003", 3, 4.00);
        billBuilder.setCashTendered(20.00);
        billBuilder.setTransactionType("Online");

        Bill bill = billBuilder.build();

        assertNotNull(bill, "Bill should not be null");
        assertEquals(1, bill.getSerialNumber(), "Bill serial number should be 1");
        assertEquals(12.00, bill.getTotalAmount(), "Total amount should be correct");
        assertEquals("Online", bill.getTransactionType(), "Transaction type should be 'Online'");
    }

    @Test
    void testBuildBillFailsIfTransactionFails() {
        when(mockBillAdapter.insertTransaction(anyString(), anyDouble())).thenReturn(-1);
        billBuilder.addItem("ITM004", 2, 5.00);

        Exception exception = assertThrows(RuntimeException.class, billBuilder::build);

        assertEquals("Failed to create transaction.", exception.getMessage(), "Should throw exception if transaction fails");
    }
}
