package test.template;

import adapter.ReportManagementSQLAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import template.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportTemplateTest {
    private ReportManagementSQLAdapter mockReportAdapter;
    private BillReport billReport;
    private ReorderLevelReport reorderLevelReport;
    private ReshelvingReport reshelvingReport;
    private StockReport stockReport;
    private TotalSalesReport totalSalesReport;

    @BeforeEach
    void setUp() {
        mockReportAdapter = mock(ReportManagementSQLAdapter.class);
        billReport = new BillReport(mockReportAdapter);
        reorderLevelReport = new ReorderLevelReport(mockReportAdapter);
        reshelvingReport = new ReshelvingReport(mockReportAdapter);
        stockReport = new StockReport(mockReportAdapter);
        totalSalesReport = new TotalSalesReport(mockReportAdapter);
    }

    @Test
    void testBillReport_FetchesData() {
        when(mockReportAdapter.generateBillReport(anyInt())).thenReturn(Collections.emptyList());

        billReport.generateReport();

        verify(mockReportAdapter, times(1)).generateBillReport(anyInt());
    }

    @Test
    void testReorderLevelReport_FetchesData() {
        when(mockReportAdapter.generateReorderLevelReport()).thenReturn(Collections.emptyList());

        reorderLevelReport.generateReport();

        verify(mockReportAdapter, times(1)).generateReorderLevelReport();
    }

    @Test
    void testReshelvingReport_FetchesData() {
        when(mockReportAdapter.generateReshelvingReport()).thenReturn(Collections.emptyList());

        reshelvingReport.generateReport();

        verify(mockReportAdapter, times(1)).generateReshelvingReport();
    }

    @Test
    void testStockReport_FetchesData() {
        when(mockReportAdapter.generateStockReport()).thenReturn(Collections.emptyList());

        stockReport.generateReport();

        verify(mockReportAdapter, times(1)).generateStockReport();
    }

    @Test
    void testTotalSalesReport_FetchesData() {
        when(mockReportAdapter.generateDailySalesReport(anyString())).thenReturn(Collections.emptyList());

        totalSalesReport.generateReport();

        verify(mockReportAdapter, times(1)).generateDailySalesReport(anyString());
    }
}
