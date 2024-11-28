package backend.Entity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TicketInfo {
    private final String ticketId;
    private final MovieTimings movieTimings;
    private final List<Seat> seats;
    private final double totalPrice;
    private final String userEmail;
    private final Transaction transaction;

    public TicketInfo(MovieTimings movieTimings, List<Seat> seats, double totalPrice, String userEmail, Transaction transaction) {
        this.ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.movieTimings = movieTimings;
        this.seats = seats;
        this.totalPrice = totalPrice;
        this.userEmail = userEmail;
        this.transaction = transaction;
    }

    // Essential getters
    public String getTicketId() {
        return ticketId;
    }

    public Movie getMovie() {
        return movieTimings.getMovie();
    }

    public String getMovieName() {
        return movieTimings.getMovie().getTitle();
    }

    public String getMovieHallName() {
        return movieTimings.getMovieHall().getHallName().toString();
    }

    public String getShowTiming() {
        return movieTimings.getFormattedMovieTimings();
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public MovieTimings getMovieTimings() {
        return movieTimings;
    }

    @Override
    public String toString() {
        String seatLabels = seats.stream()
                .map(Seat::getSeatLabel)
                .collect(Collectors.joining(", "));
                
        return String.format("Ticket ID: %s\n" +
                           "Movie: %s\n" +
                           "Hall: %s\n" +
                           "Time: %s\n" +
                           "Seats: %s\n" +
                           "Total Price: $%.2f",
                           ticketId,
                           getMovieName(),
                           getMovieHallName(),
                           getShowTiming(),
                           seatLabels,
                           totalPrice);
    }
}