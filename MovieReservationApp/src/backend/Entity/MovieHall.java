package backend.Entity;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;



public class MovieHall {
    private final MovieHallName hallName;
    private final int rows = 10;
    private final int columns = 12;
    private final int totalSeats = rows * columns;
    private final int numberOfExits = 4;
    private final Seat[][] seats;

    public MovieHall(MovieHallName hallName) {
        this.hallName = hallName;
        this.seats = initializeSeats();
    }

    private Seat[][] initializeSeats() {
        Seat[][] seatArray = new Seat[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                seatArray[row][col] = new Seat(row, col);
            }
        }
        return seatArray;
    }

    public boolean validateSeatSelection(List<Seat> selectedSeats) {
        // Check if seats are in same row
        int row = selectedSeats.get(0).getRow();
        if (!selectedSeats.stream().allMatch(seat -> seat.getRow() == row)) {
            return false;
        }

        // Sort seats by column
        List<Seat> sortedSeats = selectedSeats.stream()
                .sorted(Comparator.comparingInt(Seat::getColumn))
                .collect(Collectors.toList());

        // Check for gaps
        for (int i = 0; i < sortedSeats.size() - 1; i++) {
            if (sortedSeats.get(i + 1).getColumn() - sortedSeats.get(i).getColumn() > 1) {
                return false;
            }
        }

        return true;
    }

    // Getters
    public MovieHallName getHallName() {
        return hallName;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public int getNumberOfExits() {
        return numberOfExits;
    }

    public Seat getSeat(int row, int column) {
        if (row < 0 || row >= rows || column < 0 || column >= columns) {
            throw new IllegalArgumentException("Invalid seat position");
        }
        return seats[row][column];
    }
}