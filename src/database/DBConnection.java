package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DBConnection {
    private static DBConnection instance;  // Singleton instance
    private final BlockingQueue<Connection> connectionPool;  // Object pool for connections
    private static final int POOL_SIZE = 5;  // Max number of connections in the pool

    private static final String URL = "jdbc:mysql://localhost:3306/SYOS_POS";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Private Constructor following the Singleton Pattern
    private DBConnection() {
        connectionPool = new LinkedBlockingQueue<>(POOL_SIZE);
        initializeConnectionPool();
    }

    // Static Method for getting the single instance for DB operations
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    private void initializeConnectionPool() {
        try {
            for (int i = 0; i < POOL_SIZE; i++) {
                connectionPool.add(createNewConnection());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing database connection pool", e);
        }
    }

    private Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public Connection getConnection() throws InterruptedException {
        return connectionPool.take();
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            connectionPool.offer(connection);
        }
    }

    public void closeAllConnections() {
        for (Connection connection : connectionPool) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        connectionPool.clear();
    }
}
