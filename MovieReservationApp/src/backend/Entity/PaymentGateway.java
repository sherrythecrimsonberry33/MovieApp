package backend.Entity;

import java.time.LocalDateTime;

public class PaymentGateway {
    private String transactionId;
    private String status;
    private double amount;
    private LocalDateTime timestamp;


    //Constructor
    public PaymentGateway (String transactionId, String status, double amount, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.status = status;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    // Getters and Setters

    public String getTransactionId() {
        return transactionId;
    }

    public String getStatus() {
        return status;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
