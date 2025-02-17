package backend.Entity;

import backend.actors.Bank;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private final String transactionId;
    private final PaymentDetails paymentDetails;
    private final String status;
    private final double amount;
    private final LocalDateTime timestamp;

    // Constructor for new transactions with payment details
    public Transaction(PaymentDetails paymentDetails, String status, double amount) {
        this.transactionId = UUID.randomUUID().toString();
        this.paymentDetails = paymentDetails;
        this.status = status;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for refunds or when payment details aren't needed
    public Transaction(String transactionId, String status, double amount, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.paymentDetails = null;  // No payment details for refunds/declined transactions
        this.status = status;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public static Transaction processPayment(PaymentDetails paymentDetails, double amount) {
        Bank bank = new Bank("AcmePlex Bank");
        return bank.processPayment(paymentDetails, amount);
    }

    public static Transaction processRefund(String originalTransactionId, double amount) {
        Bank bank = new Bank("AcmePlex Bank");
        return bank.processRefund(originalTransactionId, amount);
    }

    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public PaymentDetails getPaymentDetails() {
        return paymentDetails;
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
