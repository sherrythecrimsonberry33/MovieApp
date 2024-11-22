package backend.actions.receipt;

import backend.actions.TicketInfo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.stream.Collectors;

public class GenerateReceipt {
    private final String receiptId;
    private final TicketInfo ticketInfo;
    private final double ticketPrice;
    private final int numberOfSeats;
    private final double totalAmount;
    private final LocalDateTime purchaseDateTime;

    public GenerateReceipt(TicketInfo ticketInfo, double ticketPrice) {
        this.receiptId = "RCPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.ticketInfo = ticketInfo;
        this.ticketPrice = ticketPrice;
        this.numberOfSeats = ticketInfo.getSeats().size();
        this.totalAmount = calculateTotal();
        this.purchaseDateTime = LocalDateTime.now();
    }

    private double calculateTotal() {
        return ticketPrice * numberOfSeats;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public LocalDateTime getPurchaseDateTime() {
        return purchaseDateTime;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String generateReceiptContent() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("=== AcmePlex Theaters ===\n\n");
        receipt.append(String.format("Receipt ID: %s\n", receiptId));
        receipt.append(String.format("Purchase Date: %s\n", 
            purchaseDateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))));
        receipt.append("------------------------\n");
        receipt.append(String.format("Movie: %s\n", ticketInfo.getMovieName()));
        receipt.append(String.format("Theater: %s\n", ticketInfo.getMovieHallName()));
        receipt.append(String.format("Show Time: %s\n", ticketInfo.getShowTiming()));
        receipt.append(String.format("Seats: %s\n", 
            String.join(", ", ticketInfo.getSeats().stream()
                .map(seat -> seat.getSeatLabel())
                .collect(Collectors.toList()))));
        receipt.append("------------------------\n");
        receipt.append(String.format("Price per ticket: $%.2f\n", ticketPrice));
        receipt.append(String.format("Number of seats: %d\n", numberOfSeats));
        receipt.append(String.format("Total Amount: $%.2f\n", totalAmount));
        receipt.append("\nThank you for choosing AcmePlex Theaters!");
        
        return receipt.toString();
    }
}