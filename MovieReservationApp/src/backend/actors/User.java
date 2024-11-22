package backend.actors;
import java.util.UUID;
import java.time.LocalDateTime;

public abstract class User {
    private final UUID userId;
    private String email;
    private ReservationBin currentReservation;
    private List<TicketInfo> ticketHistory;
    
    protected User(String email) {
        this.userId = UUID.randomUUID();
        this.email = email;
        this.ticketHistory = new ArrayList<>();
    }
    
    // Core functionality for all users
    public void startNewReservation(Movie movie, ShowTime showTime) {
        this.currentReservation = new ReservationBin(movie, showTime);
    }
    
    public boolean addTicketsToReservation(int numberOfTickets) {
        if (currentReservation == null) {
            throw new IllegalStateException("No active reservation found");
        }
        return currentReservation.setNumberOfTickets(numberOfTickets);
    }
    
    public void selectSeats(List<Seat> selectedSeats) {
        if (currentReservation == null) {
            throw new IllegalStateException("No active reservation found");
        }
        currentReservation.setSelectedSeats(selectedSeats);
    }
    
    public abstract double calculateTicketPrice(double basePrice);
    
    // Getters
    public UUID getUserId() {
        return userId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public ReservationBin getCurrentReservation() {
        return currentReservation;
    }
    
    public List<TicketInfo> getTicketHistory() {
        return new ArrayList<>(ticketHistory);
    }
    
    // Setters
    public void setEmail(String email) {
        this.email = email;
    }
    
    protected void addToTicketHistory(TicketInfo ticket) {
        this.ticketHistory.add(ticket);
    }
}