
package backend.actors;

import java.time.LocalDateTime;


public class RegisteredUser extends User {
    private String firstName;
    private String lastName;
    private String password;
    private LocalDateTime membershipStartDate;
    private LocalDateTime lastAnnualFeePayment;
    private PaymentDetails paymentDetails;
    private static final double ANNUAL_FEE = 20.00;
    private static final double CANCELLATION_FEE = 0.00; // No fee for registered users
    
    public RegisteredUser(String email, String firstName, String lastName, String password) {
        super(email);
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.membershipStartDate = LocalDateTime.now();
        this.lastAnnualFeePayment = LocalDateTime.now();
    }
    
    @Override
    public double calculateTicketPrice(double basePrice) {
        if (!isAnnualFeePaid()) {
            throw new IllegalStateException("Annual membership fee payment required");
        }
        return basePrice; // No additional fees for registered users
    }
    
    public boolean isAnnualFeePaid() {
        return LocalDateTime.now().isBefore(
            lastAnnualFeePayment.plusYears(1));
    }
    
    public void payAnnualFee(PaymentDetails paymentDetails) {
        // Process payment for annual fee
        Transaction.processPayment(paymentDetails, ANNUAL_FEE);
        this.lastAnnualFeePayment = LocalDateTime.now();
    }
    
    public boolean cancelTicket(TicketInfo ticket) {
        LocalDateTime showTime = ticket.getShowTime();
        if (LocalDateTime.now().plusHours(72).isBefore(showTime)) {
            // Process refund with no cancellation fee
            processRefund(ticket.getPrice());
            return true;
        }
        return false;
    }
    
    private void processRefund(double amount) {
        if (paymentDetails != null) {
            Transaction.processRefund(paymentDetails, amount);
        }
    }
    
    // Getters and setters
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public LocalDateTime getMembershipStartDate() {
        return membershipStartDate;
    }
    
    public LocalDateTime getLastAnnualFeePayment() {
        return lastAnnualFeePayment;
    }
    
    public void setPaymentDetails(PaymentDetails paymentDetails) {
        this.paymentDetails = paymentDetails;
    }
}