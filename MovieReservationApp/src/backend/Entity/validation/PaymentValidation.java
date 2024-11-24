// package backend.Entity.validation;

// import java.time.YearMonth;
// import java.util.regex.Pattern;

// public class PaymentValidation {
    
//     private static final Pattern CVV_PATTERN = Pattern.compile("\\d{3,4}");
//     private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("\\d{13,19}");


// //validates credit card number using the Luhn algorithm

//     public static boolean isValidCardNumber(String cardNumber){
//         if(!CARD_NUMBER_PATTERN.matcher(cardNumber).matches()){
//             return false;
//         }
//         return luhnCheck(cardNumber);
//     }


// //Performs the Luhn Alogorithm check

//     private static boolean luhnCheck(String cardNumber){
//         int sum = 0;
//         boolean alternate = false;
//         for (int i = cardNumber.length() - 1; i >= 0; i--){
//             int n = Integer.parseInt(cardNumber.substring(i, i + 1));
//             if (alternate){
//                 n *= 2;
//                 if(n > 9)
//                     n = (n % 10 ) +1;
//             }
//             sum += n;
//             alternate = !alternate;
//         }
//         return (sum % 10 == 0);
//     }


// //Validates the expiration date of the credit card

//     public static boolean isValidExpirationDate(int month, int year){
//         YearMonth current = YearMonth.now();
//         YearMonth expiration = YearMonth.of(year, month);
//         return expiration.isAfter(current) || expiration.equals(current);
//     }

// //Validates the CVV code
//     public static boolean isValidCVV (String cvv){
//         return CVV_PATTERN.matcher(cvv).matches();
//     }

// //Validates the payment amount (Making sure its a positive value)
//     public static boolean isValidAmount(double amount){
//         return amount > 0;
//     }

// }

package backend.Entity.validation;

import backend.Entity.PaymentDetails;

import java.time.YearMonth;
import java.util.regex.Pattern;
import java.util.HashSet;
import java.util.Set;

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
        // Check if card is blocked
        if (BLOCKED_CARDS.contains(cardNumber)) {
            return false;
        }
        
        if (!CARD_NUMBER_PATTERN.matcher(cardNumber).matches()) {
            return false;
        }
        return luhnCheck(cardNumber);
    }

    private static boolean luhnCheck(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9)
                    n = (n % 10) + 1;
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    public static boolean isValidExpirationDate(int month, int year) {
        if (month < 1 || month > 12) {
            return false;
        }
        
        YearMonth current = YearMonth.now();
        YearMonth expiration = YearMonth.of(year, month);
        
        // Card must not be expired and must not be more than 10 years in future
        return (expiration.isAfter(current) || expiration.equals(current)) 
            && expiration.isBefore(current.plusYears(10));
    }

    public static boolean isValidCVV(String cvv) {
        return CVV_PATTERN.matcher(cvv).matches();
    }

    public static boolean isValidAmount(double amount) {
        return amount > 0 && amount <= 10000; // Adding reasonable maximum limit
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
        return null; // no errors
    }
}