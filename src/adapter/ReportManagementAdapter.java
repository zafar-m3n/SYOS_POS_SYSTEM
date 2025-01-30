package adapter;

import java.util.List;
import java.util.Map;

public interface ReportManagementAdapter {
    List<Map<String, Object>> generateDailySalesReport(String date);
    List<Map<String, Object>> generateReshelvingReport();
    List<Map<String, Object>> generateReorderLevelReport();
    List<Map<String, Object>> generateStockReport();
    List<Map<String, Object>> generateBillReport(int transactionId);
}
