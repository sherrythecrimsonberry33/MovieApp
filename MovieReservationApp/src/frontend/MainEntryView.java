package frontend;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;

public class MainEntryView extends Application {
    private static final String DARK_BACKGROUND = "#1a1a1a";
    private static final String NETFLIX_RED = "#e50914";
    private static final String LOGO_PATH = "MovieReservationApp/assets/APlogo.png";
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Welcome to AcmePlex");

        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + DARK_BACKGROUND + ";");

        VBox content = new VBox(30);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));

        // Logo
        try {
            ImageView logo = new ImageView(new Image("file:" + LOGO_PATH));
            logo.setFitHeight(120);
            logo.setPreserveRatio(true);
            content.getChildren().add(logo);
        } catch (Exception e) {
            System.err.println("Failed to load logo: " + e.getMessage());
        }

        // Guest Button
        Button guestButton = createStyledButton("Proceed as Guest");
        guestButton.setOnAction(e -> proceedAsGuest());

        // Registered User Button
        Button registeredUserButton = createStyledButton("Registered User Login");
        registeredUserButton.setOnAction(e -> showLoginView());

        content.getChildren().addAll(guestButton, registeredUserButton);
        mainLayout.setCenter(content);

        Scene scene = new Scene(mainLayout);
        primaryStage.setScene(scene);

        // Enable full-screen mode
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: " + NETFLIX_RED + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 15 50;" +
            "-fx-font-size: 16px;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: derive(" + NETFLIX_RED + ", -10%);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 15 50;" +
            "-fx-font-size: 16px;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: " + NETFLIX_RED + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 15 50;" +
            "-fx-font-size: 16px;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        ));

        return button;
    }

    private void proceedAsGuest() {
        new MovieListingView().start(new Stage());
        primaryStage.close();
    }

    private void showLoginView() {
        new LoginView(primaryStage).show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}