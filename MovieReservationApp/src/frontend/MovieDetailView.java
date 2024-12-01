package frontend;

import backend.Entity.Movie;
import backend.actors.RegisteredUser;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;


public class MovieDetailView {
    private static final String DARK_BACKGROUND = "#1a1a1a";
    private static final String NETFLIX_RED = "#e50914";
    private static final String CARD_BACKGROUND = "#2a2a2a";
    
    private Movie movie;
    private MovieListingView parentView;
    private RegisteredUser loggedInUser;

    public MovieDetailView(Movie movie, MovieListingView parentView, RegisteredUser loggedInUser) {
        this.movie = movie;
        this.parentView = parentView;
        this.loggedInUser = loggedInUser;
    }

    public Node createContent() {
        // Main scroll pane for the content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle(
            "-fx-background: " + DARK_BACKGROUND + ";" +
            "-fx-background-color: transparent;" +
            "-fx-control-inner-background: " + DARK_BACKGROUND + ";"
        );
        scrollPane.setFitToWidth(true);

        // Content container
        VBox content = new VBox(30);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(40));

        // Back button
        Button backButton = createBackButton();
        
        // User badge for registered users
        if (loggedInUser != null) {
            Label userBadge = new Label("Registered User");
            userBadge.setStyle(
                "-fx-background-color: #1db954;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 5 10;" +
                "-fx-background-radius: 3;" +
                "-fx-font-size: 12px;"
            );
            content.getChildren().add(userBadge);
        }
        
        // Movie header section
        HBox headerSection = createHeaderSection();
        
        // Synopsis section
        VBox synopsisSection = createSynopsisSection();
        
        // Details section
        VBox detailsSection = createDetailsSection();
        
        // Booking button
        Button completeBookingButton = createCompleteBookingButton();

        content.getChildren().addAll(backButton, headerSection, synopsisSection, 
                                   detailsSection, completeBookingButton);
        scrollPane.setContent(content);
        
        return scrollPane;
    }

    private Button createBackButton() {
        Button backButton = new Button("← Back to Movies");
        backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;"
        );
        
        backButton.setOnMouseEntered(e -> backButton.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;"
        ));
        
        backButton.setOnMouseExited(e -> backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;"
        ));
        
        backButton.setOnAction(e -> parentView.showMovieList());
        
        return backButton;
    }

    private HBox createHeaderSection() {
    HBox header = new HBox(30);
    header.setAlignment(Pos.CENTER_LEFT);
    
    // Create ImageView first
    ImageView poster = new ImageView();
    poster.setFitWidth(300);
    poster.setFitHeight(450);
    poster.setPreserveRatio(true);
    
    // Load image with error handling
    try {
        Image image = new Image(movie.getPosterUrl(), true);  // true for background loading
        image.errorProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                try {
                    // Load default image if URL fails
                    poster.setImage(new Image("file:MovieReservationApp/assets/no-poster.png"));
                } catch (Exception e) {
                    // If even default image fails, create a blank placeholder
                    Rectangle placeholder = new Rectangle(300, 450);
                    placeholder.setFill(Color.GRAY);
                    poster.setImage(null);
                    System.err.println("Failed to load both movie poster and default image");
                }
            }
        });
        poster.setImage(image);
    } catch (Exception e) {
        try {
            // Load default image if there's any exception
            poster.setImage(new Image("file:MovieReservationApp/assets/no-poster.png"));
        } catch (Exception ex) {
            // If even default image fails, create a blank placeholder
            Rectangle placeholder = new Rectangle(300, 450);
            placeholder.setFill(Color.GRAY);
            poster.setImage(null);
            System.err.println("Failed to load both movie poster and default image");
        }
    }
    
    // Movie info
    VBox info = new VBox(15);
    info.setAlignment(Pos.CENTER_LEFT);
    
    Label title = new Label(movie.getTitle());
    title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
    title.setTextFill(Color.WHITE);
    
    Label rating = new Label(String.format("★ %.1f", movie.getRating()));
    rating.setFont(Font.font("Arial", FontWeight.BOLD, 24));
    rating.setTextFill(Color.web("#ffd700"));
    
    Label genre = new Label(movie.getGenre());
    genre.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
    genre.setTextFill(Color.LIGHTGRAY);
    
    info.getChildren().addAll(title, rating, genre);
    
    header.getChildren().addAll(poster, info);
    return header;
}

    private VBox createSynopsisSection() {
        VBox synopsis = new VBox(15);
        synopsis.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" + 
            "-fx-padding: 20;" + 
            "-fx-background-radius: 10;"
        );
        
        Label synopsisTitle = new Label("Synopsis");
        synopsisTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        synopsisTitle.setTextFill(Color.WHITE);
        
        Label synopsisText = new Label(movie.getSynopsis());
        synopsisText.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        synopsisText.setTextFill(Color.LIGHTGRAY);
        synopsisText.setWrapText(true);
        synopsisText.setTextAlignment(TextAlignment.JUSTIFY);
        
        synopsis.getChildren().addAll(synopsisTitle, synopsisText);
        return synopsis;
    }

    private VBox createDetailsSection() {
        VBox details = new VBox(15);
        details.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" + 
            "-fx-padding: 20;" + 
            "-fx-background-radius: 10;"
        );
        
        Label detailsTitle = new Label("Movie Details");
        detailsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        detailsTitle.setTextFill(Color.WHITE);
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 0, 0, 0));
        
        addDetailRow(grid, 0, "Duration:", String.format("%d minutes", movie.getDuration()));
        addDetailRow(grid, 1, "Age Rating:", movie.getAgeRating());
        addDetailRow(grid, 2, "Genre:", movie.getGenre());
        
        details.getChildren().addAll(detailsTitle, grid);
        return details;
    }

    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        labelNode.setTextFill(Color.LIGHTGRAY);
        
        Label valueNode = new Label(value);
        valueNode.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        valueNode.setTextFill(Color.WHITE);
        
        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }

    private Button createCompleteBookingButton() {
        Button completeBookingButton = new Button("Complete Booking");
        completeBookingButton.setStyle(
            "-fx-background-color: " + NETFLIX_RED + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 15 30;" +
            "-fx-font-size: 16px;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        
        completeBookingButton.setOnMouseEntered(e -> 
            completeBookingButton.setStyle(
                "-fx-background-color: derive(" + NETFLIX_RED + ", -10%);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 15 30;" +
                "-fx-font-size: 16px;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;"
            )
        );
        
        completeBookingButton.setOnMouseExited(e -> 
            completeBookingButton.setStyle(
                "-fx-background-color: " + NETFLIX_RED + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 15 30;" +
                "-fx-font-size: 16px;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;"
            )
        );
        
        completeBookingButton.setOnAction(e -> handleCompleteBooking());
        
        return completeBookingButton;
    }

    private void handleCompleteBooking() {
        System.out.println("=== MovieDetailView Debug ===");
        System.out.println("Movie ID: " + movie.getId());
        System.out.println("Movie Title: " + movie.getTitle());
        String userType = (loggedInUser != null) ? "Registered" : "Guest";
        
        try {
            // Pass both userType and loggedInUser to MovieTimingSelectionView
            MovieTimingSelectionView timingView = new MovieTimingSelectionView(movie, userType, loggedInUser);
            timingView.show();
        } catch (Exception e) {
            System.err.println("Error creating timing view: ");
            e.printStackTrace();
        }
    }
}