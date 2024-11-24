package backend.actors;

import backend.Entity.PaymentDetails;
import backend.Entity.Transaction;
import backend.Entity.validation.PaymentValidation;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

// public class Bank {
//     private final String bankName;
//     private Map<String, Transaction> transactionLog;

//     public Bank(String bankName) {
//         this.bankName = bankName;
//         this.transactionLog = new HashMap<>();
//     }

//     // Process a payment request
//     public Transaction processPayment(PaymentDetails paymentDetails, double amount) {
//         // Validate payment details
//         if (!validatePaymentDetails(paymentDetails)) {
//             return new Transaction(generateTransactionId(), "DECLINED", amount, LocalDateTime.now());
//         }

//         // Simulate bank response
//         String status = simulateBankResponse();

//         // Create transaction record
//         Transaction transaction = new Transaction(generateTransactionId(), status, amount, LocalDateTime.now());
//         logTransaction(transaction);

//         return transaction;
//     }

//     // Process a refund request
//     public Transaction processRefund(String transactionId, double amount) {
//         Transaction originalTransaction = transactionLog.get(transactionId);

//         if (originalTransaction == null || !originalTransaction.getStatus().equals("APPROVED")) {
//             return new Transaction(generateTransactionId(), "REFUND_DECLINED", amount, LocalDateTime.now());
//         }

//         // Simulate bank response for refund
//         String status = simulateBankResponse();

//         // Create refund transaction record
//         Transaction refundTransaction = new Transaction(generateTransactionId(), status, -amount, LocalDateTime.now());
//         logTransaction(refundTransaction);

//         return refundTransaction;
//     }

//     // Validates payment details
//     private boolean validatePaymentDetails(PaymentDetails paymentDetails) {
//         // Use PaymentValidation methods
//         boolean isValidCardNumber = PaymentValidation.isValidCardNumber(paymentDetails.getCardNumber());
//         boolean isValidExpiryDate = PaymentValidation.isValidExpirationDate(paymentDetails.getExpiryMonth(), paymentDetails.getExpiryYear());
//         boolean isValidCVV = PaymentValidation.isValidCVV(paymentDetails.getCvv());

//         return isValidCardNumber && isValidExpiryDate && isValidCVV;
//     }

//     // Simulates a bank's response to a payment or refund request
//     private String simulateBankResponse() {
//         Random rand = new Random();
//         return rand.nextBoolean() ? "APPROVED" : "DECLINED";
//     }

//     // Logs the transaction
//     private void logTransaction(Transaction transaction) {
//         transactionLog.put(transaction.getTransactionId(), transaction);
//     }

//     // Retrieves the status of a transaction
//     public String getTransactionStatus(String transactionId) {
//         Transaction transaction = transactionLog.get(transactionId);
//         return transaction != null ? transaction.getStatus() : "UNKNOWN_TRANSACTION";
//     }

//     // Generates a unique transaction ID
//     private String generateTransactionId() {
//         return UUID.randomUUID().toString();
//     }


//     public String getBankName() {
//         return bankName;
//     }
// }



public class Bank {
    private final String bankName;
    private Map<String, Transaction> transactionLog;

    public Bank(String bankName) {
        this.bankName = bankName;
        this.transactionLog = new HashMap<>();
    }

    public Transaction processPayment(PaymentDetails paymentDetails, double amount) {
        // First validate amount
        if (!PaymentValidation.isValidAmount(amount)) {
            return new Transaction(generateTransactionId(), "DECLINED", amount, LocalDateTime.now());
        }

        // Then validate payment details
        if (!validatePaymentDetails(paymentDetails)) {
            return new Transaction(generateTransactionId(), "DECLINED", amount, LocalDateTime.now());
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

    private String simulateBankResponse() {
        Random rand = new Random();
        return rand.nextBoolean() ? "APPROVED" : "DECLINED";
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