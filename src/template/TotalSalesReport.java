package template;

import adapter.ReportManagementSQLAdapter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TotalSalesReport extends ReportTemplate {
    private ReportManagementSQLAdapter reportAdapter;

    public TotalSalesReport(ReportManagementSQLAdapter reportAdapter) {
        this.reportAdapter = reportAdapter;
    }

    @Override
    protected List<Map<String, Object>> fetchData() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter date for sales report (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        return reportAdapter.generateDailySalesReport(date);
    }
}
