package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import databasePart1.*;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */

public class WelcomeLoginPage {

    private final DatabaseHelper databaseHelper;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label welcomeLabel = new Label("Welcome, " + user.getUserName() + "!");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Grab the roles from your user object
        Set<String> roles = user.getOriginalRoles();
        System.out.println("User Roles: " + roles);

        // For each role, we add a corresponding button
        for (String role : roles) {
            String r = role.trim().toLowerCase();

            if (r.equals("admin")) {
                Button adminButton = new Button("Continue to Admin Page");
                adminButton.setOnAction(a -> {
                    // Show admin home
                    new AdminHomePage(databaseHelper, user.getUserName()).show(primaryStage);
                });
                layout.getChildren().add(adminButton);

                // Optionally add an Invite button for admins
                Button inviteButton = new Button("Invite");
                inviteButton.setOnAction(a -> new InvitationPage().show(databaseHelper, primaryStage));
                layout.getChildren().add(inviteButton);
            }
            else if (r.equals("student")) {
                Button studentButton = new Button("Continue to Student Page");
                studentButton.setOnAction(a -> {
                    // Show student home
                    new StudentHomePage(databaseHelper, user.getUserName()).show(primaryStage);
                });
                layout.getChildren().add(studentButton);
            }
            else if (r.equals("staff")) {
                Button staffButton = new Button("Continue to Staff Page");
                staffButton.setOnAction(a -> {
                    // Show staff home
                    new StaffHomePage(databaseHelper).show(primaryStage);
                });
                layout.getChildren().add(staffButton);
            }
            else if (r.equals("user")) {
                Button userButton = new Button("Continue to User Page");
                userButton.setOnAction(a -> {
                    // Show user home
                    new UserHomePage(databaseHelper).show(primaryStage);
                });
                layout.getChildren().add(userButton);
            }
            else if (r.equals("reviewer")) {
                Button reviewerButton = new Button("Continue to Reviewer Page");
                reviewerButton.setOnAction(a -> {
                    // Show reviewer home
                    new ReviewerHomePage(databaseHelper).show(primaryStage);
                });
                layout.getChildren().add(reviewerButton);
            }
        }

        // Quit button with confirmation
        Button quitButton = new Button("Quit");
        quitButton.setOnAction(a -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Are you sure you want to quit?");
            alert.setContentText("Any unsaved progress will be lost.");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    databaseHelper.closeConnection();
                    Platform.exit();
                }
            });
        });

        layout.getChildren().addAll(welcomeLabel, quitButton);

        Scene welcomeScene = new Scene(layout, 800, 400);
        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("Welcome Page");
        primaryStage.show();
    }
}