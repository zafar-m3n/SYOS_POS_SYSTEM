package adapter;

import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportAdapter {
    private final DBConnection dbConnection;

    public ReportAdapter() {
        this.dbConnection = DBConnection.getInstance();
    }

    public List<String> generateTotalSalesReport(String date, String transactionType) {
        String query = "SELECT ti.item_code, i.name, SUM(ti.quantity) AS total_quantity, SUM(ti.total_price) AS total_revenue " +
                "FROM transaction_items ti " +
                "JOIN transactions t ON ti.transaction_id = t.transaction_id " +
                "JOIN items i ON ti.item_code = i.item_code " +
                "WHERE DATE(t.transaction_date) = ? AND t.transaction_type = ? " +
                "GROUP BY ti.item_code, i.name";
        List<String> salesReport = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, date);
            stmt.setString(2, transactionType);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                salesReport.add("Item Code: " + rs.getString("item_code") +
                        " | Name: " + rs.getString("name") +
                        " | Quantity Sold: " + rs.getInt("total_quantity") +
                        " | Revenue: $" + rs.getDouble("total_revenue"));
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
        return salesReport;
    }

    public List<String> generateReshelvingReport() {
        String query = "SELECT i.item_code, i.name, SUM(s.quantity_on_shelves) AS total_quantity " +
                "FROM store_inventory s " +
                "JOIN batches b ON s.batch_id = b.batch_id " +
                "JOIN items i ON b.item_code = i.item_code " +
                "GROUP BY i.item_code, i.name";
        List<String> reshelvingReport = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reshelvingReport.add("Item Code: " + rs.getString("item_code") +
                        " | Name: " + rs.getString("name") +
                        " | Needs Reshelving: " + rs.getInt("total_quantity"));
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
        return reshelvingReport;
    }

    public List<String> generateReorderLevelReport() {
        String query = "SELECT i.item_code, i.name, SUM(s.quantity_on_shelves) AS total_stock " +
                "FROM store_inventory s " +
                "JOIN batches b ON s.batch_id = b.batch_id " +
                "JOIN items i ON b.item_code = i.item_code " +
                "GROUP BY i.item_code, i.name HAVING total_stock < 50";
        List<String> reorderReport = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reorderReport.add("Item Code: " + rs.getString("item_code") +
                        " | Name: " + rs.getString("name") +
                        " | Stock Level: " + rs.getInt("total_stock") + " (Below reorder threshold)");
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
        return reorderReport;
    }

    public List<String> generateStockReport() {
        String query = "SELECT b.batch_id, i.item_code, i.name, b.date_received, b.expiry_date, s.quantity_on_shelves " +
                "FROM batches b " +
                "JOIN items i ON b.item_code = i.item_code " +
                "LEFT JOIN store_inventory s ON b.batch_id = s.batch_id";
        List<String> stockReport = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                stockReport.add("Batch ID: " + rs.getInt("batch_id") +
                        " | Item Code: " + rs.getString("item_code") +
                        " | Name: " + rs.getString("name") +
                        " | Received: " + rs.getDate("date_received") +
                        " | Expiry: " + rs.getDate("expiry_date") +
                        " | Stock: " + rs.getInt("quantity_on_shelves"));
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
        return stockReport;
    }

    public List<String> generateBillReport() {
        String query = "SELECT transaction_id, transaction_type, total_amount, transaction_date FROM transactions";
        List<String> billReport = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                billReport.add("Transaction ID: " + rs.getInt("transaction_id") +
                        " | Type: " + rs.getString("transaction_type") +
                        " | Total: $" + rs.getDouble("total_amount") +
                        " | Date: " + rs.getTimestamp("transaction_date"));
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
        return billReport;
    }
}
