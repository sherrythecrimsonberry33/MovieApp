package backend.Entity;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TicketInfo {
    private final String ticketId;
    private final MovieTimings movieTimings;
    private final List<Seat> seats;

    public TicketInfo(MovieTimings movieTimings, List<Seat> seats) {
        this.ticketId = generateTicketId();
        this.movieTimings = movieTimings;
        this.seats = seats;
    }

    private String generateTicketId() {
        return "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
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

    public List<Seat> getSeats() {
        return seats;
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
                           "Seats: %s",
                           ticketId,
                           getMovieName(),
                           getMovieHallName(),
                           getShowTiming(),
                           seatLabels);
    }
}
