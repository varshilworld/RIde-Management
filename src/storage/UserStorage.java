package storage;

import java.sql.*;

public class UserStorage {

    private static final String URL = "jdbc:mysql://localhost:3306/ride_management?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    static {
        // Ensure viewers table exists
        String sql = "CREATE TABLE IF NOT EXISTS viewers (" +
                "email VARCHAR(255) PRIMARY KEY, " +
                "password VARCHAR(255) NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to ensure viewers table: " + e.getMessage());
        }
    }

    // Register a new viewer. Returns true if created, false if already exists or error.
    public static boolean registerUser(String email, String password) {
        String sql = "INSERT INTO viewers (email, password) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            // duplicate entry or other error
            //e.printStackTrace();
            return false;
        }
    }

    // Authenticate viewer credentials. Returns true if match.
    public static boolean authenticateUser(String email, String password) {
        String sql = "SELECT password FROM viewers WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                String stored = rs.getString(1);
                return stored.equals(password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
