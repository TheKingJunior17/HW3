package application;

import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * InvitationPage class represents the page where an admin can generate an invitation code.
 * The invitation code is displayed upon clicking a button.
 */
public class InvitationPage {

    private static final String ERROR_MESSAGE = "Error: Unable to create the code.";
    private static final String EXPIRATION_TIME = "Expires in 10 minutes.";

    private VBox layout;
    private Label inviteCodeLabel;
    private Label expirationLabel;

    /**
     * Displays the Invite Page in the provided primary stage.
     *
     * @param databaseHelper An instance of DatabaseHelper to handle database operations.
     * @param primaryStage   The primary stage where the scene will be displayed.
     */
    public void show(DatabaseHelper databaseHelper, Stage primaryStage) {
        layout = createLayout();  // Initialize the layout
        inviteCodeLabel = createLabel("", "-fx-font-size: 14px; -fx-font-style: italic;");
        expirationLabel = createLabel("", "-fx-font-size: 14px; -fx-font-style: italic;");

        // Button to generate the invitation code
        Button showCodeButton = createShowCodeButton(databaseHelper);

        // Add components to layout
        layout.getChildren().addAll(
            createLabel("Invite", "-fx-font-size: 16px; -fx-font-weight: bold;"),
            showCodeButton,
            inviteCodeLabel,
            expirationLabel
        );

        // Set the scene to primary stage
        Scene inviteScene = new Scene(layout, 800, 400);
        primaryStage.setScene(inviteScene);
        primaryStage.setTitle("Invite Page");
    }

    // Creates and returns a VBox layout with styling
    private VBox createLayout() {
        VBox layout = new VBox(10); // Added space between components for better visual structure
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        return layout;
    }

    // Creates a label with the given text and styling
    private Label createLabel(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        return label;
    }

    // Creates a button that generates the invitation code when clicked
    private Button createShowCodeButton(DatabaseHelper databaseHelper) {
        Button showCodeButton = new Button("Generate Invitation Code");
        showCodeButton.setOnAction(e -> handleGenerateCode(databaseHelper));
        return showCodeButton;
    }

    // Handles the generation of the invitation code and manages UI updates
    private void handleGenerateCode(DatabaseHelper databaseHelper) {
        try {
            // Generate the invitation code using the databaseHelper
            String invitationCode = databaseHelper.generateInvitationCode();

            // Update labels with generated code and expiration time
            updateInviteCodeLabel(invitationCode);
            updateExpirationLabel(EXPIRATION_TIME);
        } catch (Exception e) {
            handleError();
        }
    }

    // Updates the invite code label with the given code
    private void updateInviteCodeLabel(String code) {
        inviteCodeLabel.setText(code);
    }

    // Updates the expiration label with the expiration message
    private void updateExpirationLabel(String expirationMessage) {
        expirationLabel.setText(expirationMessage);
    }

    // Handles errors by updating labels
    private void handleError() {
        updateInviteCodeLabel(ERROR_MESSAGE);
        updateExpirationLabel(""); 
    }
}
