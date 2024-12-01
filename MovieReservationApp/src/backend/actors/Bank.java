package backend.actors;

import backend.Entity.PaymentDetails;
import backend.Entity.PaymentValidation;
import backend.Entity.Transaction;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Bank {
    private final String bankName;
    private Map<String, Transaction> transactionLog;
    private static final double APPROVAL_RATE = 0.8; 

    public Bank(String bankName) {
        this.bankName = bankName;
        this.transactionLog = new HashMap<>();
    }

    public Transaction processPayment(PaymentDetails paymentDetails, double amount) {
        // First validate amount
        if (!PaymentValidation.isValidAmount(amount)) {
            return new Transaction(generateTransactionId(), "DECLINED_INVALID_AMOUNT", amount, LocalDateTime.now());
        }

        // Then validate payment details
        if (!validatePaymentDetails(paymentDetails)) {
            return new Transaction(generateTransactionId(), "DECLINED_INVALID_DETAILS", amount, LocalDateTime.now());
        }

        // Simulate bank response
        String status = simulateBankResponse();

        // Create transaction record
        Transaction transaction = new Transaction(paymentDetails, status, amount);
        logTransaction(transaction);

        return transaction;
    }

    public Transaction processRefund(String transactionId, double amount) {
        Transaction originalTransaction = transactionLog.get(transactionId);

        // Check if original transaction exists and was approved
        if (originalTransaction == null || !originalTransaction.getStatus().equals("APPROVED")) {
            return new Transaction(generateTransactionId(), "REFUND_DECLINED", amount, LocalDateTime.now());
        }

        // Validate refund amount
        if (Math.abs(amount) > Math.abs(originalTransaction.getAmount())) {
            return new Transaction(generateTransactionId(), "REFUND_DECLINED", amount, LocalDateTime.now());
        }

        // Simulate bank response for refund
        String status = simulateBankResponse();

        // Create refund transaction record
        Transaction refundTransaction = new Transaction(generateTransactionId(), status, -Math.abs(amount), LocalDateTime.now());
        logTransaction(refundTransaction);

        return refundTransaction;
    }

    private boolean validatePaymentDetails(PaymentDetails paymentDetails) {
        return PaymentValidation.isValidCardNumber(paymentDetails.getCardNumber()) &&
               PaymentValidation.isValidExpirationDate(paymentDetails.getExpiryMonth(), paymentDetails.getExpiryYear()) &&
               PaymentValidation.isValidCVV(paymentDetails.getCvv());
    }

    // private String simulateBankResponse() {
    //     Random rand = new Random();
    //     return rand.nextBoolean() ? "APPROVED" : "DECLINED";
    // }

    private String simulateBankResponse() {
        Random rand = new Random();
        
        // Higher chance of approval but still maintains possibility of decline
        if (rand.nextDouble() < APPROVAL_RATE) {
            return "APPROVED";
        } else {
            // Simulate different decline reasons
            String[] declineReasons = {
                "BANK SERVER_ERROR",
                "PAYMENT_GATEWAY_ERROR",
                "CARD HAS BEEN DECLINED",
                "DECLINED_SECURITY_CODE"
            };
            return declineReasons[rand.nextInt(declineReasons.length)];
        }
    }

    private void logTransaction(Transaction transaction) {
        transactionLog.put(transaction.getTransactionId(), transaction);
    }

    public String getTransactionStatus(String transactionId) {
        Transaction transaction = transactionLog.get(transactionId);
        return transaction != null ? transaction.getStatus() : "UNKNOWN_TRANSACTION";
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

    public String getBankName() {
        return bankName;
    }
}