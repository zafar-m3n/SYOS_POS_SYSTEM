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
    public void updateStock(List<BillItem> items, String transactionType) {
        if (!transactionType.equals("Online")) return;

        for (BillItem item : items) {
            stockAdapter.updateStock(item.getItemCode(), item.getQuantity());
        }
        System.out.println("Online inventory updated for online transaction.");
    }
}
