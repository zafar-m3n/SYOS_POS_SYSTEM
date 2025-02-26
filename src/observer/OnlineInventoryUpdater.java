package observer;

import adapter.StockManagementAdapter;
import builder.BillItem;
import java.util.List;

public class OnlineInventoryUpdater implements StockObserver {
    private StockManagementAdapter stockAdapter;

    public OnlineInventoryUpdater(StockManagementAdapter stockAdapter) {
        this.stockAdapter = stockAdapter;
    }

    @Override
    public void updateStock(List<BillItem> items) {
        for (BillItem item : items) {
            stockAdapter.updateStock(item.getItemCode(), item.getQuantity());
        }
    }
}
