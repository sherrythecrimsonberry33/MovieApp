package frontend;

import backend.Entity.*;
import backend.Entity.receipt.GenerateReceipt;
import backend.Entity.receipt.SendEmail;
import backend.database.SeatBookingDAO;
import backend.actors.RegisteredUser;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.time.YearMonth;
import java.util.List;

public class PaymentView {
    private static final String DARK_BACKGROUND = "#1a1a1a";
    private static final String NETFLIX_RED = "#e50914";
    private static final String CARD_BACKGROUND = "#2a2a2a";
    
    private Stage stage;
    private MovieTimings movieTiming;
    private List<Seat> selectedSeats;
    private String userType;
    private SeatBookingDAO seatBookingDAO;
    private double totalAmount;
    
    // Form fields
    private TextField emailField;
    private TextField cardNumberField;
    private TextField expiryMonthField;
    private TextField expiryYearField;
    private TextField cvvField;
    private TextField cardHolderField;
    private ProgressIndicator loadingIndicator;
    private VBox mainContent;
    private VBox loadingContent;
    
    private RegisteredUser loggedInUser;
    private RadioButton useStoredCardRadio;
    private RadioButton newCardRadio;
    private VBox newCardForm;

    
    public PaymentView(MovieTimings movieTiming, List<Seat> selectedSeats, String userType, RegisteredUser loggedInUser) {
        this.stage = new Stage();
        this.movieTiming = movieTiming;
        this.selectedSeats = selectedSeats;
        this.userType = userType;
        this.loggedInUser = loggedInUser;
        this.seatBookingDAO = new SeatBookingDAO();
        this.totalAmount = calculateTotalAmount();
    }
    
    
    public void show() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + DARK_BACKGROUND + ";");
        
        // Main content
        mainContent = createMainContent();
        
        // Loading content
        loadingContent = createLoadingContent();
        loadingContent.setVisible(false);
        
        StackPane contentStack = new StackPane(mainContent, loadingContent);
        mainLayout.setCenter(contentStack);
        
        Scene scene = new Scene(mainLayout, 600, 800);
        stage.setTitle("Complete Payment");
        stage.setScene(scene);
        stage.show();
    }
    
    private VBox createMainContent() {
        VBox content = new VBox(20);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(30));
        
        // Booking summary
        VBox summaryBox = createSummarySection();
        
        // Payment form
        VBox paymentForm = createPaymentForm();
        
        // Total amount
        Label totalLabel = new Label(String.format("Total Amount: $%.2f", totalAmount));
        totalLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");
        
        // Complete payment button
        Button completeButton = new Button("Complete Payment");
        styleButton(completeButton);
        completeButton.setOnAction(e -> handlePayment());
        
        content.getChildren().addAll(summaryBox, paymentForm, totalLabel, completeButton);
        return content;
    }
    
    private VBox createSummarySection() {
        VBox summary = new VBox(10);
        summary.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" +
            "-fx-padding: 20px;" +
            "-fx-background-radius: 8px;"
        );
        
        Label movieTitle = new Label(movieTiming.getMovie().getTitle());
        movieTitle.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Label showTime = new Label(movieTiming.getFormattedMovieTimings());
        showTime.setStyle("-fx-font-size: 16px; -fx-text-fill: #cccccc;");
        
        Label seatsLabel = new Label("Selected Seats: " + formatSelectedSeats());
        seatsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #cccccc;");
        
        summary.getChildren().addAll(movieTitle, showTime, seatsLabel);
        return summary;
    }
    
    // In PaymentView.java
    private VBox createPaymentForm() {
        VBox form = new VBox(15);
        form.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" +
            "-fx-padding: 20px;" +
            "-fx-background-radius: 8px;"
        );

        if (userType.equals("Registered") && loggedInUser != null) {
            // Create radio buttons for payment options
            ToggleGroup paymentOptionGroup = new ToggleGroup();
            
            useStoredCardRadio = new RadioButton("Use Stored Card");
            useStoredCardRadio.setToggleGroup(paymentOptionGroup);
            useStoredCardRadio.setSelected(true);
            useStoredCardRadio.setStyle("-fx-text-fill: white;");
            
            newCardRadio = new RadioButton("Use New Card");
            newCardRadio.setToggleGroup(paymentOptionGroup);
            newCardRadio.setStyle("-fx-text-fill: white;");
            
            // Show masked stored card info
            PaymentDetails savedDetails = loggedInUser.getSavedPaymentDetails();
            String maskedCard = String.format("Card ending in %s", 
                savedDetails.getCardNumber().substring(savedDetails.getCardNumber().length() - 4));
            Label savedCardInfo = new Label(maskedCard);
            savedCardInfo.setStyle("-fx-text-fill: #cccccc;");
            
            // Create new card form container
            newCardForm = new VBox(10);
            createPaymentFields(newCardForm);
            newCardForm.setVisible(false);
            
            // Add listener to toggle visibility
            paymentOptionGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
                newCardForm.setVisible(newCardRadio.isSelected());
            });
            
            form.getChildren().addAll(
                new Label("Payment Options:"),
                useStoredCardRadio,
                savedCardInfo,
                newCardRadio,
                newCardForm
            );
        } else {
            // For guest users
            Label emailLabel = new Label("Email Address");
            emailLabel.setStyle("-fx-text-fill: white;");
            emailField = createStyledTextField("Enter your email");
            createPaymentFields(form);
            form.getChildren().addAll(emailLabel, emailField);
        }
        
        return form;
    }
        
    private void createPaymentFields(VBox container) {
        // Payment details fields
        Label cardLabel = new Label("Card Number");
        cardLabel.setStyle("-fx-text-fill: white;");
        cardNumberField = createStyledTextField("Enter card number");
        
        HBox expiryBox = new HBox(10);
        expiryMonthField = createStyledTextField("MM");
        expiryYearField = createStyledTextField("YYYY");
        expiryBox.getChildren().addAll(expiryMonthField, expiryYearField);
        
        Label cvvLabel = new Label("CVV");
        cvvLabel.setStyle("-fx-text-fill: white;");
        cvvField = createStyledTextField("CVV");
        
        Label holderLabel = new Label("Card Holder Name");
        holderLabel.setStyle("-fx-text-fill: white;");
        cardHolderField = createStyledTextField("Enter card holder name");
        
        container.getChildren().addAll(
            cardLabel, cardNumberField,
            new Label("Expiry Date"), expiryBox,
            cvvLabel, cvvField,
            holderLabel, cardHolderField
        );
    }
    
    private VBox createLoadingContent() {
        VBox loading = new VBox(20);
        loading.setAlignment(Pos.CENTER);
        
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setStyle("-fx-progress-color: " + NETFLIX_RED + ";");
        
        Label loadingLabel = new Label("Processing payment...");
        loadingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        
        loading.getChildren().addAll(loadingIndicator, loadingLabel);
        return loading;
    }

    private void handlePayment() {
    if (!validateForm()) {
        return;
    }
    
    showLoading(true);


    Task<Boolean> paymentTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                PaymentDetails paymentDetails;
                
                try {
                    // Handle payment details based on user type
                    if (userType.equals("Registered") && loggedInUser != null) {
                        if (useStoredCardRadio.isSelected()) {
                            paymentDetails = loggedInUser.getSavedPaymentDetails();
                            // Validate stored card details
                            if (!validateStoredCard(paymentDetails)) {
                                throw new IllegalStateException("Stored card details are invalid or expired");
                            }
                        } else {
                            // Using new card for registered user
                            paymentDetails = createPaymentDetails();
                        }
                    } else {
                        // Guest user payment
                        paymentDetails = createPaymentDetails();
                    }

                    // Validate payment amount
                    if (totalAmount <= 0 || totalAmount > 10000) {
                        throw new IllegalStateException("Invalid payment amount");
                    }

                    // Process the payment
                    Transaction transaction = Transaction.processPayment(paymentDetails, totalAmount);

                    if (!"APPROVED".equals(transaction.getStatus())) {
                        throw new IllegalStateException("Payment was declined: " + transaction.getStatus());
                    }

                    // Get correct email
                    String userEmail = userType.equals("Guest") ? 
                        emailField.getText().trim() : 
                        loggedInUser.getEmail();

                    // Validate email for guest users
                    if (userType.equals("Guest") && !isValidEmail(userEmail)) {
                        throw new IllegalStateException("Invalid email address");
                    }

                    // Create ticket info
                    TicketInfo ticketInfo = new TicketInfo(
                        movieTiming, 
                        selectedSeats, 
                        totalAmount, 
                        userEmail, 
                        transaction
                    );

                    double pricePerSeat = movieTiming.getTicketPrice();

                    // Book the seats
                    boolean seatsBooked = seatBookingDAO.bookSeats(
                        movieTiming.getId(),
                        selectedSeats,
                        ticketInfo.getTicketId(),
                        pricePerSeat
                    );

                    if (!seatsBooked) {
                        throw new IllegalStateException("Failed to book seats. Please try again.");
                    }

                    try {
                        // Generate receipt and send email
                        GenerateReceipt receipt = new GenerateReceipt(
                            ticketInfo,
                            pricePerSeat,
                            TheatreInfo.getInstance()
                        );

                        SendEmail emailSender = new SendEmail();
                        emailSender.sendTicketAndReceipt(userEmail, ticketInfo, receipt);
                    } catch (Exception e) {
                        System.err.println("Failed to send email: " + e.getMessage());
                        Platform.runLater(() -> {
                            showWarning("Booking successful but failed to send email confirmation. " +
                                      "Please contact support if you don't receive your tickets.");
                        });
                    }

                    return true;

                } catch (NumberFormatException e) {
                    Platform.runLater(() -> showError("Invalid card expiry date format"));
                    return false;
                } catch (IllegalStateException e) {
                    Platform.runLater(() -> showError(e.getMessage()));
                    return false;
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Payment processing error: " + e.getMessage()));
                    return false;
                }
            }
        };
        
        paymentTask.setOnSucceeded(e -> {
            showLoading(false);
            if (paymentTask.getValue()) {
                showSuccessAndClose();
            } else {
                showError("Payment failed. Please try again.");
            }
        });
        
        paymentTask.setOnFailed(e -> {
            showLoading(false);
            showError("An error occurred: " + paymentTask.getException().getMessage());
        });
        
        new Thread(paymentTask).start();
    }

    private boolean validateForm() {
        if (userType.equals("Registered") && loggedInUser != null && useStoredCardRadio.isSelected()) {
            PaymentDetails storedCard = loggedInUser.getSavedPaymentDetails();
            if (!validateStoredCard(storedCard)) {
                showError("Stored card is invalid or expired. Please use a different card.");
                return false;
            }
            return true;
        }
        
        // Validate new card details
        if (cardNumberField.getText().isEmpty() || 
            !PaymentValidation.isValidCardNumber(cardNumberField.getText().trim())) {
            showError("Please enter a valid card number.");
            return false;
        }
        
        try {
            int month = Integer.parseInt(expiryMonthField.getText().trim());
            int year = Integer.parseInt(expiryYearField.getText().trim());
            if (month < 1 || month > 12 || year < 2024) {
                throw new NumberFormatException();
            }
            
            // Check if card is expired
            YearMonth expiry = YearMonth.of(year, month);
            if (expiry.isBefore(YearMonth.now())) {
                showError("Card has expired.");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter valid expiry date.");
            return false;
        }
        
        if (cvvField.getText().isEmpty() || !PaymentValidation.isValidCVV(cvvField.getText().trim())) {
            showError("Please enter a valid CVV.");
            return false;
        }
        
        if (cardHolderField.getText().isEmpty() || 
            !PaymentValidation.isValidCardholderName(cardHolderField.getText().trim())) {
            showError("Please enter a valid card holder name (letters and spaces only).");
            return false;
        }

        if (userType.equals("Guest") && !isValidEmail(emailField.getText())) {
            showError("Please enter a valid email address.");
            return false;
        }
        
        return true;
    }
    private boolean validateStoredCard(PaymentDetails card) {
        if (card == null) {
            return false;
        }

        try {
            // Check expiry date
            YearMonth expiry = YearMonth.of(card.getExpiryYear(), card.getExpiryMonth());
            if (expiry.isBefore(YearMonth.now())) {
                return false;
            }

            // Validate card details using existing PaymentValidation method
            String validationError = PaymentValidation.getValidationError(card, totalAmount);
            return validationError == null; // Valid if no error message

        } catch (Exception e) {
            return false;
        }
    }

    private PaymentDetails createPaymentDetails() throws IllegalStateException {
        try {
            String cardNumber = cardNumberField.getText().replaceAll("[\\s-]", "");
            int month = Integer.parseInt(expiryMonthField.getText().trim());
            int year = Integer.parseInt(expiryYearField.getText().trim());

            PaymentDetails details = new PaymentDetails(
                cardNumber,
                month,
                year,
                cvvField.getText().trim(),
                cardHolderField.getText().trim()
            );

            // Validate with amount
            String validationError = PaymentValidation.getValidationError(details, totalAmount);
            if (validationError != null) {
                throw new IllegalStateException(validationError);
            }

            return details;
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid expiry date format");
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && 
               !email.trim().isEmpty() &&
               email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    private void showLoading(boolean show) {
        Platform.runLater(() -> {
            mainContent.setVisible(!show);
            loadingContent.setVisible(show);
        });
    }
    
    private void showSuccessAndClose() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Payment successful! Your tickets and receipt have been sent to your email.");
        alert.showAndWait();
        stage.close();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    
    // Task<Boolean> paymentTask = new Task<>() {
    //     @Override
    //     protected Boolean call() throws Exception {
    //         PaymentDetails paymentDetails;
            
    //         if (userType.equals("Registered") && loggedInUser != null) {
    //             if (useStoredCardRadio.isSelected()) {
    //                 paymentDetails = loggedInUser.getSavedPaymentDetails();
    //             } else {
    //                 paymentDetails = new PaymentDetails(
    //                     cardNumberField.getText(),
    //                     Integer.parseInt(expiryMonthField.getText()),
    //                     Integer.parseInt(expiryYearField.getText()),
    //                     cvvField.getText(),
    //                     cardHolderField.getText()
    //                 );
    //             }
    //         } else {
    //             paymentDetails = new PaymentDetails(
    //                 cardNumberField.getText(),
    //                 Integer.parseInt(expiryMonthField.getText()),
    //                 Integer.parseInt(expiryYearField.getText()),
    //                 cvvField.getText(),
    //                 cardHolderField.getText()
    //             );
    //         }
            
    //         Transaction transaction = Transaction.processPayment(paymentDetails, totalAmount);
            
    //         if (transaction.getStatus().equals("APPROVED")) {
    //             String userEmail = userType.equals("Guest") ? emailField.getText() : 
    //                 loggedInUser.getEmail();
                
    //             // Create ticket first to get the ID
    //             TicketInfo ticketInfo = new TicketInfo(
    //                 movieTiming, selectedSeats, totalAmount, userEmail, transaction);
                
    //             double pricePerSeat = movieTiming.getTicketPrice();
    //             // Book seats with ticket ID
    //             boolean seatsBooked = seatBookingDAO.bookSeats(
    //                 movieTiming.getId(), selectedSeats, ticketInfo.getTicketId(), pricePerSeat);
                
    //             if (seatsBooked) {
    //                 // Generate receipt and send email
    //                 GenerateReceipt receipt = new GenerateReceipt(
    //                     ticketInfo, pricePerSeat, 
    //                     TheatreInfo.getInstance());
                    
    //                 SendEmail emailSender = new SendEmail();
    //                 emailSender.sendTicketAndReceipt(userEmail, ticketInfo, receipt);
                    
    //                 return true;
    //             }
    //         }
    //         return false;
    //     }
    // };
    
    //     paymentTask.setOnSucceeded(e -> {
    //         showLoading(false);
    //         if (paymentTask.getValue()) {
    //             showSuccessAndClose();
    //         } else {
    //             showError("Payment failed. Please try again.");
    //         }
    //     });
        
    //     paymentTask.setOnFailed(e -> {
    //         showLoading(false);
    //         showError("An error occurred: " + paymentTask.getException().getMessage());
    //     });
        
    //     new Thread(paymentTask).start();
    // }
    
    // private boolean validateForm() {
    //     if (userType.equals("Registered") && loggedInUser != null && useStoredCardRadio.isSelected()) {
    //         return true; // Skip validation for stored card
    //     }
        
    //     // Validate new card details
    //     if (cardNumberField.getText().isEmpty() || !cardNumberField.getText().matches("\\d{13,19}")) {
    //         showError("Please enter a valid card number.");
    //         return false;
    //     }
        
    //     try {
    //         int month = Integer.parseInt(expiryMonthField.getText());
    //         int year = Integer.parseInt(expiryYearField.getText());
    //         if (month < 1 || month > 12 || year < 2024) {
    //             throw new NumberFormatException();
    //         }
    //     } catch (NumberFormatException e) {
    //         showError("Please enter valid expiry date.");
    //         return false;
    //     }
        
    //     if (cvvField.getText().isEmpty() || !cvvField.getText().matches("\\d{3,4}")) {
    //         showError("Please enter a valid CVV.");
    //         return false;
    //     }
        
    //     if (cardHolderField.getText().isEmpty()) {
    //         showError("Please enter card holder name.");
    //         return false;
    //     }

    //     if (userType.equals("Guest") && (emailField.getText().isEmpty() || 
    //         !emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$"))) {
    //         showError("Please enter a valid email address.");
    //         return false;
    //     }
        
    //     return true;
    // }

    
    // private void showLoading(boolean show) {
    //     Platform.runLater(() -> {
    //         mainContent.setVisible(!show);
    //         loadingContent.setVisible(show);
    //     });
    // }
    
    // private void showSuccessAndClose() {
    //     Alert alert = new Alert(Alert.AlertType.INFORMATION);
    //     alert.setTitle("Success");
    //     alert.setHeaderText(null);
    //     alert.setContentText("Payment successful! Your tickets and receipt have been sent to your email.");
    //     alert.showAndWait();
    //     stage.close();
    // }
    
    // private void showError(String message) {
    //     Alert alert = new Alert(Alert.AlertType.ERROR);
    //     alert.setTitle("Error");
    //     alert.setHeaderText(null);
    //     alert.setContentText(message);
    //     alert.showAndWait();
    // }



    
    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle(
            "-fx-background-color: #3a3a3a;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #808080;" +
            "-fx-padding: 8px;"
        );
        return field;
    }
    
    private void styleButton(Button button) {
        button.setStyle(
            "-fx-background-color: " + NETFLIX_RED + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 30;" +
            "-fx-font-size: 14px;" +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: derive(" + NETFLIX_RED + ", -10%);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 30;" +
            "-fx-font-size: 14px;" +
            "-fx-cursor: hand;"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: " + NETFLIX_RED + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 30;" +
            "-fx-font-size: 14px;" +
            "-fx-cursor: hand;"
        ));
    }
    
    private double calculateTotalAmount() {
        return movieTiming.getTicketPrice() * selectedSeats.size();
    }
    
    private String formatSelectedSeats() {
        StringBuilder seats = new StringBuilder();
        for (Seat seat : selectedSeats) {
            if (seats.length() > 0) seats.append(", ");
            seats.append(String.format("%c%d", 
                (char)('A' + seat.getRow()), 
                seat.getColumn() + 1));
        }
        return seats.toString();
    }
}