package backend.Entity;


import backend.actors.RegisteredUser;
import backend.database.DatabaseConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Base64;

public class RegUserLogin {
    private static final String LOGIN_QUERY = 
        "SELECT * FROM registered_users WHERE email = ?";
        
    private static final String SIGNUP_QUERY = 
        "INSERT INTO registered_users (email, first_name, last_name, password, " +
        "address, membership_start_date, last_annual_fee_payment, " +
        "card_number, card_expiry_month, card_expiry_year, " +
        "card_cvv, card_holder_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public RegisteredUser authenticateUser(String email, String password) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            ps = conn.prepareStatement(LOGIN_QUERY);
            ps.setString(1, email);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password");
                String hashedAttempt = hashPassword(password);
                
                if (storedHash.equals(hashedAttempt)) {
                    PaymentDetails paymentDetails = new PaymentDetails(
                        rs.getString("card_number"),
                        rs.getInt("card_expiry_month"),
                        rs.getInt("card_expiry_year"),
                        rs.getString("card_cvv"),
                        rs.getString("card_holder_name")
                    );
                    
                    return new RegisteredUser(
                        rs.getString("email"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        password,  // Original password as RegisteredUser will hash it
                        rs.getString("address"),
                        paymentDetails
                    );
                }
            }
            return null; // Authentication failed
            
        } catch (SQLException e) {
            throw new RuntimeException("Database error during login", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean signupUser(String email, String firstName, String lastName, 
                            String password, String address, PaymentDetails paymentDetails) {
        if (!validateSignupInput(email, firstName, lastName, password, address, paymentDetails)) {
            return false;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            ps = conn.prepareStatement(SIGNUP_QUERY);
            
            LocalDateTime now = LocalDateTime.now();
            String hashedPassword = hashPassword(password);
            
            ps.setString(1, email);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, hashedPassword);
            ps.setString(5, address);
            ps.setObject(6, now); // membership_start_date
            ps.setObject(7, now); // last_annual_fee_payment
            ps.setString(8, paymentDetails.getCardNumber());
            ps.setInt(9, paymentDetails.getExpiryMonth());
            ps.setInt(10, paymentDetails.getExpiryYear());
            ps.setString(11, paymentDetails.getCvv());
            ps.setString(12, paymentDetails.getCardHolderName());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // Duplicate key error
                throw new RuntimeException("Email already registered", e);
            }
            throw new RuntimeException("Database error during signup", e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private boolean validateSignupInput(String email, String firstName, String lastName, 
                                      String password, String address, PaymentDetails paymentDetails) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return false;
        }
        if (firstName == null || firstName.trim().isEmpty() || 
            lastName == null || lastName.trim().isEmpty()) {
            return false;
        }
        if (password == null || password.length() < 8) {
            return false;
        }
        if (address == null || address.trim().isEmpty()) {
            return false;
        }
        if (paymentDetails == null || 
            !PaymentValidation.isValidCardNumber(paymentDetails.getCardNumber()) ||
            !PaymentValidation.isValidExpirationDate(
                paymentDetails.getExpiryMonth(), 
                paymentDetails.getExpiryYear()) ||
            !PaymentValidation.isValidCVV(paymentDetails.getCvv())) {
            return false;
        }
        return true;
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
    
    public boolean isAnnualFeePaid(String email) {
        String query = "SELECT last_annual_fee_payment FROM registered_users WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                LocalDateTime lastPayment = rs.getObject("last_annual_fee_payment", LocalDateTime.class);
                if (lastPayment != null) {
                    return LocalDateTime.now().isBefore(lastPayment.plusYears(1));
                }
            }
            return false;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error checking annual fee status", e);
        }
    }
}