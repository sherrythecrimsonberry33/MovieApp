package backend.actors;

import backend.Entity.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class User {
    protected final UUID userId;
    protected String email;
    protected ReservationBin reservationBin;
    protected List<TicketInfo> ticketHistory;
    
    protected User(String email) {
        this.userId = UUID.randomUUID();
        this.email = email;
        this.ticketHistory = new ArrayList<>();
    }
    
    public void startNewReservation() {
        if (this.reservationBin != null && !this.reservationBin.isExpired()) {
            throw new IllegalStateException("Active reservation already exists");
        }
        this.reservationBin = new ReservationBin();
    }
    
    public BookingReservation createBooking(MovieTimings movieTiming, List<Seat> selectedSeats) {
        if (reservationBin == null || reservationBin.isExpired()) {
            throw new IllegalStateException("No active reservation bin");
        }
        
        BookingReservation booking = new BookingReservation(movieTiming, selectedSeats, email);
        reservationBin.addReservation(booking);
        return booking;
    }
    
    public void addToTicketHistory(TicketInfo ticket) {
        ticketHistory.add(ticket);
    }
    
    public abstract boolean cancelTicket(TicketInfo ticket);
    public abstract double calculateTicketPrice(double basePrice);
    
    // Getters
    public UUID getUserId() {
        return userId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public ReservationBin getReservationBin() {
        return reservationBin;
    }
    
    public List<TicketInfo> getTicketHistory() {
        return new ArrayList<>(ticketHistory);
    }
}