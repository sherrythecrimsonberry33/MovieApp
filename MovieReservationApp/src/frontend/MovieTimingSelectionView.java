

package frontend;

import backend.Entity.*;
import backend.actors.RegisteredUser;
import backend.database.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MovieTimingSelectionView {
    private static final String DARK_BACKGROUND = "#1a1a1a";
    private static final String CARD_BACKGROUND = "#2a2a2a";
    private static final String NETFLIX_RED = "#e50914";

    private Stage stage;
    private Movie movie;
    private MovieTimings selectedTiming;
    private int selectedSeats = 1;
    private MovieTimingsDAO movieTimingsDAO;
    private String userType;  // "Guest" or "Registered"
    private RegisteredUser loggedInUser;

    private ToggleGroup timingGroup;  
    private Button proceedButton;     
    private VBox timingsSection;      
    private VBox mainLayout;          
    private DatePicker datePicker;

    public MovieTimingSelectionView(Movie movie, String userType, RegisteredUser loggedInUser) {
        this.movie = movie;
        this.userType = userType;
        this.loggedInUser = loggedInUser;  
        this.stage = new Stage();
        this.movieTimingsDAO = new MovieTimingsDAO();
        this.timingGroup = new ToggleGroup(); 
    }

    public void show() {
        mainLayout = new VBox(20);
        mainLayout.setStyle("-fx-background-color: " + DARK_BACKGROUND + ";");
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.TOP_CENTER);

        // Movie info section
        VBox movieInfo = createMovieInfoSection();

        // Date selection section
        HBox dateSelectionSection = createDateSelectionSection();

        // Timings section
        timingsSection = createTimingsSection(LocalDate.now());

        // Seats selection
        VBox seatsSection = createSeatsSection();

        // Proceed button
        proceedButton = createProceedButton();

        // Error text
        Text errorText = new Text();
        errorText.setStyle("-fx-fill: #ff4444; -fx-font-size: 14px;");

        // Add listener to timingGroup to enable/disable proceedButton
        timingGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedTiming = (MovieTimings) newVal.getUserData();
                proceedButton.setDisable(false);
                errorText.setText(""); // Clear any error messages
            } else {
                proceedButton.setDisable(true);
            }
        });

        mainLayout.getChildren().addAll(
            movieInfo,
            dateSelectionSection,
            timingsSection,
            seatsSection,
            proceedButton,
            errorText
        );

        Scene scene = new Scene(mainLayout);
        stage.setTitle("Select Show Timing - " + movie.getTitle());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private HBox createDateSelectionSection() {
        HBox section = new HBox(10);
        section.setAlignment(Pos.CENTER_LEFT);
        section.setPadding(new Insets(10));

        Label dateLabel = new Label("Select Date:");
        dateLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        datePicker = new DatePicker(LocalDate.now());
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()) || date.isAfter(LocalDate.now().plusDays(13)));
            }
        });

        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            // Update timings section when date changes
            updateTimingsSection(newVal);
        });

        section.getChildren().addAll(dateLabel, datePicker);
        return section;
    }

    private void updateTimingsSection(LocalDate selectedDate) {
        // Clear previous selections
        timingGroup.getToggles().clear();
        selectedTiming = null;
        proceedButton.setDisable(true);

        VBox newTimingsSection = createTimingsSection(selectedDate);
        // Replace the existing timings section in the main layout
        int index = mainLayout.getChildren().indexOf(timingsSection);
        mainLayout.getChildren().set(index, newTimingsSection);
        timingsSection = newTimingsSection;
    }

    private VBox createTimingsSection(LocalDate selectedDate) {
        VBox section = new VBox(15);
        section.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" +
            "-fx-padding: 20px;" +
            "-fx-background-radius: 8px;"
        );

        Label sectionTitle = new Label("Available Show Times for " + selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        sectionTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );

        FlowPane timingsGrid = new FlowPane();
        timingsGrid.setHgap(10);
        timingsGrid.setVgap(10);

        List<MovieTimings> timings;
        try {
            timings = movieTimingsDAO.getAvailableTimingsForMovie(movie.getId(), selectedDate);
            if (timings == null || timings.isEmpty()) {
                Label noTimingsLabel = new Label("No available show times");
                noTimingsLabel.setStyle("-fx-text-fill: white;");
                section.getChildren().addAll(sectionTitle, noTimingsLabel);
                return section;
            }
        } catch (Exception e) {
            System.err.println("Error loading movie timings: " + e.getMessage());
            Label errorLabel = new Label("Unable to load show times");
            errorLabel.setStyle("-fx-text-fill: #ff4444;");
            section.getChildren().addAll(sectionTitle, errorLabel);
            return section;
        }

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (MovieTimings timing : timings) {
            VBox timeCard = new VBox(5);
            timeCard.setStyle(
                "-fx-background-color: #3a3a3a;" +
                "-fx-padding: 10px;" +
                "-fx-background-radius: 4px;"
            );

            RadioButton timeButton = new RadioButton(
                timing.getShowTime().format(timeFormatter)
            );
            timeButton.setToggleGroup(timingGroup);
            timeButton.setUserData(timing);
            timeButton.setStyle("-fx-text-fill: white;");

            // Add selection listener
            timeButton.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    selectedTiming = timing;
                    timeCard.setStyle(
                        "-fx-background-color: " + NETFLIX_RED + ";" +
                        "-fx-padding: 10px;" +
                        "-fx-background-radius: 4px;"
                    );
                } else {
                    timeCard.setStyle(
                        "-fx-background-color: #3a3a3a;" +
                        "-fx-padding: 10px;" +
                        "-fx-background-radius: 4px;"
                    );
                }
            });

            Label hallLabel = new Label(timing.getMovieHall().getHallName().getDisplayName());
            hallLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");

            Label priceLabel = new Label(String.format("$%.2f", timing.getTicketPrice()));
            priceLabel.setStyle("-fx-text-fill: #ffd700; -fx-font-size: 12px;");

            timeCard.getChildren().addAll(timeButton, hallLabel, priceLabel);
            timingsGrid.getChildren().add(timeCard);
        }

        section.getChildren().addAll(sectionTitle, timingsGrid);
        return section;
    }

    private VBox createMovieInfoSection() {
        VBox section = new VBox(10);
        section.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" +
            "-fx-padding: 20px;" +
            "-fx-background-radius: 8px;"
        );

        Label titleLabel = new Label(movie.getTitle());
        titleLabel.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );

        Label detailsLabel = new Label(
            String.format("%s | %s | %s",
                movie.getGenre(),
                movie.getAgeRating(),
                movie.getFormattedDuration()
            )
        );
        detailsLabel.setStyle("-fx-text-fill: #cccccc;");

        if (userType.equals("Registered")) {
            Label userBadge = new Label("Registered User Benefits Apply");
            userBadge.setStyle(
                "-fx-background-color: #1db954;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 5 10;" +
                "-fx-background-radius: 3;" +
                "-fx-font-size: 12px;"
            );
            section.getChildren().add(userBadge);
        }

        section.getChildren().addAll(titleLabel, detailsLabel);
        return section;
    }

    private VBox createSeatsSection() {
        VBox section = new VBox(15);
        section.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" +
            "-fx-padding: 20px;" +
            "-fx-background-radius: 8px;"
        );

        Label sectionTitle = new Label("Number of Seats (Max 7)");
        sectionTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );

        HBox seatsControl = new HBox(15);
        seatsControl.setAlignment(Pos.CENTER);

        Button decreaseButton = createStyledButton("-");
        Label seatsLabel = new Label("1");
        seatsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        Button increaseButton = createStyledButton("+");

        decreaseButton.setOnAction(e -> {
            if (selectedSeats > 1) {
                selectedSeats--;
                seatsLabel.setText(String.valueOf(selectedSeats));
            }
        });

        increaseButton.setOnAction(e -> {
            if (selectedSeats < 7) {
                selectedSeats++;
                seatsLabel.setText(String.valueOf(selectedSeats));
            }
        });

        seatsControl.getChildren().addAll(decreaseButton, seatsLabel, increaseButton);
        section.getChildren().addAll(sectionTitle, seatsControl);

        return section;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: " + NETFLIX_RED + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-background-radius: 4px;"
        );
        return button;
    }

    private Button createProceedButton() {
        Button proceedButton = new Button("Proceed to Seat Selection");
        proceedButton.setStyle(
            "-fx-background-color: " + NETFLIX_RED + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-background-radius: 4px;" +
            "-fx-cursor: hand;"
        );

        // Initially disable the proceed button until a timing is selected
        proceedButton.setDisable(true);

        // Add action handler
        proceedButton.setOnAction(e -> {
            if (selectedTiming != null) {
                SeatSelectionView seatView = new SeatSelectionView(selectedTiming, selectedSeats, userType, loggedInUser);
                seatView.show();
                stage.close();
            }
        });

        // Add hover effects
        proceedButton.setOnMouseEntered(e ->
            proceedButton.setStyle(
                "-fx-background-color: derive(" + NETFLIX_RED + ", -10%);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 20;" +
                "-fx-background-radius: 4px;" +
                "-fx-cursor: hand;"
            )
        );

        proceedButton.setOnMouseExited(e ->
            proceedButton.setStyle(
                "-fx-background-color: " + NETFLIX_RED + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 20;" +
                "-fx-background-radius: 4px;" +
                "-fx-cursor: hand;"
            )
        );

        return proceedButton;
    }
}
