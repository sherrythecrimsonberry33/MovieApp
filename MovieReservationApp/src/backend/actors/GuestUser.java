package backend.actors;
import backend.Entity.TicketInfo;
import backend.Entity.Transaction;
import java.time.LocalDateTime;

public class GuestUser extends User {
    private static final double CANCELLATION_FEE_PERCENTAGE = 0.15; // 15% cancellation fee
    
    public GuestUser(String email) {
        super(email);
    }
    
    @Override
    public double calculateTicketPrice(double basePrice) {
        return basePrice; // Standard price for guest users
    }
    
    @Override
    public boolean cancelTicket(TicketInfo ticket) {
        LocalDateTime showDateTime = ticket.getMovieTimings().getShowDateTime();
        
        // Check if cancellation is within 72 hours
        if (LocalDateTime.now().plusHours(72).isBefore(showDateTime)) {
            // Calculate refund amount with 15% fee
            double refundAmount = ticket.getTotalPrice() * (1 - CANCELLATION_FEE_PERCENTAGE);
            
            // Process refund through transaction system
            Transaction refundTransaction = Transaction.processRefund(
                ticket.getTransaction().getTransactionId(), 
                refundAmount
            );
            
            return refundTransaction.getStatus().equals("APPROVED");
        }
        return false;
    }
}