package observer;

import adapter.StockManagementAdapter;
import builder.BillItem;
import java.util.List;

public class StockUpdater implements StockObserver {
    private StockManagementAdapter stockAdapter;

    public StockUpdater(StockManagementAdapter stockAdapter) {
        this.stockAdapter = stockAdapter;
    }

    @Override
    public void updateStock(List<BillItem> items, String transactionType) {
        if (!transactionType.equals("In-Store")) return;

        for (BillItem item : items) {
            stockAdapter.updateStock(item.getItemCode(), item.getQuantity());
        }
        System.out.println("Stock updated for in-store transaction.");
    }
}
