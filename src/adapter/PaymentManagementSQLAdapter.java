package adapter;

import database.DBConnection;
import java.sql.*;

public class PaymentManagementSQLAdapter implements PaymentManagementAdapter {
    private DBConnection dbConnection;

    public PaymentManagementSQLAdapter() {
        this.dbConnection = DBConnection.getInstance();
    }

    @Override
    public void insertPayment(int transactionId, String paymentMethod, double cashTendered, double changeAmount) {
        String query = "INSERT INTO payments (transaction_id, payment_method) VALUES (?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, transactionId);
            stmt.setString(2, paymentMethod);
            stmt.executeUpdate();
            if ("Cash".equals(paymentMethod)) {
                String cashQuery = "INSERT INTO cash_payments (payment_id, cash_tendered, change_amount) VALUES (?, ?, ?)";
                try (PreparedStatement cashStmt = conn.prepareStatement(cashQuery)) {
                    cashStmt.setInt(1, transactionId);
                    cashStmt.setDouble(2, cashTendered);
                    cashStmt.setDouble(3, changeAmount);
                    cashStmt.executeUpdate();
                }
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
