package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import databasePart1.*;

/**
 * The SetupLoginSelectionPage class allows users to choose between setting up a new account
 * or logging into an existing account. It provides two buttons for navigation to the respective pages.
 */
public class SetupLoginSelectionPage {
    
    private final DatabaseHelper databaseHelper;

    public SetupLoginSelectionPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        
        // Buttons to select Login / Setup options that redirect to respective pages
        Button setupButton = new Button("Set Up");
        Button loginButton = new Button("Log In");

        // Button styling for better visibility and user experience
        setupButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        loginButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20; -fx-background-color: #2196F3; -fx-text-fill: white;");

        // Event handlers for setup and login buttons
        setupButton.setOnAction(a -> {
            new SetupAccountPage(databaseHelper).show(primaryStage); // Redirect to Setup page
        });
        loginButton.setOnAction(a -> {
            new UserLoginPage(databaseHelper).show(primaryStage); // Redirect to Login page
        });

        // VBox layout setup
        VBox layout = new VBox(20); // Increased spacing for better layout
        layout.setStyle("-fx-padding: 40; -fx-alignment: center;");
        
        // Adding the buttons to the layout
        layout.getChildren().addAll(setupButton, loginButton);

        // Creating scene and setting it to the primaryStage
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Welcome - Choose Setup or Login");
        primaryStage.show();
    }
}
