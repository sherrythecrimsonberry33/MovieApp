package backend.actors;

import backend.Entity.BookingReservation;
import backend.Entity.MovieHall;
import backend.Entity.MovieTimings;
import backend.Entity.PaymentDetails;
import backend.Entity.Seat;
import backend.Entity.TicketInfo;
import backend.Entity.Transaction;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

public class RegisteredUser extends User {
    private String firstName;
    private String lastName;
    private String password;
    private String address;
    private LocalDateTime membershipStartDate;
    private LocalDateTime lastAnnualFeePayment;
    private PaymentDetails paymentDetails;
    private static final double ANNUAL_FEE = 20.00;
    private static final int EARLY_ACCESS_PERCENTAGE = 10; // 10% seats reserved
    
    public RegisteredUser(String email, String firstName, String lastName, String password, String address) {
        super(email);
        if (firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("First name, last name, password, and address cannot be empty");
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = hashPassword(password);
        this.address = address;
        this.membershipStartDate = LocalDateTime.now();
        this.lastAnnualFeePayment = LocalDateTime.now();
    }

    // Constructor with payment details
    public RegisteredUser(String email, String firstName, String lastName, String password, 
                         String address, PaymentDetails paymentDetails) {
        this(email, firstName, lastName, password, address);
        this.paymentDetails = paymentDetails;
    }

    private String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainPassword.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean verifyPassword(String attemptedPassword) {
        String hashedAttempt = hashPassword(attemptedPassword);
        return hashedAttempt.equals(this.password);
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (!verifyPassword(oldPassword)) {
            throw new IllegalArgumentException("Invalid old password");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }
        this.password = hashPassword(newPassword);
    }
    
    @Override
    public double calculateTicketPrice(double basePrice) {
        if (!isAnnualFeePaid()) {
            throw new IllegalStateException("Annual membership fee payment required");
        }
        return basePrice;
    }
    
    @Override
    public boolean cancelTicket(TicketInfo ticket) {
        // Verify ticket belongs to this user
        if (!ticket.getUserEmail().equals(this.email)) {
            throw new IllegalArgumentException("Ticket does not belong to this user");
        }

        LocalDateTime showDateTime = ticket.getMovieTimings().getShowDateTime();
        
        if (LocalDateTime.now().plusHours(72).isBefore(showDateTime)) {
            // No cancellation fee for registered users
            Transaction refundTransaction = Transaction.processRefund(
                ticket.getTransaction().getTransactionId(),
                ticket.getTotalPrice() // Full refund
            );
            
            return refundTransaction.getStatus().equals("APPROVED");
        }
        return false;
    }

    @Override
    public BookingReservation createBooking(MovieTimings movieTiming, List<Seat> selectedSeats) {
        if (!isAnnualFeePaid()) {
            throw new IllegalStateException("Annual membership fee payment required");
        }

        // Handle early access booking limits
        if (movieTiming.isEarlyAccessPeriod()) {
            int maxEarlyAccessSeats = (movieTiming.getMovieHall().getTotalSeats() * EARLY_ACCESS_PERCENTAGE) / 100;
            int currentEarlyBookings = countEarlyAccessBookings(movieTiming);
            
            if (currentEarlyBookings + selectedSeats.size() > maxEarlyAccessSeats) {
                throw new IllegalStateException("Exceeds early access seat limit");
            }
        }

        return super.createBooking(movieTiming, selectedSeats);
    }

    private int countEarlyAccessBookings(MovieTimings movieTiming) {
        return ticketHistory.stream()
            .filter(ticket -> ticket.getMovieTimings().equals(movieTiming))
            .mapToInt(ticket -> ticket.getSeats().size())
            .sum();
    }
    
    public boolean payAnnualFee(PaymentDetails paymentDetails) {
        if (paymentDetails == null) {
            throw new IllegalArgumentException("Payment details cannot be null");
        }

        Transaction transaction = Transaction.processPayment(paymentDetails, ANNUAL_FEE);
        
        if (transaction.getStatus().equals("APPROVED")) {
            this.lastAnnualFeePayment = LocalDateTime.now();
            this.paymentDetails = paymentDetails;
            return true;
        }
        return false;
    }
    
    public boolean isAnnualFeePaid() {
        return LocalDateTime.now().isBefore(lastAnnualFeePayment.plusYears(1));
    }

    public boolean canAccessEarlyBooking() {
        return isAnnualFeePaid();
    }

    public int getEarlyAccessSeatLimit(MovieHall hall) {
        return (hall.getTotalSeats() * EARLY_ACCESS_PERCENTAGE) / 100;
    }
    
    // Getters
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getAddress() {
        return address;
    }
    
    public LocalDateTime getMembershipStartDate() {
        return membershipStartDate;
    }
    
    public LocalDateTime getLastAnnualFeePayment() {
        return lastAnnualFeePayment;
    }
    
    public PaymentDetails getSavedPaymentDetails() {
        return paymentDetails;
    }
    
    public void updatePaymentDetails(PaymentDetails newPaymentDetails) {
        if (newPaymentDetails == null) {
            throw new IllegalArgumentException("Payment details cannot be null");
        }
        this.paymentDetails = newPaymentDetails;
    }

    public void updateAddress(String newAddress) {
        if (newAddress == null || newAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }
        this.address = newAddress.trim();
    }
}