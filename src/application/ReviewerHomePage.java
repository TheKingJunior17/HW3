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
public class ReviewerHomePage {

    private final DatabaseHelper databaseHelper;

    public ReviewerHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        VBox layout = createLayout();  // Create and configure the layout

        // Add a welcoming label
        layout.getChildren().add(createLabel("Hello, Reviewer!", "-fx-font-size: 16px; -fx-font-weight: bold;"));

        // Add the log out button
        Button logoutButton = createLogoutButton(primaryStage);
        layout.getChildren().add(logoutButton);

        // Set the scene to the primary stage
        Scene userScene = new Scene(layout, 800, 400);
        primaryStage.setScene(userScene);
        primaryStage.setTitle("Reviewer Page");
    }

    // Helper method to create and return a VBox
    private VBox createLayout() {
        VBox layout = new VBox(10); 
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        return layout;
    }

    // Helper method to create a Label
    private Label createLabel(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        return label;
    }

    // Helper method to create the Log Out button
    private Button createLogoutButton(Stage primaryStage) {
        Button logoutButton = new Button("Log Out");
        logoutButton.setOnAction(a -> {
            new UserLoginPage(databaseHelper).show(primaryStage); // Transition to login page
        });
        return logoutButton;
    }
}
