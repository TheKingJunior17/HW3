package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import databasePart1.*;

/**
 * The AdminSetupPage class handles the setup process for creating an administrator
 * account. This is intended to be used by the first user to initialize the
 * system with admin credentials.
 */
public class AdminSetupPage {

    private final DatabaseHelper databaseHelper;

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        // Input fields for userName, password, name, and email
        TextField userNameField = createTextField("Enter Admin userName");
        PasswordField passwordField = createPasswordField("Enter Password");
        TextField nameField = createTextField("Enter Admin Name");
        TextField emailField = createTextField("Enter Admin Email");

        Label userNameErrorLabel = createErrorLabel();
        Label passwordErrorLabel = createErrorLabel();

        Button setupButton = new Button("Setup");
        setupButton.setOnAction(e -> handleSetup(userNameField, passwordField, nameField, emailField, 
                                                  userNameErrorLabel, passwordErrorLabel, primaryStage));

        VBox layout = new VBox(10, userNameField, passwordField, nameField, emailField, setupButton, 
                              userNameErrorLabel, passwordErrorLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
    }

    // Create a simple text field with a prompt
    private TextField createTextField(String prompt) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setMaxWidth(250);
        return textField;
    }

    // Create a password field with a prompt
    private PasswordField createPasswordField(String prompt) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(prompt);
        passwordField.setMaxWidth(250);
        return passwordField;
    }

    // Create a label to display error messages
    private Label createErrorLabel() {
        Label label = new Label();
        label.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        return label;
    }

    // Handle the setup process for the admin user
    private void handleSetup(TextField userNameField, PasswordField passwordField, TextField nameField,
                              TextField emailField, Label userNameErrorLabel, Label passwordErrorLabel, Stage primaryStage) {
        // Retrieve user input
        String userName = userNameField.getText();
        String password = passwordField.getText();
        String name = nameField.getText();
        String email = emailField.getText();

        // Clear previous error messages
        userNameErrorLabel.setText("");
        passwordErrorLabel.setText("");

        // Validate input fields
        boolean errorFound = validateInputs(userName, password, userNameErrorLabel, passwordErrorLabel);

        if (errorFound) return; // Do not proceed if errors were found

        // Create user and register in the database
        try {
            Set<String> roles = new HashSet<>();
            roles.add("admin"); // Add the admin role
            roles.add("user");  // Add the user role
            roles.add("student");
            		
            // Create a new user with name and email
            User user = new User(userName, password, roles, name, email);

            databaseHelper.register(user);  // Register user in the database
            System.out.println("Administrator setup completed.");

            new WelcomeLoginPage(databaseHelper).show(primaryStage, user); // Navigate to login page
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Validate user inputs for username and password
    private boolean validateInputs(String userName, String password, Label userNameErrorLabel, Label passwordErrorLabel) {
        boolean errorFound = false;

        // Check for valid username
        String userNameError = UserNameRecognizer.checkForValidUserName(userName);
        if (!userNameError.isEmpty()) {
            userNameErrorLabel.setText(userNameError); // Display error message
            errorFound = true;
        }

        // Check for valid password
        String passwordError = PasswordEvaluator.evaluatePassword(password);
        if (!passwordError.isEmpty()) {
            passwordErrorLabel.setText(passwordError); // Display error message
            errorFound = true;
        }

        return errorFound;
    }
}
