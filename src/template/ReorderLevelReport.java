package template;

import adapter.ReportManagementSQLAdapter;
import java.util.List;
import java.util.Map;

public class ReorderLevelReport extends ReportTemplate {
    private ReportManagementSQLAdapter reportAdapter;

    public ReorderLevelReport(ReportManagementSQLAdapter reportAdapter) {
        this.reportAdapter = reportAdapter;
    }

    @Override
    protected List<Map<String, Object>> fetchData() {
        return reportAdapter.generateReorderLevelReport();
    }
}
