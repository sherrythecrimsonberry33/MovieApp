package frontend;

import backend.Entity.*;
import backend.actors.RegisteredUser;
import backend.database.SeatBookingDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.util.*;

public class SeatSelectionView {
    private static final String DARK_BACKGROUND = "#1a1a1a";
    private static final String NETFLIX_RED = "#e50914";
    private static final String CARD_BACKGROUND = "#2a2a2a";
    
    private Stage stage;
    private MovieTimings movieTiming;
    private int requiredSeats;
    private String userType;
    private RegisteredUser registeredUser;  // Add this field
    private Set<String> selectedSeats = new HashSet<>();
    private Map<String, Button> seatButtons = new HashMap<>();
    private SeatBookingDAO seatBookingDAO;
    
    // Update constructor
    public SeatSelectionView(MovieTimings movieTiming, int requiredSeats, String userType, RegisteredUser registeredUser) {
        this.stage = new Stage();
        this.movieTiming = movieTiming;
        this.requiredSeats = requiredSeats;
        this.userType = userType;
        this.registeredUser = registeredUser;
        this.seatBookingDAO = new SeatBookingDAO();
    }
    
    public void show() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + DARK_BACKGROUND + ";");
        mainLayout.setPadding(new Insets(20));
        
        // Top section with movie info
        VBox topSection = createTopSection();
        mainLayout.setTop(topSection);
        
        // Center section with seats
        ScrollPane scrollPane = new ScrollPane(createSeatsSection());
        scrollPane.setStyle(
            "-fx-background: " + DARK_BACKGROUND + ";" +
            "-fx-background-color: transparent;" +
            "-fx-control-inner-background: " + DARK_BACKGROUND + ";"
        );
        scrollPane.setFitToWidth(true);
        mainLayout.setCenter(scrollPane);
        
        // Bottom section with legend and proceed button
        VBox bottomSection = createBottomSection();
        mainLayout.setBottom(bottomSection);
        
        Scene scene = new Scene(mainLayout, 800, 600);
        stage.setTitle("Select Your Seats");
        stage.setScene(scene);
        
        // Load booked seats from database
        loadBookedSeats();
        
        stage.show();
    }
    
    private VBox createTopSection() {
        VBox section = new VBox(10);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: " + CARD_BACKGROUND + ";");
        
        Label movieTitle = new Label(movieTiming.getMovie().getTitle());
        movieTitle.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Label showTime = new Label(movieTiming.getFormattedMovieTimings());
        showTime.setStyle("-fx-font-size: 16px; -fx-text-fill: #cccccc;");
        
        Label hallInfo = new Label(movieTiming.getMovieHall().getHallName().getDisplayName());
        hallInfo.setStyle("-fx-font-size: 16px; -fx-text-fill: #cccccc;");
        
        section.getChildren().addAll(movieTitle, showTime, hallInfo);
        
        return section;
    }
    

    private GridPane createSeatsSection() {
        GridPane seatsGrid = new GridPane();
        seatsGrid.setAlignment(Pos.CENTER);
        seatsGrid.setHgap(10); // Consistent spacing
        seatsGrid.setVgap(10);
        seatsGrid.setPadding(new Insets(30)); // Increased padding for better spacing
    
        int rows = movieTiming.getMovieHall().getRows();
        int cols = movieTiming.getMovieHall().getColumns();
    
        // Improved cinema screen
        Rectangle screenBase = new Rectangle(cols * 30, 10); // Scaled to column count
        screenBase.setFill(Color.web("#606060"));
        screenBase.setArcHeight(20);
        screenBase.setArcWidth(40);
    
        VBox screenContainer = new VBox(10);
        screenContainer.setAlignment(Pos.CENTER);
        Label screenLabel = new Label("SCREEN");
        screenLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        screenContainer.getChildren().addAll(screenBase, screenLabel);
        screenContainer.setPadding(new Insets(0, 0, 40, 0)); // Better separation from seats
        seatsGrid.add(screenContainer, 0, 0, cols + 2, 1);
    
        // Curvature and spacing adjustments
        double baseSpacing = 10.0;
        double rowSpacingIncrease = 1.0; // Subtle spacing increase
        double curveIntensity = 0.6;     // Reduced curve for symmetry
    
        for (int row = 0; row < rows; row++) {
            Label rowLabel = new Label(String.valueOf((char) ('A' + row)));
            rowLabel.setStyle("-fx-text-fill: white;");
    
            double currentSpacing = baseSpacing + (row * rowSpacingIncrease);
    
            HBox rowBox = new HBox();
            rowBox.setAlignment(Pos.CENTER);
            rowBox.setSpacing(currentSpacing);
    
            rowBox.getChildren().add(rowLabel);
    
            for (int col = 0; col < cols; col++) {
                Button seatButton = createSeatButton(row, col);
    
                // Symmetric curvature calculation
                double centerOffset = col - (cols - 1) / 2.0;
                double curveOffset = Math.pow(centerOffset / (cols / 2.0), 2);
                double yOffset = curveOffset * curveIntensity * (rows - row); // Adjusted multiplier
    
                VBox seatContainer = new VBox();
                seatContainer.setPadding(new Insets(yOffset, 0, 0, 0));
                seatContainer.getChildren().add(seatButton);
    
                rowBox.getChildren().add(seatContainer);
                seatButtons.put(getSeatId(row, col), seatButton);
            }
    
            double rowMargin = row * 4.0; // Consistent margin increase
            GridPane.setMargin(rowBox, new Insets(rowMargin, 0, 0, 0));
            seatsGrid.add(rowBox, 0, row + 2, cols + 2, 1);
        }
    
        return seatsGrid;
    }
    
    private Button createSeatButton(int row, int col) {
        Button button = new Button();
        button.setMinSize(24, 24); // Uniform size
        button.setMaxSize(24, 24);
        button.setStyle(getDefaultSeatStyle());
    
        String seatId = getSeatId(row, col);
        button.setId(seatId);
    
        button.setOnAction(e -> handleSeatSelection(seatId, button));
    
        return button;
    }
    
    
    private VBox createBottomSection() {
        VBox section = new VBox(20);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(20));
        
        // Legend
        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER);
        legend.getChildren().addAll(
            createLegendItem("Available", getDefaultSeatStyle()),
            createLegendItem("Selected", getSelectedSeatStyle()),
            createLegendItem("Booked", getBookedSeatStyle())
        );
        
        // Selection info
        Label selectionInfo = new Label(String.format("Select %d seats", requiredSeats));
        selectionInfo.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        
        // Proceed button
        Button proceedButton = new Button("Proceed to Payment");
        proceedButton.setStyle(
            "-fx-background-color: " + NETFLIX_RED + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-font-size: 14px;"
        );
        
        proceedButton.setOnAction(e -> handleProceed());
        
        section.getChildren().addAll(legend, selectionInfo, proceedButton);
        return section;
    }
    
    private HBox createLegendItem(String text, String style) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER);
        
        Button sample = new Button();
        sample.setMinSize(20, 20);
        sample.setMaxSize(20, 20);
        sample.setStyle(style);
        sample.setDisable(true);
        
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white;");
        
        item.getChildren().addAll(sample, label);
        return item;
    }
    
    private void handleSeatSelection(String seatId, Button button) {
        if (button.getStyle().equals(getBookedSeatStyle())) {
            return; // Can't select booked seats
        }
        
        if (selectedSeats.contains(seatId)) {
            selectedSeats.remove(seatId);
            button.setStyle(getDefaultSeatStyle());
        } else if (selectedSeats.size() < requiredSeats) {
            selectedSeats.add(seatId);
            button.setStyle(getSelectedSeatStyle());
        }
    }
    
    private void loadBookedSeats() {
        List<Seat> bookedSeats = seatBookingDAO.getBookedSeatsForTiming(movieTiming.getId());
        for (Seat seat : bookedSeats) {
            String seatId = getSeatId(seat.getRow(), seat.getColumn());
            Button button = seatButtons.get(seatId);
            if (button != null) {
                button.setStyle(getBookedSeatStyle());
            }
        }
    }
    
    private void handleProceed() {
        if (selectedSeats.size() != requiredSeats) {
            showAlert("Please select exactly " + requiredSeats + " seats.");
            return;
        }
        
        List<Seat> seats = new ArrayList<>();
        for (String seatId : selectedSeats) {
            int row = seatId.charAt(0) - 'A';
            int col = Integer.parseInt(seatId.substring(1)) - 1;
            seats.add(new Seat(row, col));
        }
        
        // Pass registeredUser to PaymentView
        PaymentView paymentView = new PaymentView(movieTiming, seats, userType, registeredUser);
        paymentView.show();
        stage.close();
    }

    
    private String getSeatId(int row, int col) {
        return String.format("%c%d", (char)('A' + row), col + 1);
    }
    
    private String getDefaultSeatStyle() {
        return "-fx-background-color: #007bff; -fx-text-fill: white;";
    }
    
    private String getSelectedSeatStyle() {
        return "-fx-background-color: #28a745; -fx-text-fill: white;";
    }
    
    private String getBookedSeatStyle() {
        return "-fx-background-color: #6c757d; -fx-text-fill: white;";
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}