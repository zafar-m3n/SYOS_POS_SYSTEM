package test.observer;

import adapter.StockManagementAdapter;
import builder.BillItem;
import observer.OnlineInventoryUpdater;
import observer.StockObserver;
import observer.StockSubject;
import observer.StockUpdater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class StockObserverTest {
    private StockUpdater stockUpdater;
    private OnlineInventoryUpdater onlineInventoryUpdater;
    private StockManagementAdapter mockStockAdapter;

    @BeforeEach
    void setUp() {
        mockStockAdapter = mock(StockManagementAdapter.class);
        stockUpdater = new StockUpdater(mockStockAdapter);
        onlineInventoryUpdater = new OnlineInventoryUpdater(mockStockAdapter);
    }

    @Test
    void testStockUpdaterUpdatesStock() {
        List<BillItem> items = Arrays.asList(
                new BillItem("ITM001", 2, 10.0),
                new BillItem("ITM002", 1, 5.0)
        );

        stockUpdater.updateStock(items);

        verify(mockStockAdapter, times(1)).updateStock("ITM001", 2);
        verify(mockStockAdapter, times(1)).updateStock("ITM002", 1);
    }

    @Test
    void testOnlineInventoryUpdaterUpdatesStock() {
        List<BillItem> items = Arrays.asList(
                new BillItem("ITM003", 3, 15.0),
                new BillItem("ITM004", 2, 8.0)
        );

        onlineInventoryUpdater.updateStock(items);

        verify(mockStockAdapter, times(1)).updateStock("ITM003", 3);
        verify(mockStockAdapter, times(1)).updateStock("ITM004", 2);
    }

    @Test
    void testStockSubject_RegisterAndNotifyObservers() {
        StockSubject stockSubject = mock(StockSubject.class);
        StockObserver mockObserver = mock(StockObserver.class);
        List<BillItem> items = Arrays.asList(new BillItem("ITM005", 1, 12.0));

        stockSubject.registerObserver(mockObserver);
        stockSubject.notifyObservers(items);

        verify(stockSubject, times(1)).registerObserver(mockObserver);
        verify(stockSubject, times(1)).notifyObservers(items);
    }

    @Test
    void testStockSubject_RemoveObserver() {
        StockSubject stockSubject = mock(StockSubject.class);
        StockObserver mockObserver = mock(StockObserver.class);

        stockSubject.registerObserver(mockObserver);
        stockSubject.removeObserver(mockObserver);

        verify(stockSubject, times(1)).registerObserver(mockObserver);
        verify(stockSubject, times(1)).removeObserver(mockObserver);
    }
}
