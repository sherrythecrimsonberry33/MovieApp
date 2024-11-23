package backend.Entity;

public class PaymentDetails {
    private String cardNumber;
    private int expiryMonth;
    private int expiryYear;
    private String cvv;
    private String cardHolderName;

    //constructor
    public PaymentDetails(String cardNumber, int expiryMonth, int expiryYear, String cvv, String cardHolderNname){
        this.cardNumber = cardNumber;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.cvv = cvv;
        this.cardHolderName = cardHolderNname;
    }
    
    //Getters and Setters
    public String getCardNumber() {
        return cardNumber;
    }

    public int getExpiryMonth() {
        return expiryMonth;
    }

    public int getExpiryYear() {
        return expiryYear;
    }

    public String getCvv() {
        return cvv;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }
}
