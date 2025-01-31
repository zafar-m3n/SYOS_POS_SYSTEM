package template;

import java.util.List;
import java.util.Map;

public abstract class ReportTemplate {

    // Template method defining the report generation process
    public final void generateReport() {
        List<Map<String, Object>> data = fetchData();
        displayReport(formatData(data));
    }

    // Abstract method to be implemented by subclasses to fetch report data
    protected abstract List<Map<String, Object>> fetchData();

    // Format data into tabular structure
    protected String formatData(List<Map<String, Object>> data) {
        if (data.isEmpty()) {
            return "No data available for this report.";
        }

        // Get column headers from first row keys
        StringBuilder table = new StringBuilder();
        Map<String, Object> firstRow = data.get(0);
        for (String key : firstRow.keySet()) {
            table.append(String.format("%-20s", key.toUpperCase()));
        }
        table.append("\n");

        // Print data rows
        for (Map<String, Object> row : data) {
            for (Object value : row.values()) {
                table.append(String.format("%-20s", value));
            }
            table.append("\n");
        }

        return table.toString();
    }

    // Display formatted report
    private void displayReport(String reportContent) {
        System.out.println("\n" + reportContent);
    }
}
