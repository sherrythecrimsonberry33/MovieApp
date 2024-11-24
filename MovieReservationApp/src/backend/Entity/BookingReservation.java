package backend.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingReservation {
    private final String bookingId;
    private final MovieTimings movieTiming;
    private final List<Seat> selectedSeats;
    private final String userEmail;
    private final LocalDateTime bookingTime;
    private BookingStatus status;
    private Transaction transaction;

    public BookingReservation(MovieTimings movieTiming, List<Seat> selectedSeats, String userEmail) {
        this.bookingId = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.movieTiming = movieTiming;
        this.selectedSeats = new ArrayList<>(selectedSeats);
        this.userEmail = userEmail;
        this.bookingTime = LocalDateTime.now();
        this.status = BookingStatus.PENDING;
    }

    public boolean validateSeatSelection() {
        return movieTiming.getMovieHall().validateSeatSelection(selectedSeats);
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
        this.status = transaction.getStatus().equals("APPROVED") ? 
            BookingStatus.CONFIRMED : BookingStatus.FAILED;
    }

    // Getters
    public String getBookingId() {
        return bookingId;
    }

    public MovieTimings getMovieTiming() {
        return movieTiming;
    }

    public List<Seat> getSelectedSeats() {
        return new ArrayList<>(selectedSeats);
    }

    public String getUserEmail() {
        return userEmail;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    private enum BookingStatus {
        PENDING,
        CONFIRMED,
        FAILED,
        CANCELLED
    }
}