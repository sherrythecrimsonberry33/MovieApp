package backend.actions;

public class Seat {
    private final int row;
    private final int column;
    private boolean isBooked;

    public Seat(int row, int column) {
        this.row = row;
        this.column = column;
        this.isBooked = false;
    }

    public synchronized boolean book() {
        if (!isBooked) {
            isBooked = true;
            return true;
        }
        return false;
    }

    public synchronized void cancelBooking() {
        isBooked = false;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public String getSeatLabel() {
        return String.format("%c%d", (char)('A' + row), column + 1);
    }

    @Override
    public String toString() {
        return String.format("Seat %s", getSeatLabel());
    }
}