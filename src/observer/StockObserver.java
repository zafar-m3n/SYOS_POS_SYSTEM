package observer;

import builder.BillItem;
import java.util.List;

public interface StockObserver {
    void updateStock(List<BillItem> items);
}
