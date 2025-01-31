package observer;

import builder.BillItem;
import java.util.List;

public interface StockSubject {
    void registerObserver(StockObserver observer);
    void removeObserver(StockObserver observer);
    void notifyObservers(List<BillItem> items, String transactionType);
}
