package backend.Entity.validation;

import java.time.YearMonth;
import java.util.regex.Pattern;

public class PaymentValidation {
    
    private static final Pattern CVV_PATTERN = Pattern.compile("\\d{3,4}");
    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("\\d{13,19}");


//validates credit card number using the Luhn algorithm

    public static boolean isValidCardNumber(String cardNumber){
        if(!CARD_NUMBER_PATTERN.matcher(cardNumber).matches()){
            return false;
        }
        return luhnCheck(cardNumber);
    }


//Performs the Luhn Alogorithm check

    private static boolean luhnCheck(String cardNumber){
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--){
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate){
                n *= 2;
                if(n > 9)
                    n = (n % 10 ) +1;
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }


//Validates the expiration date of the credit card

    public static boolean isValidExpirationDate(int month, int year){
        YearMonth current = YearMonth.now();
        YearMonth expiration = YearMonth.of(year, month);
        return expiration.isAfter(current) || expiration.equals(current);
    }

//Validates the CVV code
    public static boolean isValidCVV (String cvv){
        return CVV_PATTERN.matcher(cvv).matches();
    }

//Validates the payment amount (Making sure its a positive value)
    public static boolean isValidAmount(double amount){
        return amount > 0;
    }

}
