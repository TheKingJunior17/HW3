package application;

import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the user.
 */
public class StaffHomePage {
	
    private final DatabaseHelper databaseHelper;

    public StaffHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        // Create a layout container (VBox)
        VBox layout = new VBox(20);  // Increased spacing for better layout
        layout.setStyle("-fx-alignment: center; -fx-padding: 40;");

        // Label to display "Hello Staff!"
        Label userLabel = new Label("Hello, Staff!");
        userLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        // Logout button stylalized
        Button logoutButton = new Button("Log Out");
        logoutButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20; -fx-background-color: #FF6347; -fx-text-fill: white;");
        
        // Handle LogOut action
        logoutButton.setOnAction(a -> {
            // Navigate to login page when log out button is clicked
            new UserLoginPage(databaseHelper).show(primaryStage);
        });

        // Add the label and button to the layout
        layout.getChildren().addAll(userLabel, logoutButton);

        // Create a new scene and set it to the primaryStage
        Scene userScene = new Scene(layout, 800, 400);
        primaryStage.setScene(userScene);
        primaryStage.setTitle("Staff Page");
        primaryStage.show();
    }
}
