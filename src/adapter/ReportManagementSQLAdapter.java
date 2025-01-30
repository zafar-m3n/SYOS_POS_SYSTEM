package adapter;

import database.DBConnection;
import java.sql.*;
import java.util.*;

public class ReportManagementSQLAdapter implements ReportManagementAdapter {
    private DBConnection dbConnection;

    public ReportManagementSQLAdapter() {
        this.dbConnection = DBConnection.getInstance();
    }

    @Override
    public List<Map<String, Object>> generateDailySalesReport(String date) {
        List<Map<String, Object>> salesReport = new ArrayList<>();
        String query = "SELECT ti.item_code, i.name, SUM(ti.quantity) AS total_quantity, SUM(ti.total_price) AS total_revenue " +
                "FROM transaction_items ti " +
                "JOIN items i ON ti.item_code = i.item_code " +
                "JOIN transactions t ON ti.transaction_id = t.transaction_id " +
                "WHERE DATE(t.transaction_date) = ? " +
                "GROUP BY ti.item_code, i.name";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, date);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> record = new HashMap<>();
                record.put("item_code", rs.getString("item_code"));
                record.put("name", rs.getString("name"));
                record.put("total_quantity", rs.getInt("total_quantity"));
                record.put("total_revenue", rs.getDouble("total_revenue"));
                salesReport.add(record);
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
        return salesReport;
    }

    @Override
    public List<Map<String, Object>> generateReshelvingReport() {
        List<Map<String, Object>> reshelvingReport = new ArrayList<>();
        String query = "SELECT i.item_code, i.name, SUM(s.quantity_on_shelves) AS total_shelved " +
                "FROM store_inventory s " +
                "JOIN batches b ON s.batch_id = b.batch_id " +
                "JOIN items i ON b.item_code = i.item_code " +
                "GROUP BY i.item_code, i.name";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> record = new HashMap<>();
                record.put("item_code", rs.getString("item_code"));
                record.put("name", rs.getString("name"));
                record.put("total_shelved", rs.getInt("total_shelved"));
                reshelvingReport.add(record);
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
        return reshelvingReport;
    }

    @Override
    public List<Map<String, Object>> generateReorderLevelReport() {
        List<Map<String, Object>> reorderReport = new ArrayList<>();
        String query = "SELECT i.item_code, i.name, SUM(b.quantity) AS total_quantity " +
                "FROM items i " +
                "JOIN batches b ON i.item_code = b.item_code " +
                "GROUP BY i.item_code, i.name " +
                "HAVING total_quantity < 50";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> record = new HashMap<>();
                record.put("item_code", rs.getString("item_code"));
                record.put("name", rs.getString("name"));
                record.put("total_quantity", rs.getInt("total_quantity"));
                reorderReport.add(record);
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
        return reorderReport;
    }

    @Override
    public List<Map<String, Object>> generateStockReport() {
        List<Map<String, Object>> stockReport = new ArrayList<>();
        String query = "SELECT b.batch_id, i.item_code, i.name, b.date_received, b.expiry_date, b.quantity " +
                "FROM batches b " +
                "JOIN items i ON b.item_code = i.item_code " +
                "ORDER BY i.name, b.expiry_date ASC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> record = new HashMap<>();
                record.put("batch_id", rs.getInt("batch_id"));
                record.put("item_code", rs.getString("item_code"));
                record.put("name", rs.getString("name"));
                record.put("date_received", rs.getDate("date_received"));
                record.put("expiry_date", rs.getDate("expiry_date"));
                record.put("quantity", rs.getInt("quantity"));
                stockReport.add(record);
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
        return stockReport;
    }

    @Override
    public List<Map<String, Object>> generateBillReport(int transactionId) {
        List<Map<String, Object>> billReport = new ArrayList<>();
        String billQuery = "SELECT t.transaction_id, t.transaction_type, t.total_amount, t.transaction_date, " +
                "p.payment_method, cp.cash_tendered, cp.change_amount " +
                "FROM transactions t " +
                "LEFT JOIN payments p ON t.transaction_id = p.transaction_id " +
                "LEFT JOIN cash_payments cp ON p.payment_id = cp.payment_id " +
                "WHERE t.transaction_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(billQuery)) {
            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Map<String, Object> billDetails = new HashMap<>();
                billDetails.put("transaction_id", rs.getInt("transaction_id"));
                billDetails.put("transaction_type", rs.getString("transaction_type"));
                billDetails.put("total_amount", rs.getDouble("total_amount"));
                billDetails.put("transaction_date", rs.getTimestamp("transaction_date"));
                billDetails.put("payment_method", rs.getString("payment_method"));
                billDetails.put("cash_tendered", rs.getDouble("cash_tendered"));
                billDetails.put("change_amount", rs.getDouble("change_amount"));
                billReport.add(billDetails);
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }

        String itemsQuery = "SELECT ti.item_code, i.name, ti.quantity, ti.total_price " +
                "FROM transaction_items ti " +
                "JOIN items i ON ti.item_code = i.item_code " +
                "WHERE ti.transaction_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(itemsQuery)) {
            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> itemDetails = new HashMap<>();
                itemDetails.put("item_code", rs.getString("item_code"));
                itemDetails.put("name", rs.getString("name"));
                itemDetails.put("quantity", rs.getInt("quantity"));
                itemDetails.put("total_price", rs.getDouble("total_price"));
                billReport.add(itemDetails);
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
        return billReport;
    }
}
