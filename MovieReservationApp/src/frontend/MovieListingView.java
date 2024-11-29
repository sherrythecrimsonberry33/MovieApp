// package frontend;

// import backend.Entity.Movie;
// import backend.actors.RegisteredUser;
// import backend.database.MovieDAO;
// import javafx.application.Application;
// import javafx.geometry.Insets;
// import javafx.scene.Scene;
// import javafx.scene.control.*;
// import javafx.scene.image.Image;
// import javafx.scene.image.ImageView;
// import javafx.scene.layout.*;
// import javafx.stage.Stage;
// import javafx.scene.text.Font;
// import javafx.scene.text.FontWeight;
// import javafx.geometry.Pos;
// import javafx.scene.paint.Color;
// import javafx.concurrent.Task;
// import javafx.scene.effect.DropShadow;

// import java.util.List;

// public class MovieListingView extends Application {
//     private MovieDAO movieDAO;
//     private VBox contentContainer;
//     private ScrollPane scrollPane;
//     private ProgressIndicator loadingIndicator;
//     private BorderPane mainLayout;
//     private List<Movie> cachedMovies;
//     private RegisteredUser loggedInUser;
//     private Stage primaryStage;
    
//     // Define common styles as constants
//     private static final String DARK_BACKGROUND = "#1a1a1a";
//     private static final String NETFLIX_RED = "#e50914";
//     private static final String CARD_BACKGROUND = "#2a2a2a";
//     private static final String LOGO_PATH = "MovieReservationApp/assets/APlogo.png";

//     @Override
//     public void start(Stage primaryStage) {
//         this.primaryStage = primaryStage;
//         movieDAO = new MovieDAO();

//         mainLayout = new BorderPane();
//         mainLayout.setStyle("-fx-background-color: " + DARK_BACKGROUND + ";");

//         // Header with logo
//         VBox header = createHeader();
//         mainLayout.setTop(header);

//         // Show the movie list initially
//         showMovieList();

//         Scene scene = new Scene(mainLayout);
//         primaryStage.setTitle("AcmePlex - Now Showing");
//         primaryStage.setScene(scene);
//         // Enable full-screen mode
//         primaryStage.setMaximized(true);
//         primaryStage.show();
//     }

//     protected VBox createHeader() {
//         VBox header = new VBox(10);
//         header.setAlignment(Pos.CENTER);
//         header.setPadding(new Insets(20));
//         header.setStyle("-fx-background-color: #000000;");

//         // Add user info bar if logged in
//         if (loggedInUser != null) {
//             HBox userInfo = new HBox(10);
//             userInfo.setAlignment(Pos.CENTER_RIGHT);
//             userInfo.setPadding(new Insets(5, 20, 5, 20));
//             userInfo.setStyle("-fx-background-color: #1a1a1a;");
            
//             Label welcomeLabel = new Label(String.format("Welcome, %s %s", 
//                 loggedInUser.getFirstName(), loggedInUser.getLastName()));
//             welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
//             welcomeLabel.setTextFill(Color.WHITE);
            
//             Button logoutButton = createStyledButton("Logout");
//             logoutButton.setOnAction(e -> handleLogout());
            
//             userInfo.getChildren().addAll(welcomeLabel, logoutButton);
//             header.getChildren().add(userInfo);
//         }

//         try {
//             // Add logo
//             ImageView logo = new ImageView(new Image("file:" + LOGO_PATH));
//             logo.setFitHeight(60);
//             logo.setPreserveRatio(true);
//             header.getChildren().add(logo);
//         } catch (Exception e) {
//             System.err.println("Failed to load logo: " + e.getMessage());
//         }

//         Label title = new Label("AcmePlex Theaters");
//         title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
//         title.setTextFill(Color.WHITE);

//         Label subtitle = new Label("Now Showing");
//         subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
//         subtitle.setTextFill(Color.web(NETFLIX_RED));

//         header.getChildren().addAll(title, subtitle);
//         return header;
//     }

//     public void setLoggedInUser(RegisteredUser user) {
//         this.loggedInUser = user;
//     }

//     private void handleLogout() {
//         this.loggedInUser = null;
//         // Restart the application in guest mode
//         new MainEntryView().start(new Stage());
//         primaryStage.close();
//     }

//     public void showMovieList() {
//         contentContainer = new VBox(20);
//         contentContainer.setPadding(new Insets(20));
//         contentContainer.setAlignment(Pos.CENTER);

//         scrollPane = new ScrollPane(contentContainer);
//         scrollPane.setFitToWidth(true);
//         scrollPane.setStyle(
//             "-fx-background: " + DARK_BACKGROUND + ";" +
//             "-fx-background-color: transparent;" +
//             "-fx-control-inner-background: " + DARK_BACKGROUND + ";" +
//             "-fx-viewport-background: " + DARK_BACKGROUND + ";"
//         );

//         mainLayout.setCenter(scrollPane);

//         if (cachedMovies != null) {
//             displayMovies(cachedMovies);
//         } else {
//             loadingIndicator = new ProgressIndicator();
//             loadingIndicator.setStyle("-fx-progress-color: " + NETFLIX_RED + ";");
//             contentContainer.getChildren().add(loadingIndicator);
//             loadMovies();
//         }
//     }

//     private void loadMovies() {
//         Task<List<Movie>> loadMoviesTask = new Task<>() {
//             @Override
//             protected List<Movie> call() {
//                 return movieDAO.getAllMovies();
//             }
//         };

//         loadMoviesTask.setOnSucceeded(event -> {
//             cachedMovies = loadMoviesTask.getValue();
//             displayMovies(cachedMovies);
//             loadingIndicator.setVisible(false);
//         });

//         loadMoviesTask.setOnFailed(event -> {
//             loadingIndicator.setVisible(false);
//             showError("Failed to load movies");
//         });

//         new Thread(loadMoviesTask).start();
//     }

//     private void displayMovies(List<Movie> movies) {
//         contentContainer.getChildren().clear();
//         FlowPane movieGrid = new FlowPane(20, 20);
//         movieGrid.setAlignment(Pos.CENTER);

//         for (Movie movie : movies) {
//             movieGrid.getChildren().add(createMovieCard(movie));
//         }

//         contentContainer.getChildren().add(movieGrid);
//     }

//     private VBox createMovieCard(Movie movie) {
//         VBox card = new VBox(10);
//         card.setPadding(new Insets(10));
//         card.setAlignment(Pos.CENTER);
//         card.setMinWidth(250);
//         card.setMaxWidth(250);
        
//         // Card styling
//         DropShadow shadow = new DropShadow();
//         shadow.setColor(Color.rgb(0, 0, 0, 0.5));
//         shadow.setRadius(10);
        
//         card.setStyle(
//             "-fx-background-color: " + CARD_BACKGROUND + ";" +
//             "-fx-background-radius: 10;" +
//             "-fx-border-radius: 10;"
//         );
//         card.setEffect(shadow);

//         // Poster
//         ImageView poster = new ImageView();
//         poster.setFitWidth(200);
//         poster.setFitHeight(300);
//         poster.setPreserveRatio(true);
        
//         // Load image asynchronously
//         Task<Image> loadImageTask = new Task<>() {
//             @Override
//             protected Image call() {
//                 return new Image(movie.getPosterUrl(), true);
//             }
//         };

//         loadImageTask.setOnSucceeded(event -> poster.setImage(loadImageTask.getValue()));
//         new Thread(loadImageTask).start();

//         // Movie info
//         Label title = new Label(movie.getTitle());
//         title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
//         title.setWrapText(true);
//         title.setTextFill(Color.WHITE);

//         Label genre = new Label(movie.getGenre());
//         genre.setTextFill(Color.LIGHTGRAY);

//         Label rating = new Label(String.format("★ %.1f", movie.getRating()));
//         rating.setTextFill(Color.web("#ffd700"));

//         Label duration = new Label(String.format("%d min • %s", 
//             movie.getDuration(), movie.getAgeRating()));
//         duration.setTextFill(Color.LIGHTGRAY);

//         // Book button
//         Button bookButton = createStyledButton("Book Tickets");
        
//         bookButton.setOnAction(e -> handleBooking(movie));

//         card.getChildren().addAll(poster, title, genre, rating, duration, bookButton);
//         return card;
//     }

//     private Button createStyledButton(String text) {
//         Button button = new Button(text);
//         button.setStyle(
//             "-fx-background-color: " + NETFLIX_RED + ";" +
//             "-fx-text-fill: white;" +
//             "-fx-font-weight: bold;" +
//             "-fx-padding: 8 16;" +
//             "-fx-background-radius: 5;" +
//             "-fx-cursor: hand;"
//         );
        
//         button.setOnMouseEntered(e -> 
//             button.setStyle(
//                 "-fx-background-color: derive(" + NETFLIX_RED + ", -10%);" +
//                 "-fx-text-fill: white;" +
//                 "-fx-font-weight: bold;" +
//                 "-fx-padding: 8 16;" +
//                 "-fx-background-radius: 5;" +
//                 "-fx-cursor: hand;"
//             )
//         );
        
//         button.setOnMouseExited(e -> 
//             button.setStyle(
//                 "-fx-background-color: " + NETFLIX_RED + ";" +
//                 "-fx-text-fill: white;" +
//                 "-fx-font-weight: bold;" +
//                 "-fx-padding: 8 16;" +
//                 "-fx-background-radius: 5;" +
//                 "-fx-cursor: hand;"
//             )
//         );
        
//         return button;
//     }

//     private void handleBooking(Movie movie) {
//         // Create movie detail view and show its content in the main layout
//         MovieDetailView detailView = new MovieDetailView(movie, this, loggedInUser);
//         mainLayout.setCenter(detailView.createContent());
//     }

//     private void showError(String message) {
//         Alert alert = new Alert(Alert.AlertType.ERROR);
//         alert.setTitle("Error");
//         alert.setHeaderText(null);
//         alert.setContentText(message);
//         alert.showAndWait();
//     }

// }

package frontend;

import backend.Entity.Movie;
import backend.actors.RegisteredUser;
import backend.database.MovieDAO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.concurrent.Task;
import javafx.scene.effect.DropShadow;

import java.util.List;

public class MovieListingView extends Application {
    private MovieDAO movieDAO;
    private VBox contentContainer;
    private ScrollPane scrollPane;
    private ProgressIndicator loadingIndicator;
    private BorderPane mainLayout;
    private List<Movie> cachedMovies;
    private RegisteredUser loggedInUser;
    private Stage primaryStage;
    
    // Define common styles as constants
    private static final String DARK_BACKGROUND = "#1a1a1a";
    private static final String NETFLIX_RED = "#e50914";
    private static final String CARD_BACKGROUND = "#2a2a2a";
    private static final String LOGO_PATH = "MovieReservationApp/assets/APlogo.png";

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        movieDAO = new MovieDAO();

        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + DARK_BACKGROUND + ";");

        // Header with logo and buttons
        VBox header = createHeader();
        mainLayout.setTop(header);

        // Show the movie list initially
        showMovieList();

        Scene scene = new Scene(mainLayout);
        primaryStage.setTitle("AcmePlex - Now Showing");
        primaryStage.setScene(scene);
        // Enable full-screen mode
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    protected VBox createHeader() {
        VBox header = new VBox();
        header.setPadding(new Insets(0));
        header.setStyle("-fx-background-color: #000000;");
    
        // Top bar with Cancel Ticket button and User Info
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setAlignment(Pos.CENTER_LEFT); // Start aligned to the left
        topBar.setSpacing(10);
        topBar.setStyle("-fx-background-color: #1a1a1a;");
    
        // Cancel Ticket Button
        Button cancelTicketButton = createStyledButton("Cancel Ticket");
        cancelTicketButton.setOnAction(e -> handleCancelTicket());
    
        // Spacer to push the button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
    
        // Add Cancel Ticket button after the spacer
        topBar.getChildren().addAll(spacer, cancelTicketButton);
    
        // Add user info and Logout button if logged in
        if (loggedInUser != null) {
            Label welcomeLabel = new Label(String.format("Welcome, %s %s", 
                loggedInUser.getFirstName(), loggedInUser.getLastName()));
            welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            welcomeLabel.setTextFill(Color.WHITE);
            
            Button logoutButton = createStyledButton("Logout");
            logoutButton.setOnAction(e -> handleLogout());
            
            // Add user info and Logout button to the topBar
            topBar.getChildren().addAll(welcomeLabel, logoutButton);
        }
    
        // Center part with logo and titles
        VBox centerBox = new VBox(5);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(10));
    
        try {
            // Add logo
            ImageView logo = new ImageView(new Image("file:" + LOGO_PATH));
            logo.setFitHeight(60);
            logo.setPreserveRatio(true);
            centerBox.getChildren().add(logo);
        } catch (Exception e) {
            System.err.println("Failed to load logo: " + e.getMessage());
        }
    
        Label title = new Label("AcmePlex Theaters");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setTextFill(Color.WHITE);
    
        Label subtitle = new Label("Now Showing");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        subtitle.setTextFill(Color.web(NETFLIX_RED));
    
        centerBox.getChildren().addAll(title, subtitle);
    
        // Add the topBar and centerBox to the header
        header.getChildren().addAll(topBar, centerBox);
        return header;
    }
    

    public void setLoggedInUser(RegisteredUser user) {
        this.loggedInUser = user;
    }

    private void handleLogout() {
        this.loggedInUser = null;
        // Restart the application in guest mode
        new MainEntryView().start(new Stage());
        primaryStage.close();
    }

    private void handleCancelTicket() {
        // Open the TicketCancellationView
        TicketCancellationView cancellationView = new TicketCancellationView();
        cancellationView.show();
    }

    public void showMovieList() {
        contentContainer = new VBox(20);
        contentContainer.setPadding(new Insets(20));
        contentContainer.setAlignment(Pos.CENTER);

        scrollPane = new ScrollPane(contentContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
            "-fx-background: " + DARK_BACKGROUND + ";" +
            "-fx-background-color: transparent;" +
            "-fx-control-inner-background: " + DARK_BACKGROUND + ";" +
            "-fx-viewport-background: " + DARK_BACKGROUND + ";"
        );

        mainLayout.setCenter(scrollPane);

        if (cachedMovies != null) {
            displayMovies(cachedMovies);
        } else {
            loadingIndicator = new ProgressIndicator();
            loadingIndicator.setStyle("-fx-progress-color: " + NETFLIX_RED + ";");
            contentContainer.getChildren().add(loadingIndicator);
            loadMovies();
        }
    }

    private void loadMovies() {
        Task<List<Movie>> loadMoviesTask = new Task<>() {
            @Override
            protected List<Movie> call() {
                return movieDAO.getAllMovies();
            }
        };

        loadMoviesTask.setOnSucceeded(event -> {
            cachedMovies = loadMoviesTask.getValue();
            displayMovies(cachedMovies);
            loadingIndicator.setVisible(false);
        });

        loadMoviesTask.setOnFailed(event -> {
            loadingIndicator.setVisible(false);
            showError("Failed to load movies");
        });

        new Thread(loadMoviesTask).start();
    }

    private void displayMovies(List<Movie> movies) {
        contentContainer.getChildren().clear();
        FlowPane movieGrid = new FlowPane(20, 20);
        movieGrid.setAlignment(Pos.CENTER);

        for (Movie movie : movies) {
            movieGrid.getChildren().add(createMovieCard(movie));
        }

        contentContainer.getChildren().add(movieGrid);
    }

    private VBox createMovieCard(Movie movie) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER);
        card.setMinWidth(250);
        card.setMaxWidth(250);
        
        // Card styling
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(10);
        
        card.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;"
        );
        card.setEffect(shadow);

        // Poster
        ImageView poster = new ImageView();
        poster.setFitWidth(200);
        poster.setFitHeight(300);
        poster.setPreserveRatio(true);
        
        // Load image asynchronously
        Task<Image> loadImageTask = new Task<>() {
            @Override
            protected Image call() {
                return new Image(movie.getPosterUrl(), true);
            }
        };

        loadImageTask.setOnSucceeded(event -> poster.setImage(loadImageTask.getValue()));
        new Thread(loadImageTask).start();

        // Movie info
        Label title = new Label(movie.getTitle());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setWrapText(true);
        title.setTextFill(Color.WHITE);

        Label genre = new Label(movie.getGenre());
        genre.setTextFill(Color.LIGHTGRAY);

        Label rating = new Label(String.format("★ %.1f", movie.getRating()));
        rating.setTextFill(Color.web("#ffd700"));

        Label duration = new Label(String.format("%d min • %s", 
            movie.getDuration(), movie.getAgeRating()));
        duration.setTextFill(Color.LIGHTGRAY);

        // Book button
        Button bookButton = createStyledButton("Book Tickets");
        
        bookButton.setOnAction(e -> handleBooking(movie));

        card.getChildren().addAll(poster, title, genre, rating, duration, bookButton);
        return card;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: " + NETFLIX_RED + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 16;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: derive(" + NETFLIX_RED + ", -10%);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 16;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;"
            )
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(
                "-fx-background-color: " + NETFLIX_RED + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 16;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;"
            )
        );
        
        return button;
    }

    private void handleBooking(Movie movie) {
        // Create movie detail view and show its content in the main layout
        MovieDetailView detailView = new MovieDetailView(movie, this, loggedInUser);
        mainLayout.setCenter(detailView.createContent());
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
