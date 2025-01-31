package adapter;

import database.DBConnection;
import java.sql.*;
import java.util.*;

public class StockManagementSQLAdapter implements StockManagementAdapter {
    private DBConnection dbConnection;

    public StockManagementSQLAdapter() {
        this.dbConnection = DBConnection.getInstance();
    }

    @Override
    public boolean isItemInStock(String itemCode, int quantity) {
        String query = "SELECT SUM(quantity) AS total_quantity FROM batches WHERE item_code = ?";
        Connection conn = null;
        boolean inStock = false;

        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, itemCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int totalStock = rs.getInt("total_quantity");
                inStock = totalStock >= quantity;
            }

            rs.close();
            stmt.close();
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }

        return inStock;
    }

    public Map<String, Object> getItemByCode(String itemCode) {
        Map<String, Object> itemDetails = null;
        String query = "SELECT item_code, name, price, category FROM items WHERE item_code = ?";
        Connection conn = null;

        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, itemCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                itemDetails = new HashMap<>();
                itemDetails.put("item_code", rs.getString("item_code"));
                itemDetails.put("name", rs.getString("name"));
                itemDetails.put("price", rs.getDouble("price"));
                itemDetails.put("category", rs.getString("category"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }

        return itemDetails;
    }

    @Override
    public void updateStock(String itemCode, int quantity) {
        String selectQuery = "SELECT batch_id, quantity FROM batches WHERE item_code = ? ORDER BY expiry_date ASC, date_received ASC";
        Connection conn = null;

        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
            selectStmt.setString(1, itemCode);
            ResultSet rs = selectStmt.executeQuery();

            while (rs.next() && quantity > 0) {
                int batchId = rs.getInt("batch_id");
                int batchQuantity = rs.getInt("quantity");

                int reduction = Math.min(batchQuantity, quantity);
                quantity -= reduction;

                String updateQuery = "UPDATE batches SET quantity = quantity - ? WHERE batch_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, reduction);
                updateStmt.setInt(2, batchId);
                updateStmt.executeUpdate();
                updateStmt.close();
            }

            rs.close();
            selectStmt.close();
            conn.commit(); // Commit transaction

        } catch (SQLException | InterruptedException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback in case of failure
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit mode
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                dbConnection.releaseConnection(conn);
            }
        }
    }

    @Override
    public void reshelveItems() {
        String selectQuery = "SELECT batch_id, quantity FROM batches WHERE quantity > 0 ORDER BY expiry_date ASC, date_received ASC";
        Connection conn = null;

        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
            ResultSet rs = selectStmt.executeQuery();

            while (rs.next()) {
                int batchId = rs.getInt("batch_id");
                int quantity = rs.getInt("quantity");

                String insertQuery = "INSERT INTO store_inventory (batch_id, quantity_on_shelves) VALUES (?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setInt(1, batchId);
                insertStmt.setInt(2, quantity);
                insertStmt.executeUpdate();
                insertStmt.close();
            }

            rs.close();
            selectStmt.close();
            conn.commit(); // Commit transaction

        } catch (SQLException | InterruptedException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on failure
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                dbConnection.releaseConnection(conn);
            }
        }
    }

    @Override
    public List<Map<String, Object>> checkReorderLevels() {
        List<Map<String, Object>> reorderList = new ArrayList<>();
        String query = "SELECT item_code, SUM(quantity) AS total_quantity FROM batches GROUP BY item_code HAVING total_quantity < 50";
        Connection conn = null;

        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("item_code", rs.getString("item_code"));
                item.put("total_quantity", rs.getInt("total_quantity"));
                reorderList.add(item);
            }

            rs.close();
            stmt.close();
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }

        return reorderList;
    }
}
