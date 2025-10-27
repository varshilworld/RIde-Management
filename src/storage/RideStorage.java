package storage;

import model.Ride;
import java.sql.*;
import java.util.ArrayList;

public class RideStorage {

    private static final String URL = "jdbc:mysql://localhost:3306/ride_management?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";         // <-- set your MySQL username
    private static final String PASSWORD = "1234";     // <-- set your MySQL password

    // Save a new ride to DB
    public static void saveRide(Ride ride) {
        String sql = "INSERT INTO rides (source, destination, fare, seats_available) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, ride.getSource());
            stmt.setString(2, ride.getDestination());
            stmt.setDouble(3, ride.getFare());
            stmt.setInt(4, ride.getSeatsAvailable());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) throw new SQLException("Creating ride failed, no rows affected.");

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    ride.setId(keys.getInt(1)); // update ride with generated ID
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database insert error: " + e.getMessage());
        }
    }

    // Fetch bookings for a user
    public static java.util.ArrayList<model.Booking> getBookingsForUser(String userEmail) {
        java.util.ArrayList<model.Booking> list = new java.util.ArrayList<>();
        ensureBookingsSchemaIfNeeded();
        String sql = "SELECT id, ride_id, ride_source, ride_destination, user_email, seats_booked, total_fare, booked_at FROM bookings WHERE user_email = ? ORDER BY booked_at DESC";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userEmail);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new model.Booking(
                            rs.getInt("id"),
                            rs.getInt("ride_id"),
                            rs.getString("ride_source"),
                            rs.getString("ride_destination"),
                            rs.getString("user_email"),
                            rs.getInt("seats_booked"),
                            rs.getDouble("total_fare"),
                            rs.getTimestamp("booked_at")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static void ensureBookingsSchemaIfNeeded() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            ensureBookingsSchema(conn);
        } catch (SQLException e) {
            // ignore
        }
    }

    // Load all rides from DB
    public static ArrayList<Ride> loadRides() {
        ArrayList<Ride> rides = new ArrayList<>();
        ensureRidesSchemaIfNeeded();
        String sql = "SELECT id, source, destination, fare, seats_available FROM rides WHERE seats_available > 0";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
        rides.add(new Ride(
            rs.getInt("id"),
            rs.getString("source"),
            rs.getString("destination"),
            rs.getDouble("fare"),
            rs.getInt("seats_available")
        ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database load error: " + e.getMessage());
        }
        return rides;
    }

    // Search rides by keyword (matches source OR destination). If keyword is empty or null, returns all rides.
    public static ArrayList<Ride> searchRides(String keyword) {
        ArrayList<Ride> rides = new ArrayList<>();
        // If keyword is empty, fallback to loadRides()
        if (keyword == null || keyword.trim().isEmpty()) {
            return loadRides();
        }

    ensureRidesSchemaIfNeeded();
    String sql = "SELECT id, source, destination, fare, seats_available FROM rides WHERE (source LIKE ? OR destination LIKE ?) AND seats_available > 0";
        String like = "%" + keyword.trim() + "%";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, like);
            stmt.setString(2, like);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
            rides.add(new Ride(
                rs.getInt("id"),
                rs.getString("source"),
                rs.getString("destination"),
                rs.getDouble("fare"),
                rs.getInt("seats_available")
            ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database search error: " + e.getMessage());
        }

        return rides;
    }

    // Search rides by source AND destination (both are partial-match). If either is empty, it will match all for that field.
    public static ArrayList<Ride> searchRidesBySourceDestination(String source, String destination) {
        ArrayList<Ride> rides = new ArrayList<>();
        String srcLike = (source == null || source.trim().isEmpty()) ? "%" : "%" + source.trim() + "%";
        String dstLike = (destination == null || destination.trim().isEmpty()) ? "%" : "%" + destination.trim() + "%";

    ensureRidesSchemaIfNeeded();
    String sql = "SELECT id, source, destination, fare, seats_available FROM rides WHERE source LIKE ? AND destination LIKE ? AND seats_available > 0";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, srcLike);
            stmt.setString(2, dstLike);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
            rides.add(new Ride(
                rs.getInt("id"),
                rs.getString("source"),
                rs.getString("destination"),
                rs.getDouble("fare"),
                rs.getInt("seats_available")
            ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database search error: " + e.getMessage());
        }

        return rides;
    }

    /**
     * Book a number of seats on a ride. Inserts a booking and decrements seats_available.
     * If seats become zero, the ride row will be removed. Returns true on success.
     */
    public static boolean bookRide(int rideId, String userEmail, int seatsToBook) {
    String insertBooking = "INSERT INTO bookings (ride_id, user_email, ride_source, ride_destination, seats_booked, total_fare, booked_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
    String selectRide = "SELECT fare, seats_available, source, destination FROM rides WHERE id = ? FOR UPDATE";
        String deleteRide = "DELETE FROM rides WHERE id = ?";
        String updateRide = "UPDATE rides SET seats_available = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            ensureBookingsSchema(conn);

            // ensure rides and bookings schema exist
            ensureRidesSchema(conn);
            ensureBookingsSchema(conn);

            conn.setAutoCommit(false);

            double fare;
            int seatsAvailable;
            String rideSource = "";
            String rideDestination = "";
            try (PreparedStatement ps = conn.prepareStatement(selectRide)) {
                ps.setInt(1, rideId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false; // ride not found
                    }
                    fare = rs.getDouble("fare");
                    seatsAvailable = rs.getInt("seats_available");
                    rideSource = rs.getString("source");
                    rideDestination = rs.getString("destination");
                }
            }

            if (seatsToBook <= 0 || seatsToBook > seatsAvailable) {
                conn.rollback();
                return false; // invalid request
            }

            double totalFare = fare * seatsToBook;

            try (PreparedStatement psInsert = conn.prepareStatement(insertBooking)) {
                psInsert.setInt(1, rideId);
                psInsert.setString(2, userEmail);
                psInsert.setString(3, rideSource);
                psInsert.setString(4, rideDestination);
                psInsert.setInt(5, seatsToBook);
                psInsert.setDouble(6, totalFare);
                psInsert.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
                psInsert.executeUpdate();
            }

            int newSeats = seatsAvailable - seatsToBook;
            if (newSeats <= 0) {
                try (PreparedStatement psDel = conn.prepareStatement(deleteRide)) {
                    psDel.setInt(1, rideId);
                    psDel.executeUpdate();
                }
            } else {
                try (PreparedStatement psUpd = conn.prepareStatement(updateRide)) {
                    psUpd.setInt(1, newSeats);
                    psUpd.setInt(2, rideId);
                    psUpd.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Booking failed: " + e.getMessage());
            return false;
        }
    }

    // Ensure rides table exists and has seats_available column
    private static void ensureRidesSchema(Connection conn) {
        try (Statement st = conn.createStatement()) {
            String create = "CREATE TABLE IF NOT EXISTS rides (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "source VARCHAR(255), " +
                    "destination VARCHAR(255), " +
                    "fare DOUBLE, " +
                    "seats_available INT DEFAULT 4)";
            st.execute(create);
        } catch (SQLException e) {
            // ignore here; caller handles
        }

        // check if column exists; if not, try to add it
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, "rides", "seats_available")) {
            if (!rs.next()) {
                try (Statement st = conn.createStatement()) {
                    st.execute("ALTER TABLE rides ADD COLUMN seats_available INT DEFAULT 4");
                } catch (SQLException ex) {
                    // ignore
                }
            }
        } catch (SQLException e) {
            // ignore
        }
    }

    private static void ensureBookingsSchema(Connection conn) {
        try (Statement st = conn.createStatement()) {
            String create = "CREATE TABLE IF NOT EXISTS bookings (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "ride_id INT NOT NULL, " +
                    "user_email VARCHAR(255), " +
                    "ride_source VARCHAR(255), " +
                    "ride_destination VARCHAR(255), " +
                    "seats_booked INT, " +
                    "total_fare DOUBLE, " +
                    "booked_at TIMESTAMP)";
            st.execute(create);
        } catch (SQLException e) {
            // ignore
        }

        // ensure columns exist
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, "bookings", "ride_source")) {
            if (!rs.next()) {
                try (Statement st = conn.createStatement()) {
                    st.execute("ALTER TABLE bookings ADD COLUMN ride_source VARCHAR(255)");
                } catch (SQLException ex) { /* ignore */ }
            }
        } catch (SQLException e) { /* ignore */ }
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, "bookings", "ride_destination")) {
            if (!rs.next()) {
                try (Statement st = conn.createStatement()) {
                    st.execute("ALTER TABLE bookings ADD COLUMN ride_destination VARCHAR(255)");
                } catch (SQLException ex) { /* ignore */ }
            }
        } catch (SQLException e) { /* ignore */ }

        try (ResultSet rs2 = conn.getMetaData().getColumns(null, null, "bookings", "seats_booked")) {
            if (!rs2.next()) {
                try (Statement st = conn.createStatement()) {
                    st.execute("ALTER TABLE bookings ADD COLUMN seats_booked INT");
                } catch (SQLException ex) { /* ignore */ }
            }
        } catch (SQLException e) { /* ignore */ }

        try (ResultSet rs3 = conn.getMetaData().getColumns(null, null, "bookings", "total_fare")) {
            if (!rs3.next()) {
                try (Statement st = conn.createStatement()) {
                    st.execute("ALTER TABLE bookings ADD COLUMN total_fare DOUBLE");
                } catch (SQLException ex) { /* ignore */ }
            }
        } catch (SQLException e) { /* ignore */ }
    }

    // Convenience method to ensure schema using a new connection
    private static void ensureRidesSchemaIfNeeded() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            ensureRidesSchema(conn);
        } catch (SQLException e) {
            // ignore - callers will handle SQL errors
        }
    }

    // Delete ride by ID
    public static void deleteRide(int id) {
        String sql = "DELETE FROM rides WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database delete error: " + e.getMessage());
        }
    }

    // Update ride details
    public static void updateRide(Ride ride) {
        String sql = "UPDATE rides SET source = ?, destination = ?, fare = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ride.getSource());
            stmt.setString(2, ride.getDestination());
            stmt.setDouble(3, ride.getFare());
            stmt.setInt(4, ride.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database update error: " + e.getMessage());
        }
    }
}
