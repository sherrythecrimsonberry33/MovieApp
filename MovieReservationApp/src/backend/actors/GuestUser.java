package backend.actors;

import java.time.LocalDateTime;

public class GuestUser extends User {
    private static final double CANCELLATION_FEE_PERCENTAGE = 0.15;
    
    public GuestUser(String email) {
        super(email);
    }
    
    @Override
    public double calculateTicketPrice(double basePrice) {
        return basePrice; // Standard pricing for guest users
    }
    
    public boolean cancelTicket(TicketInfo ticket) {
        LocalDateTime showTime = ticket.getShowTime();
        if (LocalDateTime.now().plusHours(72).isBefore(showTime)) {
            // Process refund with 15% cancellation fee
            double refundAmount = ticket.getPrice() * (1 - CANCELLATION_FEE_PERCENTAGE);
            processRefund(refundAmount);
            return true;
        }
        return false;
    }
    
    private void processRefund(double amount) {
        // Create credit voucher valid for one year
        CreditVoucher voucher = new CreditVoucher(amount, 
            LocalDateTime.now().plusYears(1));
        // Store voucher in database and send to user
    }
}