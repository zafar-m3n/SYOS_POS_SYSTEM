package template;

import adapter.ReportManagementSQLAdapter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BillReport extends ReportTemplate {
    private ReportManagementSQLAdapter reportAdapter;

    public BillReport(ReportManagementSQLAdapter reportAdapter) {
        this.reportAdapter = reportAdapter;
    }

    @Override
    protected List<Map<String, Object>> fetchData() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Transaction ID for bill report: ");
        int transactionId = scanner.nextInt();
        return reportAdapter.generateBillReport(transactionId);
    }
}
