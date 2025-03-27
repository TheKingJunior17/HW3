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
 * SetupAccountPage class handles the account setup process for new users. Users
 * provide their userName, password, name, email, and a valid invitation code to register.
 */
public class SetupAccountPage {

    private final DatabaseHelper databaseHelper;

    // DatabaseHelper to handle database operations.
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * 
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
        // Create UI components
        TextField userNameField = createTextField("Enter userName");
        PasswordField passwordField = createPasswordField("Enter Password");
        TextField inviteCodeField = createTextField("Enter InvitationCode");
        TextField nameField = createTextField("Enter your Name");
        TextField emailField = createTextField("Enter your Email");

        // Labels for error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Label userNameErrorLabel = new Label();
        userNameErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Label passwordErrorLabel = new Label();
        passwordErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // Setup Button with action handler
        Button setupButton = new Button("Setup");
        setupButton.setOnAction(a -> handleSetupButtonAction(userNameField, passwordField, inviteCodeField, 
                                                                 nameField, emailField, userNameErrorLabel, 
                                                             passwordErrorLabel, errorLabel, primaryStage));

        // Layout setup
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, inviteCodeField, nameField, emailField, 
                                    setupButton, errorLabel, userNameErrorLabel, passwordErrorLabel);

        // Show the scene
        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }

    // Helper method to create a TextField
    private TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setMaxWidth(250);
        return textField;
    }

    // Helper method to create a PasswordField
    private PasswordField createPasswordField(String promptText) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(promptText);
        passwordField.setMaxWidth(250);
        return passwordField;
    }

    // Handle the action when the setup button is clicked
    private void handleSetupButtonAction(TextField userNameField, PasswordField passwordField, TextField inviteCodeField, 
                                          TextField nameField, TextField emailField, Label userNameErrorLabel, 
                                          Label passwordErrorLabel, Label errorLabel, Stage primaryStage) {
        // Retrieve user input
        String userName = userNameField.getText();
        String password = passwordField.getText();
        String code = inviteCodeField.getText();
        String name = nameField.getText();
        String email = emailField.getText();

        // Clear previous error messages
        userNameErrorLabel.setText("");
        passwordErrorLabel.setText("");
        errorLabel.setText("");

        // Validate user input
        boolean errorFound = false;

        String userNameError = UserNameRecognizer.checkForValidUserName(userName);
        if (!userNameError.isEmpty()) {
            userNameErrorLabel.setText(userNameError);
            errorFound = true;
        }

        String passwordError = PasswordEvaluator.evaluatePassword(password);
        if (!passwordError.isEmpty()) {
            passwordErrorLabel.setText(passwordError);
            errorFound = true;
        }

        // Don't proceed if any errors were found
        if (errorFound) {
            return;
        }

        // Try to register the user in the database
        try {
            if (!databaseHelper.doesUserExist(userName)) {
                if (databaseHelper.validateInvitationCode(code)) {
                    Set<String> roles = new HashSet<>();
                    roles.add("user");
                    
                    User user = new User(userName, password, roles, name, email);
                    databaseHelper.register(user);

                    new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
                } else {
                    errorLabel.setText("Please enter a valid invitation code");
                }
            } else {
                errorLabel.setText("This userName is taken! Please use another username.");
            }
        } catch (SQLException e) {
            errorLabel.setText("Database error occurred. Please try again.");
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
