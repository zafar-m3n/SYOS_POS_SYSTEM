package adapter;

import java.util.List;
import java.util.Map;

public interface StockManagementAdapter {
    boolean isItemInStock(String itemCode, int quantity);
    void updateStock(String itemCode, int quantity);
    void reshelveItems();
    List<Map<String, Object>> checkReorderLevels();
}
