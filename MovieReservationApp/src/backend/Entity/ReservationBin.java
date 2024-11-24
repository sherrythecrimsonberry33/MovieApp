package backend.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReservationBin {
    private final String binId;
    private final List<BookingReservation> reservations;
    private double totalAmount;
    private LocalDateTime creationTime;
    private static final int RESERVATION_TIMEOUT_MINUTES = 10;

    public ReservationBin() {
        this.binId = "RB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.reservations = new ArrayList<>();
        this.creationTime = LocalDateTime.now();
        this.totalAmount = 0.0;
    }

    public boolean addReservation(BookingReservation reservation) {
        if (isExpired()) {
            throw new IllegalStateException("Reservation bin has expired");
        }
        
        // Validate seat selection
        if (!reservation.validateSeatSelection()) {
            throw new IllegalArgumentException("Invalid seat selection");
        }

        // Calculate price based on number of seats
        double ticketPrice = reservation.getMovieTiming().getTicketPrice();
        totalAmount += ticketPrice * reservation.getSelectedSeats().size();
        
        return reservations.add(reservation);
    }

    public boolean removeReservation(String bookingId) {
        boolean removed = reservations.removeIf(res -> res.getBookingId().equals(bookingId));
        if (removed) {
            // Recalculate total amount
            totalAmount = 0.0;
            for (BookingReservation reservation : reservations) {
                double ticketPrice = reservation.getMovieTiming().getTicketPrice();
                totalAmount += ticketPrice * reservation.getSelectedSeats().size();
            }
        }
        return removed;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(
            creationTime.plusMinutes(RESERVATION_TIMEOUT_MINUTES));
    }

    public void clearBin() {
        reservations.clear();
        totalAmount = 0.0;
    }

    public List<Seat> getAllSelectedSeats() {
        List<Seat> allSeats = new ArrayList<>();
        for (BookingReservation reservation : reservations) {
            allSeats.addAll(reservation.getSelectedSeats());
        }
        return allSeats;
    }

    // Getters
    public String getBinId() {
        return binId;
    }

    public List<BookingReservation> getReservations() {
        return new ArrayList<>(reservations);
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public int getTimeoutMinutes() {
        return RESERVATION_TIMEOUT_MINUTES;
    }
}