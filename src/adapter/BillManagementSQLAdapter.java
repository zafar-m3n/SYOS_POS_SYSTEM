package adapter;

import database.DBConnection;
import java.sql.*;
import java.util.*;

public class BillManagementSQLAdapter implements BillManagementAdapter {
    private DBConnection dbConnection;

    public BillManagementSQLAdapter() {
        this.dbConnection = DBConnection.getInstance();
    }

    @Override
    public int insertTransaction(String transactionType, double totalAmount) {
        String query = "INSERT INTO transactions (transaction_type, total_amount) VALUES (?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, transactionType);
            stmt.setDouble(2, totalAmount);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void insertTransactionItem(int transactionId, String itemCode, int quantity, double totalPrice) {
        String query = "INSERT INTO transaction_items (transaction_id, item_code, quantity, total_price) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, transactionId);
            stmt.setString(2, itemCode);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, totalPrice);
            stmt.executeUpdate();
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Map<String, Object>> getTransactionDetails(int transactionId) {
        String query = "SELECT * FROM transactions WHERE transaction_id = ?";
        List<Map<String, Object>> resultList = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("transaction_id", rs.getInt("transaction_id"));
                row.put("total_amount", rs.getDouble("total_amount"));
                row.put("transaction_date", rs.getTimestamp("transaction_date"));
                resultList.add(row);
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
