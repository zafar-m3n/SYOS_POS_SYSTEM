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
        Connection conn = null;
        int transactionId = -1;

        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, transactionType);
            stmt.setDouble(2, totalAmount);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                transactionId = rs.getInt(1);
            }

            rs.close();
            stmt.close();
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                dbConnection.releaseConnection(conn);
            }
        }

        return transactionId;
    }

    @Override
    public void insertTransactionItem(int transactionId, String itemCode, int quantity, double totalPrice) {
        String query = "INSERT INTO transaction_items (transaction_id, item_code, quantity, total_price) VALUES (?, ?, ?, ?)";
        Connection conn = null;

        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setInt(1, transactionId);
            stmt.setString(2, itemCode);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, totalPrice);
            stmt.executeUpdate();

            stmt.close();
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                dbConnection.releaseConnection(conn);
            }
        }
    }

    @Override
    public List<Map<String, Object>> getTransactionDetails(int transactionId) {
        String query = "SELECT * FROM transactions WHERE transaction_id = ?";
        List<Map<String, Object>> resultList = new ArrayList<>();
        Connection conn = null;

        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("transaction_id", rs.getInt("transaction_id"));
                row.put("total_amount", rs.getDouble("total_amount"));
                row.put("transaction_date", rs.getTimestamp("transaction_date"));
                resultList.add(row);
            }

            rs.close();
            stmt.close();
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                dbConnection.releaseConnection(conn);
            }
        }

        return resultList;
    }
}
