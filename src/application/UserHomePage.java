package application;

import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the user and a logout button.
 */
public class UserHomePage {
    
    private final DatabaseHelper databaseHelper;

    // Constructor takes a DatabaseHelper instance
    public UserHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the User Home Page in the provided stage.
     * 
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
        VBox layout = new VBox(20); 
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Personalized greeting 
        Label userLabel = new Label("Hello, User!"); 
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Add the greeting label to the layout
        layout.getChildren().add(userLabel);
        
        // Button to log out
        Button logoutButton = new Button("Log Out");
        logoutButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-color: #f44336; -fx-text-fill: white;"); // Styling the logout button
        logoutButton.setOnAction(a -> { 
            new UserLoginPage(databaseHelper).show(primaryStage);
        });
        
        // Add the logout button to the layout
        layout.getChildren().add(logoutButton);

        // Set up the scene and show it in the primary stage
        Scene userScene = new Scene(layout, 800, 400);
        primaryStage.setScene(userScene);
        primaryStage.setTitle("User Page");
        primaryStage.show();
    }
}
