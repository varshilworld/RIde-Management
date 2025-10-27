package model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Booking implements Serializable {
    private int id;
    private int rideId;
    private String rideSource;
    private String rideDestination;
    private String userEmail;
    private int seatsBooked;
    private double totalFare;
    private Timestamp bookedAt;

    public Booking(int id, int rideId, String rideSource, String rideDestination, String userEmail, int seatsBooked, double totalFare, Timestamp bookedAt) {
        this.id = id;
        this.rideId = rideId;
        this.rideSource = rideSource;
        this.rideDestination = rideDestination;
        this.userEmail = userEmail;
        this.seatsBooked = seatsBooked;
        this.totalFare = totalFare;
        this.bookedAt = bookedAt;
    }

    public int getId() { return id; }
    public int getRideId() { return rideId; }
    public String getRideSource() { return rideSource; }
    public String getRideDestination() { return rideDestination; }
    public String getUserEmail() { return userEmail; }
    public int getSeatsBooked() { return seatsBooked; }
    public double getTotalFare() { return totalFare; }
    public Timestamp getBookedAt() { return bookedAt; }

    @Override
    public String toString() {
        return String.format("%s → %s | Seats: %d | ₹%.2f | %s", rideSource, rideDestination, seatsBooked, totalFare, bookedAt == null ? "" : bookedAt.toString());
    }
}
