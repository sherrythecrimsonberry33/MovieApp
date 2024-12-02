

package frontend;

import backend.database.SeatBookingDAO;
import backend.database.CancellationResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import backend.actors.RegisteredUser;
import java.util.Optional;

public class TicketCancellationView {
    private Stage stage;
    private SeatBookingDAO seatBookingDAO;
    private RegisteredUser loggedInUser;

    public TicketCancellationView(RegisteredUser loggedInUser) {
        this.stage = new Stage();
        this.seatBookingDAO = new SeatBookingDAO();
        this.loggedInUser = loggedInUser;
    }

    public void show() {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: #000000;");

        // Title
        Label titleLabel = new Label("Ticket Cancellation");
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // User type indication
        Label userTypeLabel = new Label(loggedInUser != null ? 
            "Registered User - Full Refund Available" : 
            "Guest User - 15% Cancellation Fee Applies");
        userTypeLabel.setTextFill(Color.LIGHTGRAY);
        userTypeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        // Instruction Label for Ticket ID
        Label ticketIdLabel = new Label("Enter Ticket ID:");
        ticketIdLabel.setTextFill(Color.WHITE);
        ticketIdLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // Ticket ID TextField
        TextField ticketIdField = new TextField();
        ticketIdField.setPromptText("Enter your ticket ID");
        ticketIdField.setMaxWidth(250);
        ticketIdField.setStyle(
            "-fx-background-color: #333333;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #aaaaaa;"
        );

        VBox inputFields = new VBox(10);
        inputFields.getChildren().addAll(ticketIdLabel, ticketIdField);

        // Conditional email field for guest users
        TextField emailField = null;
        if (loggedInUser == null) {
            Label emailLabel = new Label("Enter Your Email Address:");
            emailLabel.setTextFill(Color.WHITE);
            emailLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

            emailField = new TextField();
            emailField.setPromptText("your.email@example.com");
            emailField.setMaxWidth(250);
            emailField.setStyle(
                "-fx-background-color: #333333;" +
                "-fx-text-fill: white;" +
                "-fx-prompt-text-fill: #aaaaaa;"
            );
            inputFields.getChildren().addAll(emailLabel, emailField);
        } else {
            Label emailInfo = new Label("Refund confirmation will be sent to your registered email");
            emailInfo.setTextFill(Color.LIGHTGRAY);
            emailInfo.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            inputFields.getChildren().add(emailInfo);
        }

        // Cancel Button
        Button cancelButton = new Button("Cancel Ticket");
        styleButton(cancelButton);

        // Message Text
        Text messageText = new Text();
        messageText.setFill(Color.LIGHTGRAY);
        messageText.setFont(Font.font("Arial", 14));

        // Final reference for lambda
        final TextField finalEmailField = emailField;

        // Action Handler
        cancelButton.setOnAction(e -> {
            String ticketId = ticketIdField.getText().trim();
            String email = loggedInUser != null ? loggedInUser.getEmail() : 
                          finalEmailField != null ? finalEmailField.getText().trim() : "";

            if (!validateInputs(ticketId, email, messageText)) {
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Cancellation");
            confirm.setHeaderText(null);
            String message = loggedInUser != null ?
                "You will receive a full refund for this cancellation." :
                "A 15% administrative fee will be deducted from your refund.";
            confirm.setContentText("Are you sure you want to cancel ticket " + ticketId + "?\n\n" + message);

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                CancellationResult cancellationResult = seatBookingDAO.cancelBooking(ticketId, email);
                handleCancellationResult(cancellationResult, messageText);
            }
        });

        mainLayout.getChildren().addAll(
            titleLabel,
            userTypeLabel,
            inputFields,
            cancelButton,
            messageText
        );

        Scene scene = new Scene(mainLayout, 500, loggedInUser == null ? 400 : 300);
        stage.setTitle("Ticket Cancellation");
        stage.setScene(scene);
        stage.show();
    }

    private boolean validateInputs(String ticketId, String email, Text messageText) {
        if (ticketId.isEmpty()) {
            messageText.setText("Please enter a Ticket ID.");
            messageText.setFill(Color.RED);
            return false;
        }

        if (loggedInUser == null && (email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$"))) {
            messageText.setText("Please enter a valid email address.");
            messageText.setFill(Color.RED);
            return false;
        }

        return true;
    }

    private void handleCancellationResult(CancellationResult result, Text messageText) {
        switch (result) {
            case SUCCESS:
                messageText.setText("Ticket cancelled successfully. Refund details sent to your email.");
                messageText.setFill(Color.GREEN);
                break;
            case SUCCESS_EMAIL_FAILED:
                messageText.setText("Ticket cancelled successfully, but email notification failed.");
                messageText.setFill(Color.ORANGE);
                break;
            case NO_BOOKING_FOUND:
                messageText.setText("No active booking found with the provided Ticket ID.");
                messageText.setFill(Color.RED);
                break;
            case CANCELLATION_NOT_ALLOWED:
                messageText.setText("Cancellation not allowed within 72 hours of showtime.");
                messageText.setFill(Color.RED);
                break;
            case ERROR:
                messageText.setText("An error occurred during cancellation. Please try again.");
                messageText.setFill(Color.RED);
                break;
        }
    }

    private void styleButton(Button button) {
        button.setStyle(
            "-fx-background-color: #e50914;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-background-radius: 4px;" +
            "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: #c40612;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 20;" +
                "-fx-background-radius: 4px;" +
                "-fx-cursor: hand;"
            )
        );

        button.setOnMouseExited(e -> 
            button.setStyle(
                "-fx-background-color: #e50914;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 20;" +
                "-fx-background-radius: 4px;" +
                "-fx-cursor: hand;"
            )
        );
    }
}