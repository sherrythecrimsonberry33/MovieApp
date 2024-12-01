// package frontend;

// import backend.Entity.PaymentDetails;
// import backend.Entity.RegUserLogin;
// import backend.actors.RegisteredUser;
// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.Scene;
// import javafx.scene.control.*;
// import javafx.scene.layout.GridPane;
// import javafx.scene.layout.HBox;
// import javafx.scene.layout.StackPane;
// import javafx.scene.layout.VBox;
// import javafx.stage.Stage;
// import javafx.scene.text.Text;
// import javafx.scene.Node;


// public class LoginView {
//     private static final String DARK_BACKGROUND = "#1a1a1a";
//     private static final String NETFLIX_RED = "#e50914";
//     private static final String CARD_BACKGROUND = "#1a1a1a";
//     private static final String INPUT_BACKGROUND = "#3a3a3a";
    
//     private Stage parentStage;
//     private Stage loginStage;
//     private RegUserLogin regUserLogin;
    
//     public LoginView(Stage parentStage) {
//         this.parentStage = parentStage;
//         this.loginStage = new Stage();
//         this.regUserLogin = new RegUserLogin();
//     }
    
        
//     public void show() {
//         VBox mainLayout = new VBox(20);
//         mainLayout.setStyle("-fx-background-color: " + DARK_BACKGROUND + ";");
//         mainLayout.setPadding(new Insets(40));
//         mainLayout.setAlignment(Pos.CENTER);
    
//         // Create theme buttons instead of tabs
//         HBox buttonBox = new HBox(20);
//         buttonBox.setAlignment(Pos.CENTER);
    
//         Button loginButton = createThemeButton("Login");
//         Button signupButton = createThemeButton("Sign Up");
    
//         buttonBox.getChildren().addAll(loginButton, signupButton);
    
//         // Create a container for the forms
//         StackPane formContainer = new StackPane();
    
//         // Create login and signup forms
//         VBox loginForm = createLoginForm();
//         ScrollPane signupForm = createSignupForm();  // Note: This returns ScrollPane
    
//         formContainer.getChildren().addAll(loginForm, signupForm);
    
//         // Initially show login form
//         loginForm.setVisible(true);
//         signupForm.setVisible(false);
    
//         // Button click handlers
//         loginButton.setOnAction(e -> {
//             loginForm.setVisible(true);
//             signupForm.setVisible(false);
//             loginButton.setStyle(getSelectedButtonStyle());
//             signupButton.setStyle(getDefaultButtonStyle());
//         });
    
//         signupButton.setOnAction(e -> {
//             loginForm.setVisible(false);
//             signupForm.setVisible(true);
//             signupButton.setStyle(getSelectedButtonStyle());
//             loginButton.setStyle(getDefaultButtonStyle());
//         });
    
//         // Set initial button styles
//         loginButton.setStyle(getSelectedButtonStyle());
//         signupButton.setStyle(getDefaultButtonStyle());
    
//         // Add hover effects to buttons
//         loginButton.setOnMouseEntered(e -> {
//             if (!loginForm.isVisible()) {
//                 loginButton.setStyle(getHoverButtonStyle());
//             }
//         });
//         loginButton.setOnMouseExited(e -> {
//             loginButton.setStyle(loginForm.isVisible() ? getSelectedButtonStyle() : getDefaultButtonStyle());
//         });
    
//         signupButton.setOnMouseEntered(e -> {
//             if (!signupForm.isVisible()) {
//                 signupButton.setStyle(getHoverButtonStyle());
//             }
//         });
//         signupButton.setOnMouseExited(e -> {
//             signupButton.setStyle(signupForm.isVisible() ? getSelectedButtonStyle() : getDefaultButtonStyle());
//         });
    
//         mainLayout.getChildren().addAll(buttonBox, formContainer);
    
//         Scene scene = new Scene(mainLayout, 600, 800);
//         loginStage.setTitle("AcmePlex - User Login");
//         loginStage.setScene(scene);
//         loginStage.show();
//     }
    
//     private Button createThemeButton(String text) {
//         Button button = new Button(text);
//         button.setMinWidth(200);  // Set minimum width for buttons
//         button.setStyle(getDefaultButtonStyle());
//         return button;
//     }
    
//     private String getDefaultButtonStyle() {
//         return "-fx-background-color: " + DARK_BACKGROUND + ";" +
//                "-fx-text-fill: #808080;" +  // Gray color for unselected
//                "-fx-font-size: 16px;" +
//                "-fx-font-weight: bold;" +
//                "-fx-padding: 10 20;" +
//                "-fx-cursor: hand;" +
//                "-fx-border-width: 0;";
//     }
    
//     private String getSelectedButtonStyle() {
//         return "-fx-background-color: " + DARK_BACKGROUND + ";" +
//                "-fx-text-fill: white;" +  // White color for selected
//                "-fx-font-size: 16px;" +
//                "-fx-font-weight: bold;" +
//                "-fx-padding: 10 20;" +
//                "-fx-cursor: hand;" +
//                "-fx-border-width: 0 0 2 0;" +
//                "-fx-border-color: " + NETFLIX_RED + ";";
//     }
    
//     private String getHoverButtonStyle() {
//         return "-fx-background-color: " + DARK_BACKGROUND + ";" +
//                "-fx-text-fill: white;" +  // White color on hover
//                "-fx-font-size: 16px;" +
//                "-fx-font-weight: bold;" +
//                "-fx-padding: 10 20;" +
//                "-fx-cursor: hand;" +
//                "-fx-border-width: 0;";
//     }
    


//     private VBox createLoginForm() {
//         VBox loginForm = new VBox(25); // Increased spacing
//         loginForm.setStyle(
//             "-fx-background-color: " + CARD_BACKGROUND + ";" +
//             "-fx-padding: 40px;" +
//             "-fx-background-radius: 8px;"
//         );
//         loginForm.setPadding(new Insets(40));
//         loginForm.setAlignment(Pos.CENTER);

//         TextField emailField = createStyledTextField("Email");
//         PasswordField passwordField = createStyledPasswordField("Password");
//         Button loginButton = createStyledButton("Login");
//         Text errorText = new Text();
//         errorText.setStyle("-fx-fill: #ff4444; -fx-font-size: 14px;");

//         loginButton.setOnAction(e -> {
//             String email = emailField.getText();
//             String password = passwordField.getText();
            
//             RegisteredUser user = regUserLogin.authenticateUser(email, password);
//             if (user != null) {
//                 MovieListingView movieView = new MovieListingView();
//                 movieView.setLoggedInUser(user);
//                 movieView.start(new Stage());
                
//                 loginStage.close();
//                 parentStage.close();
//             } else {
//                 errorText.setText("Invalid email or password");
//             }
//         });

//         loginForm.getChildren().addAll(
//             createHeader("Login to Your Account"),
//             createFieldContainer(emailField),
//             createFieldContainer(passwordField),
//             loginButton,
//             errorText
//         );

//         return loginForm;
//     }

//     private ScrollPane createSignupForm() {
//         ScrollPane scrollPane = new ScrollPane();
//         scrollPane.setFitToWidth(true);
//         scrollPane.setStyle(
//             "-fx-background: transparent;" +
//             "-fx-background-color: transparent;" +
//             "-fx-padding: 0;" +
//             "-fx-border-width: 0;"
//         );

//         // In createSignupForm(), modify the ScrollPane styling:
//         scrollPane.setStyle(
 
//             "-fx-padding: 0;" +
//             "-fx-border-width: 0;" +
//             "-fx-background-insets: 0;" +
//             "-fx-border-insets: 0;" +
//             "-fx-control-inner-background: " + DARK_BACKGROUND + ";"
//         );

  

//         VBox signupForm = new VBox(25); // Increased spacing
//         signupForm.setStyle(
//             "-fx-background-color: " + CARD_BACKGROUND + ";" +
//             "-fx-padding: 40px;" +
//             "-fx-background-radius: 8px;"
//         );
//         signupForm.setPadding(new Insets(40));
//         signupForm.setAlignment(Pos.CENTER);

//         GridPane grid = new GridPane();
//         grid.setHgap(20);
//         grid.setVgap(20);
//         grid.setAlignment(Pos.CENTER);

//         // Personal Information
//         TextField firstNameField = createStyledTextField("First Name");
//         TextField lastNameField = createStyledTextField("Last Name");
//         TextField emailField = createStyledTextField("Email");
//         PasswordField passwordField = createStyledPasswordField("Password");
//         TextArea addressField = createStyledTextArea("Address");
//         addressField.setStyle(
//             "-fx-background-color: " + INPUT_BACKGROUND + ";" +
//             "-fx-text-fill: white;" +
//             "-fx-prompt-text-fill: #808080;" +
//             "-fx-font-size: 14px;" +
//             "-fx-padding: 12px;" +
//             "-fx-background-radius: 4px;" +
//             "-fx-border-radius: 4px;" +
//             "-fx-focus-color: " + NETFLIX_RED + ";" +
//             "-fx-faint-focus-color: transparent;" +
//             "-fx-control-inner-background: " + INPUT_BACKGROUND + ";" + // This fixes the white background
//             "-fx-text-box-border: transparent;" +
//             "-fx-background-insets: 0;" +
//             "-fx-border-color: transparent;"
//         );

//         // Payment Information
//         TextField cardNumberField = createStyledTextField("Card Number");
//         TextField expiryMonthField = createStyledTextField("MM");
//         TextField expiryYearField = createStyledTextField("YYYY");
//         TextField cvvField = createStyledTextField("CVV");
//         TextField cardHolderField = createStyledTextField("Card Holder Name");


//         VBox paymentCard = new VBox(10);
//         paymentCard.setStyle(
//             "-fx-background-color: " + CARD_BACKGROUND + ";" +
//             "-fx-padding: 20px;" +
//             "-fx-background-radius: 8px;" +
//             "-fx-border-color: " + NETFLIX_RED + ";" +
//             "-fx-border-radius: 8px;" +
//             "-fx-border-width: 1px;"
//         );
//         paymentCard.setAlignment(Pos.CENTER);

//         Label feeLabel = new Label("Annual Membership Fee: $20.00");
//         feeLabel.setStyle(
//             "-fx-text-fill: white;" +
//             "-fx-font-size: 18px;" +
//             "-fx-font-weight: bold;"
//         );

//         Label feeDescription = new Label(
//             "By signing up, you agree to pay the annual membership fee of $20.00.\n" +
//             "Benefits include:\n" +
//             "• No cancellation fees\n" +
//             "• Early access to movie bookings\n" +
//             "• Exclusive movie news and announcements"
//         );
//         feeDescription.setStyle(
//             "-fx-text-fill: #cccccc;" +
//             "-fx-font-size: 14px;"
//         );
//         feeDescription.setWrapText(true);

//         paymentCard.getChildren().addAll(feeLabel, feeDescription);

//         Button signupButton = createStyledButton("Sign Up");
//         Text errorText = new Text();
//         errorText.setStyle("-fx-fill: #ff4444; -fx-font-size: 14px;");

//         signupButton.setOnAction(e -> {
//             try {
//                 PaymentDetails paymentDetails = new PaymentDetails(
//                     cardNumberField.getText(),
//                     Integer.parseInt(expiryMonthField.getText()),
//                     Integer.parseInt(expiryYearField.getText()),
//                     cvvField.getText(),
//                     cardHolderField.getText()
//                 );

//                 boolean success = regUserLogin.signupUser(
//                     emailField.getText(),
//                     firstNameField.getText(),
//                     lastNameField.getText(),
//                     passwordField.getText(),
//                     addressField.getText(),
//                     paymentDetails
//                 );

//                 if (success) {
//                     showAlert("Success", "Account created successfully. Please login.", Alert.AlertType.INFORMATION);
//                     Node parent = signupForm.getParent();
//         while (parent != null && !(parent instanceof HBox)) {
//             parent = parent.getParent();
//         }
//         if (parent != null) {
//             HBox buttonBox = (HBox) parent;
//             // Find and click the login button
//             buttonBox.getChildren().stream()
//                 .filter(node -> node instanceof Button && 
//                         ((Button) node).getText().equals("Login"))
//                 .findFirst()
//                 .ifPresent(button -> ((Button) button).fire());
//         }
    
//                 } else {
//                     errorText.setText("Failed to create account. Please try again.");
//                 }
//             } catch (Exception ex) {
//                 errorText.setText(ex.getMessage());
//             }
//         });

//         // Add section headers with improved styling
//         Label personalInfoHeader = createSectionHeader("Personal Information");
//         Label paymentInfoHeader = createSectionHeader("Payment Information");

//         // Add fields to grid with improved layout
//         int row = 0;
//         grid.add(personalInfoHeader, 0, row++, 2, 1);
//         grid.add(createFieldContainer(firstNameField), 0, row);
//         grid.add(createFieldContainer(lastNameField), 1, row++);
//         grid.add(createFieldContainer(emailField), 0, row++, 2, 1);
//         grid.add(createFieldContainer(passwordField), 0, row++, 2, 1);
//         grid.add(createFieldContainer(addressField), 0, row++, 2, 1);
        
//         grid.add(paymentInfoHeader, 0, row++, 2, 1);
//         grid.add(createFieldContainer(cardNumberField), 0, row++, 2, 1);
        
//         // Create a payment details subgrid for better organization
//         GridPane paymentDetailsGrid = new GridPane();
//         paymentDetailsGrid.setHgap(10);
//         paymentDetailsGrid.add(createFieldContainer(expiryMonthField), 0, 0);
//         paymentDetailsGrid.add(createFieldContainer(expiryYearField), 1, 0);
//         paymentDetailsGrid.add(createFieldContainer(cvvField), 2, 0);
        
//         grid.add(paymentDetailsGrid, 0, row++, 2, 1);
//         grid.add(createFieldContainer(cardHolderField), 0, row++, 2, 1);

//         signupForm.getChildren().addAll(
//             createHeader("Create New Account"),
//             grid,
//             paymentCard,
//             signupButton,
//             errorText
//         );

//         scrollPane.setContent(signupForm);
//         return scrollPane; 
//     }

//     private VBox createFieldContainer(Control field) {
//         VBox container = new VBox(5);
//         container.setStyle("-fx-background-radius: 4px;");
//         container.getChildren().add(field);
//         return container;
//     }

//     private Label createHeader(String text) {
//         Label header = new Label(text);
//         header.setStyle(
//             "-fx-font-size: 28px;" +
//             "-fx-font-weight: bold;" +
//             "-fx-text-fill: white;" +
//             "-fx-padding: 0 0 20 0;"
//         );
//         return header;
//     }

//     private Label createSectionHeader(String text) {
//         Label header = new Label(text);
//         header.setStyle(
//             "-fx-font-size: 18px;" +
//             "-fx-font-weight: bold;" +
//             "-fx-text-fill: white;" +
//             "-fx-padding: 20 0 10 0;"
//         );
//         return header;
//     }

//     private TextField createStyledTextField(String prompt) {
//         TextField field = new TextField();
//         field.setPromptText(prompt);
//         field.setStyle(
//             "-fx-background-color: " + INPUT_BACKGROUND + ";" +
//             "-fx-text-fill: white;" +
//             "-fx-prompt-text-fill: #808080;" +
//             "-fx-font-size: 14px;" +
//             "-fx-padding: 12px;" +
//             "-fx-background-radius: 4px;" +
//             "-fx-border-radius: 4px;" +
//             "-fx-focus-color: " + NETFLIX_RED + ";" +
//             "-fx-faint-focus-color: transparent;"
//         );
//         return field;
//     }

//     private PasswordField createStyledPasswordField(String prompt) {
//         PasswordField field = new PasswordField();
//         field.setPromptText(prompt);
//         field.setStyle(
//             "-fx-background-color: " + INPUT_BACKGROUND + ";" +
//             "-fx-text-fill: white;" +
//             "-fx-prompt-text-fill: #808080;" +
//             "-fx-font-size: 14px;" +
//             "-fx-padding: 12px;" +
//             "-fx-background-radius: 4px;" +
//             "-fx-border-radius: 4px;" +
//             "-fx-focus-color: " + NETFLIX_RED + ";" +
//             "-fx-faint-focus-color: transparent;"
//         );
//         return field;
//     }

//     private TextArea createStyledTextArea(String prompt) {
//         TextArea area = new TextArea();
//         area.setPromptText(prompt);
//         area.setPrefRowCount(3);
//         area.setWrapText(true);
//         area.setStyle(
//             "-fx-background-color: " + INPUT_BACKGROUND + ";" +
//             "-fx-text-fill: white;" +
//             "-fx-prompt-text-fill: #808080;" +
//             "-fx-font-size: 14px;" +
//             "-fx-padding: 12px;" +
//             "-fx-background-radius: 4px;" +
//             "-fx-border-radius: 4px;" +
//             "-fx-focus-color: " + NETFLIX_RED + ";" +
//             "-fx-faint-focus-color: transparent;"
//         );
//         return area;
//     }

//     private Button createStyledButton(String text) {
//         Button button = new Button(text);
//         button.setStyle(
//             "-fx-background-color: " + NETFLIX_RED + ";" +
//             "-fx-text-fill: white;" +
//             "-fx-font-weight: bold;" +
//             "-fx-font-size: 14px;" +
//             "-fx-padding: 12 40;" +
//             "-fx-background-radius: 4px;" +
//             "-fx-cursor: hand;" +
//             "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 8, 0, 0, 2);"
//         );
        
//         // Add hover effect
//         button.setOnMouseEntered(e -> button.setStyle(
//             "-fx-background-color: #f40612;" + // Slightly lighter red on hover
//             "-fx-text-fill: white;" +
//             "-fx-font-weight: bold;" +
//             "-fx-font-size: 14px;" +
//             "-fx-padding: 12 40;" +
//             "-fx-background-radius: 4px;" +
//             "-fx-cursor: hand;" +
//             "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 10, 0, 0, 4);"
//         ));
        
//         // Restore original style on mouse exit
//         button.setOnMouseExited(e -> button.setStyle(
//             "-fx-background-color: " + NETFLIX_RED + ";" +
//             "-fx-text-fill: white;" +
//             "-fx-font-weight: bold;" +
//             "-fx-font-size: 14px;" +
//             "-fx-padding: 12 40;" +
//             "-fx-background-radius: 4px;" +
//             "-fx-cursor: hand;" +
//             "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 8, 0, 0, 2);"
//         ));
        
//         return button;
//     }

//     private void showAlert(String title, String content, Alert.AlertType type) {
//         Alert alert = new Alert(type);
//         alert.setTitle(title);
//         alert.setHeaderText(null);
//         alert.setContentText(content);
        
//         // Style the alert dialog
//         DialogPane dialogPane = alert.getDialogPane();
//         dialogPane.setStyle(
//             "-fx-background-color: " + CARD_BACKGROUND + ";" +
//             "-fx-text-fill: white;"
//         );
//         dialogPane.getStyleClass().add("alert");
        
//         // Style the buttons
//         dialogPane.lookupButton(ButtonType.OK).setStyle(
//             "-fx-background-color: " + NETFLIX_RED + ";" +
//             "-fx-text-fill: white;" +
//             "-fx-font-weight: bold;" +
//             "-fx-background-radius: 4px;"
//         );
        
//         alert.showAndWait();
//     }
// }

package frontend;

import backend.Entity.PaymentDetails;
import backend.Entity.RegUserLogin;
import backend.actors.RegisteredUser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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

    // Declare forms and buttons as instance variables
    private VBox loginForm;
    private ScrollPane signupForm;
    private Button loginButton;
    private Button signupButton;
    private Button loginSubmitButton;  // For login form submit button

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

        // Initialize buttons
        loginButton = createThemeButton("Login");
        signupButton = createThemeButton("Sign Up");

        // Create theme buttons instead of tabs
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        buttonBox.getChildren().addAll(loginButton, signupButton);

        // Create a container for the forms
        StackPane formContainer = new StackPane();

        // Initialize login and signup forms
        loginForm = createLoginForm();
        signupForm = createSignupForm();

        // Initially show login form
        loginForm.setVisible(true);
        loginForm.setManaged(true);
        signupForm.setVisible(false);
        signupForm.setManaged(false);

        formContainer.getChildren().addAll(loginForm, signupForm);

        // Button click handlers
        loginButton.setOnAction(e -> {
            loginForm.setVisible(true);
            loginForm.setManaged(true);
            loginForm.toFront(); // Bring loginForm to the front
            signupForm.setVisible(false);
            signupForm.setManaged(false);
            loginButton.setStyle(getSelectedButtonStyle());
            signupButton.setStyle(getDefaultButtonStyle());
        });

        signupButton.setOnAction(e -> {
            signupForm.setVisible(true);
            signupForm.setManaged(true);
            signupForm.toFront(); // Bring signupForm to the front
            loginForm.setVisible(false);
            loginForm.setManaged(false);
            signupButton.setStyle(getSelectedButtonStyle());
            loginButton.setStyle(getDefaultButtonStyle());
        });

        // Set initial button styles
        loginButton.setStyle(getSelectedButtonStyle());
        signupButton.setStyle(getDefaultButtonStyle());

        // Add hover effects to buttons
        loginButton.setOnMouseEntered(e -> {
            if (!loginForm.isVisible()) {
                loginButton.setStyle(getHoverButtonStyle());
            }
        });
        loginButton.setOnMouseExited(e -> {
            loginButton.setStyle(loginForm.isVisible() ? getSelectedButtonStyle() : getDefaultButtonStyle());
        });

        signupButton.setOnMouseEntered(e -> {
            if (!signupForm.isVisible()) {
                signupButton.setStyle(getHoverButtonStyle());
            }
        });
        signupButton.setOnMouseExited(e -> {
            signupButton.setStyle(signupForm.isVisible() ? getSelectedButtonStyle() : getDefaultButtonStyle());
        });

        mainLayout.getChildren().addAll(buttonBox, formContainer);

        Scene scene = new Scene(mainLayout, 600, 800);
        loginStage.setTitle("AcmePlex - User Login");
        loginStage.setScene(scene);
        loginStage.show();
    }

    private Button createThemeButton(String text) {
        Button button = new Button(text);
        button.setMinWidth(200);  // Set minimum width for buttons
        button.setStyle(getDefaultButtonStyle());
        return button;
    }

    private String getDefaultButtonStyle() {
        return "-fx-background-color: " + DARK_BACKGROUND + ";" +
               "-fx-text-fill: #808080;" +  // Gray color for unselected
               "-fx-font-size: 16px;" +
               "-fx-font-weight: bold;" +
               "-fx-padding: 10 20;" +
               "-fx-cursor: hand;" +
               "-fx-border-width: 0;";
    }

    private String getSelectedButtonStyle() {
        return "-fx-background-color: " + DARK_BACKGROUND + ";" +
               "-fx-text-fill: white;" +  // White color for selected
               "-fx-font-size: 16px;" +
               "-fx-font-weight: bold;" +
               "-fx-padding: 10 20;" +
               "-fx-cursor: hand;" +
               "-fx-border-width: 0 0 2 0;" +
               "-fx-border-color: " + NETFLIX_RED + ";";
    }

    private String getHoverButtonStyle() {
        return "-fx-background-color: " + DARK_BACKGROUND + ";" +
               "-fx-text-fill: white;" +  // White color on hover
               "-fx-font-size: 16px;" +
               "-fx-font-weight: bold;" +
               "-fx-padding: 10 20;" +
               "-fx-cursor: hand;" +
               "-fx-border-width: 0;";
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
        loginSubmitButton = createStyledButton("Login");  // Use instance variable
        Text errorText = new Text();
        errorText.setStyle("-fx-fill: #ff4444; -fx-font-size: 14px;");

        loginSubmitButton.setOnAction(e -> {
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
            loginSubmitButton,
            errorText
        );

        return loginForm;
    }

    private ScrollPane createSignupForm() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
            "-fx-padding: 0;" +
            "-fx-border-width: 0;" +
            "-fx-background-insets: 0;" +
            "-fx-border-insets: 0;" +
            "-fx-control-inner-background: " + DARK_BACKGROUND + ";"
        );

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

        // Payment Information
        TextField cardNumberField = createStyledTextField("Card Number");
        TextField expiryMonthField = createStyledTextField("MM");
        TextField expiryYearField = createStyledTextField("YYYY");
        TextField cvvField = createStyledTextField("CVV");
        TextField cardHolderField = createStyledTextField("Card Holder Name");

        VBox paymentCard = new VBox(10);
        paymentCard.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" +
            "-fx-padding: 20px;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-color: " + NETFLIX_RED + ";" +
            "-fx-border-radius: 8px;" +
            "-fx-border-width: 1px;"
        );
        paymentCard.setAlignment(Pos.CENTER);

        Label feeLabel = new Label("Annual Membership Fee: $20.00");
        feeLabel.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );

        Label feeDescription = new Label(
            "By signing up, you agree to pay the annual membership fee of $20.00.\n" +
            "Benefits include:\n" +
            "• No cancellation fees\n" +
            "• Early access to movie bookings\n" +
            "• Exclusive movie news and announcements"
        );
        feeDescription.setStyle(
            "-fx-text-fill: #cccccc;" +
            "-fx-font-size: 14px;"
        );
        feeDescription.setWrapText(true);

        paymentCard.getChildren().addAll(feeLabel, feeDescription);

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

                    // Switch back to login form
                    signupForm.setVisible(false);
                    signupForm.setManaged(false);
                    loginForm.setVisible(true);
                    loginForm.setManaged(true);
                    loginForm.toFront(); 

                    // Update button styles
                    loginButton.setStyle(getSelectedButtonStyle());
                    signupButton.setStyle(getDefaultButtonStyle());

                    // Clear the signup form fields
                    firstNameField.clear();
                    lastNameField.clear();
                    emailField.clear();
                    passwordField.clear();
                    addressField.clear();
                    cardNumberField.clear();
                    expiryMonthField.clear();
                    expiryYearField.clear();
                    cvvField.clear();
                    cardHolderField.clear();

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
            paymentCard,
            signupButton,
            errorText
        );

        scrollPane.setContent(signupForm);
        return scrollPane;
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
