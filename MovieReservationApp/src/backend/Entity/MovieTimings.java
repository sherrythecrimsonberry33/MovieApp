package backend.Entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;



public class MovieTimings {
    private Integer id;
    private Movie movie;
    private MovieHall movieHall;
    private LocalDateTime showDateTime;
    private Map<String, Boolean> seatAvailability;
    private double price;
    private static final int RU_SEAT_LIMIT_PERCENTAGE = 10; // 10% seats reserved for registered users

    public MovieTimings(Movie movie, MovieHall movieHall, LocalDateTime showDateTime, double price) {
        this.movie = movie;
        this.movieHall = movieHall;
        this.showDateTime = showDateTime;
        this.price = price;
        initializeSeatAvailability();
    }

    // Constructor with ID for database records
    public MovieTimings(Integer id, Movie movie, MovieHall movieHall, LocalDateTime showDateTime, double price) {
        this(movie, movieHall, showDateTime, price);
        this.id = id;
    }

    private void initializeSeatAvailability() {
        seatAvailability = new HashMap<>();
        int rows = movieHall.getRows();
        int cols = movieHall.getColumns();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                String seatId = String.format("%c%d", (char)('A' + row), col + 1);
                seatAvailability.put(seatId, true);
            }
        }

        reserveSeatsForRegisteredUsers();
    }

    private void reserveSeatsForRegisteredUsers() {
        int totalSeats = movieHall.getTotalSeats();
        int seatsToReserve = (totalSeats * RU_SEAT_LIMIT_PERCENTAGE) / 100;

        List<String> allSeats = new ArrayList<>(seatAvailability.keySet());
        Collections.sort(allSeats);
        
        int middleStart = allSeats.size() / 3;
        for (int i = 0; i < seatsToReserve && i + middleStart < allSeats.size(); i++) {
            seatAvailability.put(allSeats.get(i + middleStart), true);
        }
    }

    public double getTicketPrice() {
        return price;
    }

    public boolean bookSeats(List<String> seatIds, String userType) {
        if (!validateSeatSelection(seatIds)) {
            return false;
        }

        if (userType.equals("Guest") && !validateGuestUserBooking(seatIds)) {
            return false;
        }

        for (String seatId : seatIds) {
            seatAvailability.put(seatId, false);
        }

        return true;
    }

    private boolean validateSeatSelection(List<String> seatIds) {
        if (seatIds.isEmpty()) return false;

        // Check if all seats are in same row
        char row = seatIds.get(0).charAt(0);
        if (!seatIds.stream().allMatch(id -> id.charAt(0) == row)) {
            return false;
        }

        // Check if seats are available
        if (!seatIds.stream().allMatch(id -> seatAvailability.get(id))) {
            return false;
        }

        // Sort seats by column number
        List<Integer> columns = seatIds.stream()
            .map(id -> Integer.parseInt(id.substring(1)))
            .sorted()
            .collect(Collectors.toList());

        // Check if seats are continuous
        for (int i = 0; i < columns.size() - 1; i++) {
            if (columns.get(i + 1) - columns.get(i) != 1) {
                return false;
            }
        }

        return true;
    }

    private boolean validateGuestUserBooking(List<String> seatIds) {
        return true; // Implement actual validation logic based on your requirements
    }

    public boolean cancelBooking(List<String> seatIds) {
        if (!isCancellationAllowed()) {
            return false;
        }

        for (String seatId : seatIds) {
            seatAvailability.put(seatId, true);
        }

        return true;
    }

    public boolean isCancellationAllowed() {
        LocalDateTime cancellationDeadline = showDateTime.minusHours(72);
        return LocalDateTime.now().isBefore(cancellationDeadline);
    }

    public int getAvailableSeatsCount() {
        return (int) seatAvailability.entrySet().stream()
            .filter(Map.Entry::getValue)
            .count();
    }

    public String getFormattedMovieTimings() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return showDateTime.format(formatter);
    }

    public boolean isSeatAvailable(String seatId) {
        return seatAvailability.getOrDefault(seatId, false);
    }

    public List<String> getAvailableSeats() {
        return seatAvailability.entrySet().stream()
            .filter(Map.Entry::getValue)
            .map(Map.Entry::getKey)
            .sorted()
            .collect(Collectors.toList());
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public Movie getMovie() {
        return movie;
    }

    public MovieHall getMovieHall() {
        return movieHall;
    }

    public LocalDateTime getShowDateTime() {
        return showDateTime;
    }

    public Map<String, Boolean> getSeatAvailability() {
        return new HashMap<>(seatAvailability);
    }

    @Override
    public String toString() {
        return String.format("%s - %s - %s - Available Seats: %d",
            movie.getTitle(),
            movieHall.getHallName(),
            getFormattedMovieTimings(),
            getAvailableSeatsCount());
    }
}