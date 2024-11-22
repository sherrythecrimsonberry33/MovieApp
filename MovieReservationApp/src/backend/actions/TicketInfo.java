package backend.actions;



import java.util.List;
import java.util.UUID;

public class TicketInfo {
    private final String ticketId;
    private final MovieTimings movieTimings;
    private final List<String> seatNumbers;
    private final double totalPrice;

    public TicketInfo(MovieTimings movieTimings, List<String> seatNumbers) {
        this.ticketId = generateTicketId();
        this.movieTimings = movieTimings;
        this.seatNumbers = seatNumbers;
        this.totalPrice = calculateTotalPrice();
    }

    private String generateTicketId() {
        return "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private double calculateTotalPrice() {
        return movieTimings.getTicketPrice() * seatNumbers.size();
    }

    // Essential getters
    public String getTicketId() {
        return ticketId;
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

    public List<String> getSeatNumbers() {
        return seatNumbers;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    @Override
    public String toString() {
        return String.format("Ticket ID: %s\n" +
                           "Movie: %s\n" +
                           "Hall: %s\n" +
                           "Time: %s\n" +
                           "Seats: %s\n" +
                           "Total: $%.2f",
                           ticketId,
                           getMovieName(),
                           getMovieHallName(),
                           getShowTiming(),
                           String.join(", ", seatNumbers),
                           totalPrice);
    }
}