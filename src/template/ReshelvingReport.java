package template;

import adapter.ReportManagementSQLAdapter;
import java.util.List;
import java.util.Map;

public class ReshelvingReport extends ReportTemplate {
    private ReportManagementSQLAdapter reportAdapter;

    public ReshelvingReport(ReportManagementSQLAdapter reportAdapter) {
        this.reportAdapter = reportAdapter;
    }

    @Override
    protected List<Map<String, Object>> fetchData() {
        return reportAdapter.generateReshelvingReport();
    }
}
