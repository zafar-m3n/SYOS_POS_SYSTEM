package adapter;

import java.util.List;
import java.util.Map;

public interface BillManagementAdapter {
    int insertTransaction(String transactionType, double totalAmount);
    void insertTransactionItem(int transactionId, String itemCode, int quantity, double totalPrice);
    List<Map<String, Object>> getTransactionDetails(int transactionId);
}
