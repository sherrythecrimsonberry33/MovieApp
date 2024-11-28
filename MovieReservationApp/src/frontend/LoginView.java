package frontend;

import backend.Entity.PaymentDetails;
import backend.Entity.RegUserLogin;
import backend.actors.RegisteredUser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Text;

public class LoginView {
    private static final String DARK_BACKGROUND = "#1a1a1a";
    private static final String NETFLIX_RED = "#e50914";
    private static final String CARD_BACKGROUND = "#1a1a1a";
    private static final String INPUT_BACKGROUND = "#3a3a3a";
    
    private Stage parentStage;
    private Stage loginStage;
    private RegUserLogin regUserLogin;
    
    public LoginView(Stage parentStage) {
        this.parentStage = parentStage;
        this.loginStage = new Stage();
        this.regUserLogin = new RegUserLogin();
    }
    
    public void show() {
        VBox mainLayout = new VBox(20);
        mainLayout.setStyle("-fx-background-color: " + DARK_BACKGROUND + ";");
        mainLayout.setPadding(new Insets(40));
        mainLayout.setAlignment(Pos.CENTER);

        TabPane tabPane = new TabPane();
        tabPane.setStyle(
     
            "-fx-tab-min-width: 120px;" +
            "-fx-tab-max-width: 120px;" +
            "-fx-tab-min-height: 40px;" 

        );
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Tab loginTab = new Tab("Login", createLoginForm());
        Tab signupTab = new Tab("Sign Up", createSignupForm());
        loginTab.setStyle("-fx-text-fill: white;");
        signupTab.setStyle("-fx-text-fill: white;");
        
        // Style the tabs
        String tabStyle = 

            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10;";
        
        loginTab.setStyle(tabStyle);
        signupTab.setStyle(tabStyle);
        
 
    
        
        tabPane.getTabs().addAll(loginTab, signupTab);
        mainLayout.getChildren().add(tabPane);

        Scene scene = new Scene(mainLayout, 600, 800);
        loginStage.setTitle("AcmePlex - User Login");
        loginStage.setScene(scene);
        loginStage.show();
    }

    private VBox createLoginForm() {
        VBox loginForm = new VBox(25); // Increased spacing
        loginForm.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" +
            "-fx-padding: 40px;" +
            "-fx-background-radius: 8px;"
        );
        loginForm.setPadding(new Insets(40));
        loginForm.setAlignment(Pos.CENTER);

        TextField emailField = createStyledTextField("Email");
        PasswordField passwordField = createStyledPasswordField("Password");
        Button loginButton = createStyledButton("Login");
        Text errorText = new Text();
        errorText.setStyle("-fx-fill: #ff4444; -fx-font-size: 14px;");

        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            
            RegisteredUser user = regUserLogin.authenticateUser(email, password);
            if (user != null) {
                MovieListingView movieView = new MovieListingView();
                movieView.setLoggedInUser(user);
                movieView.start(new Stage());
                
                loginStage.close();
                parentStage.close();
            } else {
                errorText.setText("Invalid email or password");
            }
        });

        loginForm.getChildren().addAll(
            createHeader("Login to Your Account"),
            createFieldContainer(emailField),
            createFieldContainer(passwordField),
            loginButton,
            errorText
        );

        return loginForm;
    }

    private VBox createSignupForm() {
        VBox signupForm = new VBox(25); // Increased spacing
        signupForm.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" +
            "-fx-padding: 40px;" +
            "-fx-background-radius: 8px;"
        );
        signupForm.setPadding(new Insets(40));
        signupForm.setAlignment(Pos.CENTER);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        // Personal Information
        TextField firstNameField = createStyledTextField("First Name");
        TextField lastNameField = createStyledTextField("Last Name");
        TextField emailField = createStyledTextField("Email");
        PasswordField passwordField = createStyledPasswordField("Password");
        TextArea addressField = createStyledTextArea("Address");
        addressField.setStyle(
            "-fx-background-color: " + INPUT_BACKGROUND + ";" +
            "-fx-text-fill: black;" +
            "-fx-prompt-text-fill: #808080;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12px;" +
            "-fx-background-radius: 4px;" +
            "-fx-border-radius: 4px;" +
            "-fx-focus-color: " + NETFLIX_RED + ";" +
            "-fx-faint-focus-color: transparent;"
        );

        // Payment Information
        TextField cardNumberField = createStyledTextField("Card Number");
        TextField expiryMonthField = createStyledTextField("MM");
        TextField expiryYearField = createStyledTextField("YYYY");
        TextField cvvField = createStyledTextField("CVV");
        TextField cardHolderField = createStyledTextField("Card Holder Name");

        Button signupButton = createStyledButton("Sign Up");
        Text errorText = new Text();
        errorText.setStyle("-fx-fill: #ff4444; -fx-font-size: 14px;");

        signupButton.setOnAction(e -> {
            try {
                PaymentDetails paymentDetails = new PaymentDetails(
                    cardNumberField.getText(),
                    Integer.parseInt(expiryMonthField.getText()),
                    Integer.parseInt(expiryYearField.getText()),
                    cvvField.getText(),
                    cardHolderField.getText()
                );

                boolean success = regUserLogin.signupUser(
                    emailField.getText(),
                    firstNameField.getText(),
                    lastNameField.getText(),
                    passwordField.getText(),
                    addressField.getText(),
                    paymentDetails
                );

                if (success) {
                    showAlert("Success", "Account created successfully. Please login.", Alert.AlertType.INFORMATION);
                    TabPane tabPane = (TabPane) signupForm.getParent().getParent();
                    tabPane.getSelectionModel().select(0);
                } else {
                    errorText.setText("Failed to create account. Please try again.");
                }
            } catch (Exception ex) {
                errorText.setText(ex.getMessage());
            }
        });

        // Add section headers with improved styling
        Label personalInfoHeader = createSectionHeader("Personal Information");
        Label paymentInfoHeader = createSectionHeader("Payment Information");

        // Add fields to grid with improved layout
        int row = 0;
        grid.add(personalInfoHeader, 0, row++, 2, 1);
        grid.add(createFieldContainer(firstNameField), 0, row);
        grid.add(createFieldContainer(lastNameField), 1, row++);
        grid.add(createFieldContainer(emailField), 0, row++, 2, 1);
        grid.add(createFieldContainer(passwordField), 0, row++, 2, 1);
        grid.add(createFieldContainer(addressField), 0, row++, 2, 1);
        
        grid.add(paymentInfoHeader, 0, row++, 2, 1);
        grid.add(createFieldContainer(cardNumberField), 0, row++, 2, 1);
        
        // Create a payment details subgrid for better organization
        GridPane paymentDetailsGrid = new GridPane();
        paymentDetailsGrid.setHgap(10);
        paymentDetailsGrid.add(createFieldContainer(expiryMonthField), 0, 0);
        paymentDetailsGrid.add(createFieldContainer(expiryYearField), 1, 0);
        paymentDetailsGrid.add(createFieldContainer(cvvField), 2, 0);
        
        grid.add(paymentDetailsGrid, 0, row++, 2, 1);
        grid.add(createFieldContainer(cardHolderField), 0, row++, 2, 1);

        signupForm.getChildren().addAll(
            createHeader("Create New Account"),
            grid,
            signupButton,
            errorText
        );

        return signupForm;
    }

    private VBox createFieldContainer(Control field) {
        VBox container = new VBox(5);
        container.setStyle("-fx-background-radius: 4px;");
        container.getChildren().add(field);
        return container;
    }

    private Label createHeader(String text) {
        Label header = new Label(text);
        header.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 0 0 20 0;"
        );
        return header;
    }

    private Label createSectionHeader(String text) {
        Label header = new Label(text);
        header.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 20 0 10 0;"
        );
        return header;
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle(
            "-fx-background-color: " + INPUT_BACKGROUND + ";" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #808080;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12px;" +
            "-fx-background-radius: 4px;" +
            "-fx-border-radius: 4px;" +
            "-fx-focus-color: " + NETFLIX_RED + ";" +
            "-fx-faint-focus-color: transparent;"
        );
        return field;
    }

    private PasswordField createStyledPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setStyle(
            "-fx-background-color: " + INPUT_BACKGROUND + ";" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #808080;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12px;" +
            "-fx-background-radius: 4px;" +
            "-fx-border-radius: 4px;" +
            "-fx-focus-color: " + NETFLIX_RED + ";" +
            "-fx-faint-focus-color: transparent;"
        );
        return field;
    }

    private TextArea createStyledTextArea(String prompt) {
        TextArea area = new TextArea();
        area.setPromptText(prompt);
        area.setPrefRowCount(3);
        area.setWrapText(true);
        area.setStyle(
            "-fx-background-color: " + INPUT_BACKGROUND + ";" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #808080;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12px;" +
            "-fx-background-radius: 4px;" +
            "-fx-border-radius: 4px;" +
            "-fx-focus-color: " + NETFLIX_RED + ";" +
            "-fx-faint-focus-color: transparent;"
        );
        return area;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: " + NETFLIX_RED + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12 40;" +
            "-fx-background-radius: 4px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 8, 0, 0, 2);"
        );
        
        // Add hover effect
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #f40612;" + // Slightly lighter red on hover
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12 40;" +
            "-fx-background-radius: 4px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 10, 0, 0, 4);"
        ));
        
        // Restore original style on mouse exit
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: " + NETFLIX_RED + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12 40;" +
            "-fx-background-radius: 4px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 8, 0, 0, 2);"
        ));
        
        return button;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        // Style the alert dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" +
            "-fx-text-fill: white;"
        );
        dialogPane.getStyleClass().add("alert");
        
        // Style the buttons
        dialogPane.lookupButton(ButtonType.OK).setStyle(
            "-fx-background-color: " + NETFLIX_RED + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 4px;"
        );
        
        alert.showAndWait();
    }
}