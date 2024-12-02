


package backend.Entity;

import java.time.YearMonth;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class PaymentValidation {
    private static final Pattern CVV_PATTERN = Pattern.compile("\\d{3,4}");
    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("\\d{13,19}");
    private static final Pattern CARDHOLDER_NAME_PATTERN = Pattern.compile("^[a-zA-Z ]{2,50}$");
    private static final Set<String> BLOCKED_CARDS = new HashSet<>();

    // Initialize some test blocked cards
    static {
        BLOCKED_CARDS.add("4111111111111111"); // Test blocked card
    }

    public static boolean isValidCardNumber(String cardNumber) {
        // Remove any spaces or dashes from the card number
        cardNumber = cardNumber.replaceAll("[\\s-]", "");

        // Check if card is blocked
        if (BLOCKED_CARDS.contains(cardNumber)) {
            return false;
        }

        // Check if the card number matches the pattern
        if (!CARD_NUMBER_PATTERN.matcher(cardNumber).matches()) {
            return false;
        }

        // Optionally, check for valid card prefixes (BIN ranges)
        return hasValidPrefix(cardNumber);

    }

    // Optional method to check for valid card prefixes
    private static boolean hasValidPrefix(String cardNumber) {
        return cardNumber.startsWith("4") ||                     // Visa
               (cardNumber.startsWith("51") ||                   // MasterCard
                cardNumber.startsWith("52") ||
                cardNumber.startsWith("53") ||
                cardNumber.startsWith("54") ||
                cardNumber.startsWith("55")) ||
               cardNumber.startsWith("34") || cardNumber.startsWith("37"); // American Express
               // Add other card prefixes as needed
    }

    // Removed the luhnCheck method

    public static boolean isValidExpirationDate(int month, int year) {
        if (month < 1 || month > 12) {
            return false;
        }

        YearMonth current = YearMonth.now();
        YearMonth expiration = YearMonth.of(year, month);

        // Card must not be expired and not more than 10 years in the future
        return (expiration.isAfter(current) || expiration.equals(current))
                && expiration.isBefore(current.plusYears(10));
    }

    public static boolean isValidCVV(String cvv) {
        return CVV_PATTERN.matcher(cvv).matches();
    }

    public static boolean isValidAmount(double amount) {
        return amount > 0 && amount <= 10000; // Reasonable maximum limit
    }

    public static boolean isValidCardholderName(String name) {
        return name != null && CARDHOLDER_NAME_PATTERN.matcher(name).matches();
    }

    public static String getValidationError(PaymentDetails payment, double amount) {
        if (!isValidCardNumber(payment.getCardNumber())) {
            return "Invalid card number";
        }
        if (!isValidExpirationDate(payment.getExpiryMonth(), payment.getExpiryYear())) {
            return "Card is expired or has invalid expiration date";
        }
        if (!isValidCVV(payment.getCvv())) {
            return "Invalid CVV";
        }
        if (!isValidAmount(amount)) {
            return "Invalid payment amount";
        }
        if (!isValidCardholderName(payment.getCardHolderName())) {
            return "Invalid cardholder name";
        }
        return null; // No errors
    }
}
