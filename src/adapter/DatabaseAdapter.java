package adapter;

import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {
    private final DBConnection dbConnection;

    public DatabaseAdapter() {
        this.dbConnection = DBConnection.getInstance();
    }

    public String getItemDetails(String itemCode) {
        String query = "SELECT name, price, category FROM items WHERE item_code = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, itemCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("name") + " | Price: $" + rs.getDouble("price") + " | Category: " + rs.getString("category");
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
        return "Item not found";
    }

    public List<String> getBatchDetails(String itemCode) {
        String query = "SELECT batch_id, date_received, expiry_date, quantity FROM batches WHERE item_code = ?";
        List<String> batchDetails = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, itemCode);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                batchDetails.add("Batch ID: " + rs.getInt("batch_id") +
                        " | Received: " + rs.getDate("date_received") +
                        " | Expiry: " + rs.getDate("expiry_date") +
                        " | Quantity: " + rs.getInt("quantity"));
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
        return batchDetails;
    }

    public int insertTransaction(String transactionType, double totalAmount) {
        String query = "INSERT INTO transactions (transaction_type, total_amount) VALUES (?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, transactionType);
            stmt.setDouble(2, totalAmount);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

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

    public int insertPayment(int transactionId, String paymentMethod) {
        String query = "INSERT INTO payments (transaction_id, payment_method) VALUES (?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, transactionId);
            stmt.setString(2, paymentMethod);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void insertCashPayment(int paymentId, double cashTendered, double changeAmount) {
        String query = "INSERT INTO cash_payments (payment_id, cash_tendered, change_amount) VALUES (?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, paymentId);
            stmt.setDouble(2, cashTendered);
            stmt.setDouble(3, changeAmount);
            stmt.executeUpdate();
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
