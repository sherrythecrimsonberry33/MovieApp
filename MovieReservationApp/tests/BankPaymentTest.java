package MovieReservationApp.tests;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import backend.Entity.PaymentDetails;
import backend.Entity.Transaction;
import backend.actors.Bank;
import java.time.LocalDateTime;

public class BankPaymentTest {
    private Bank bank;
    private PaymentDetails validPaymentDetails;
    private PaymentDetails invalidPaymentDetails;
    
    @BeforeEach
    void setUp() {
        bank = new Bank("Test Bank");
        
        // Set up valid payment details
        validPaymentDetails = new PaymentDetails(
            "4532756279624064",  // Valid card number passing Luhn check
            12,                  // Valid month
            2025,               // Valid future year
            "123",              // Valid CVV
            "John Doe"          // Valid name
        );
        
        // Set up invalid payment details
        invalidPaymentDetails = new PaymentDetails(
            "1234567812345678",  // Invalid card number
            13,                  // Invalid month
            2020,               // Expired year
            "12",               // Invalid CVV
            ""                  // Invalid name
        );
    }

    @Test
    @DisplayName("Process valid payment should succeed")
    void testValidPaymentProcessing() {
        double amount = 100.00;
        Transaction transaction = bank.processPayment(validPaymentDetails, amount);
        
        assertNotNull(transaction, "Transaction should not be null");
        assertNotNull(transaction.getTransactionId(), "Transaction ID should not be null");
        assertEquals(amount, transaction.getAmount(), "Amount should match");
        assertTrue(transaction.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)), 
                  "Timestamp should be recent");
        
        // Status could be either APPROVED or DECLINED due to random simulation
        assertTrue(transaction.getStatus().equals("APPROVED") || 
                  transaction.getStatus().equals("DECLINED"),
                  "Status should be either APPROVED or DECLINED");
    }

    @Test
    @DisplayName("Process invalid payment should be declined")
    void testInvalidPaymentProcessing() {
        double amount = 100.00;
        Transaction transaction = bank.processPayment(invalidPaymentDetails, amount);
        
        assertNotNull(transaction, "Transaction should not be null");
        assertEquals("DECLINED", transaction.getStatus(), "Status should be DECLINED");
        assertEquals(amount, transaction.getAmount(), "Amount should match");
    }

    @Test
    @DisplayName("Process refund for valid transaction")
    void testValidRefundProcessing() {
        // First create a successful transaction
        double amount = 100.00;
        Transaction originalTransaction = bank.processPayment(validPaymentDetails, amount);
        
        // If original transaction was approved, test refund
        if (originalTransaction.getStatus().equals("APPROVED")) {
            Transaction refundTransaction = bank.processRefund(
                originalTransaction.getTransactionId(), 
                amount
            );
            
            assertNotNull(refundTransaction, "Refund transaction should not be null");
            assertEquals(-amount, refundTransaction.getAmount(), 
                        "Refund amount should be negative");
        }
    }

    @Test
    @DisplayName("Process refund for non-existent transaction")
    void testInvalidRefundProcessing() {
        double amount = 100.00;
        Transaction refundTransaction = bank.processRefund("non-existent-id", amount);
        
        assertNotNull(refundTransaction, "Refund transaction should not be null");
        assertEquals("REFUND_DECLINED", refundTransaction.getStatus(), 
                    "Status should be REFUND_DECLINED");
    }

    @Test
    @DisplayName("Transaction logging and retrieval")
    void testTransactionLogging() {
        double amount = 100.00;
        Transaction transaction = bank.processPayment(validPaymentDetails, amount);
        
        String status = bank.getTransactionStatus(transaction.getTransactionId());
        assertEquals(transaction.getStatus(), status, 
                    "Retrieved status should match transaction status");
    }

    @Test
    @DisplayName("Zero amount payment should be declined")
    void testZeroAmountPayment() {
        double amount = 0.00;
        Transaction transaction = bank.processPayment(validPaymentDetails, amount);
        
        assertEquals("DECLINED", transaction.getStatus(), 
                    "Zero amount transaction should be declined");
    }

    @Test
    @DisplayName("Negative amount payment should be declined")
    void testNegativeAmountPayment() {
        double amount = -50.00;
        Transaction transaction = bank.processPayment(validPaymentDetails, amount);
        
        assertEquals("DECLINED", transaction.getStatus(), 
                    "Negative amount transaction should be declined");
    }

    @Test
    @DisplayName("Test non-existent transaction status")
    void testNonExistentTransactionStatus() {
        String status = bank.getTransactionStatus("non-existent-id");
        assertEquals("UNKNOWN_TRANSACTION", status, 
                    "Non-existent transaction should return UNKNOWN_TRANSACTION");
    }
}