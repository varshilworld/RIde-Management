package model;

import java.io.Serializable;

public class Ride implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id; // DB id
    private String source;
    private String destination;
    private double fare;
    private int seatsAvailable;

    // Constructor with id (for DB-loaded rides)
    public Ride(int id, String source, String destination, double fare, int seatsAvailable) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.fare = fare;
        this.seatsAvailable = seatsAvailable;
    }

    // Constructor without id (for new rides before DB insertion)
    public Ride(String source, String destination, double fare) {
        this(0, source, destination, fare, 4); // default 4 seats
    }

    // Constructor convenience with seats
    public Ride(String source, String destination, double fare, int seatsAvailable) {
        this(0, source, destination, fare, seatsAvailable);
    }

    // Getters
    public int getId() { return id; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public double getFare() { return fare; }
    public int getSeatsAvailable() { return seatsAvailable; }

    // Setter for id (needed after inserting new ride into DB)
    public void setId(int id) { this.id = id; }

    public void setSeatsAvailable(int seats) { this.seatsAvailable = seats; }

    @Override
    public String toString() {
        return String.format("%s → %s | ₹%.2f | Seats: %d", source, destination, fare, seatsAvailable);
    }
}
