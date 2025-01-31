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
        String paymentQuery = "INSERT INTO payments (transaction_id, payment_method) VALUES (?, ?)";
        Connection conn = null;
        int paymentId = -1;

        try {
            conn = dbConnection.getConnection();
            PreparedStatement paymentStmt = conn.prepareStatement(paymentQuery, Statement.RETURN_GENERATED_KEYS);

            paymentStmt.setInt(1, transactionId);
            paymentStmt.setString(2, paymentMethod);
            paymentStmt.executeUpdate();

            // Get the generated payment ID
            ResultSet rs = paymentStmt.getGeneratedKeys();
            if (rs.next()) {
                paymentId = rs.getInt(1);
            }

            rs.close();
            paymentStmt.close();

            // If the payment is cash, insert into cash_payments table
            if ("Cash".equals(paymentMethod) && paymentId != -1) {
                String cashQuery = "INSERT INTO cash_payments (payment_id, cash_tendered, change_amount) VALUES (?, ?, ?)";
                PreparedStatement cashStmt = conn.prepareStatement(cashQuery);

                cashStmt.setInt(1, paymentId); // Use the generated payment ID, not the transaction ID
                cashStmt.setDouble(2, cashTendered);
                cashStmt.setDouble(3, changeAmount);
                cashStmt.executeUpdate();

                cashStmt.close();
            }

        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                dbConnection.releaseConnection(conn);
            }
        }
    }
}
