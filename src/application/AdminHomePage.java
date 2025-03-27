package application;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin and allows role management.
 */
public class AdminHomePage {

    private final DatabaseHelper databaseHelper;
    private final String currentUserName;

    public AdminHomePage(DatabaseHelper databaseHelper, String currentUserName) {
        this.databaseHelper = databaseHelper;
        this.currentUserName = currentUserName;
    }

    /**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
        VBox layout = createLayout(primaryStage); 
        Scene scene = new Scene(layout, 800, 400);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Home Page");
        primaryStage.show();
    }

    private VBox createLayout(Stage primaryStage) { 
        VBox layout = new VBox(20);
        layout.setStyle("-fx-alignment: center;");
        
        Label adminLabel = createLabel("Hello, Admin!");
        layout.getChildren().add(adminLabel);

        layout.getChildren().addAll(
            createButton("Add Role to User", e -> handleAddRole()),
            createButton("Remove Role from User", e -> handleRemoveRole()),
            createButton("Remove User", e -> handleRemoveUser()),
            createButton("Set Temporary Password", e -> handleSetTemporaryPassword()),
            createButton("Log Out", e -> handleLogout()),
            createButton("Display All Users", e -> showAllUsers(primaryStage)) // Pass primaryStage here
        );

        return layout;
    }
    
    private void showAllUsers(Stage primaryStage) {
        try {
            // Fetch all users from the database
            List<User> users = databaseHelper.getAllUsers();

            // Create a table view to display the users
            TableView<User> tableView = new TableView<>();

            // Create columns for name, username, role, and email
            TableColumn<User, String> nameColumn = new TableColumn<>("Name");
            nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

            TableColumn<User, String> userNameColumn = new TableColumn<>("Username");
            userNameColumn.setCellValueFactory(cellData -> cellData.getValue().userNameProperty());

            TableColumn<User, String> roleColumn = new TableColumn<>("Role");
            roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());

            TableColumn<User, String> emailColumn = new TableColumn<>("Email");
            emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());

            // Add columns to the table
            tableView.getColumns().addAll(nameColumn, userNameColumn, roleColumn, emailColumn);

            // Set the data for the table
            tableView.getItems().addAll(users);

            // Create a new scene and show it in a new window
            VBox layout = new VBox(10, tableView);
            Scene scene = new Scene(layout, 800, 400);
            Stage userListStage = new Stage();
            userListStage.setTitle("User List");
            userListStage.setScene(scene);
            userListStage.show();

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Error fetching users from database.");
        }
    }

    // Method to show an error dialog
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        return label;
    }

    private Button createButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button(text);
        button.setOnAction(handler);
        return button;
    }

    private void handleAddRole() {
        String targetUser = promptForInput("Add Role", "Enter the username of the target user:", "Username:");
        if (targetUser == null || targetUser.isEmpty()) return;

        String role = promptForInput("Add Role", "Enter the role to add:", "Role:");
        if (role == null || role.isEmpty()) return;

        try {
            boolean success = databaseHelper.addRoleToUser(targetUser, role);
            showAlert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, 
                      success ? "Success" : "Error", 
                      success ? "Role \"" + role + "\" added to " + targetUser 
                              : "Failed to add role. It might already exist or the user was not found.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage());
        }
    }

    private void handleRemoveRole() {
        String targetUser = promptForInput("Remove Role", "Enter the username of the target user:", "Username:");
        if (targetUser == null || targetUser.isEmpty()) return;

        String role = promptForInput("Remove Role", "Enter the role to remove:", "Role:");
        if (role == null || role.isEmpty()) return;

        // Prevent admin from removing their own role
        if (targetUser.equals(currentUserName) && role.equalsIgnoreCase("admin")) {
            showAlert(Alert.AlertType.ERROR, "Error", "You cannot remove the admin role from your own account.");
            return;
        }

        try {
            // Ensure at least one admin exists
            if (role.equalsIgnoreCase("admin")) {
                int adminCount = databaseHelper.countAdminUsers();
                if (adminCount <= 1) {
                    showAlert(Alert.AlertType.ERROR, "Error", "There must be at least one admin in the system.");
                    return;
                }
            }
            boolean success = databaseHelper.removeRoleFromUser(targetUser, role);
            showAlert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, 
                      success ? "Success" : "Error", 
                      success ? "Role \"" + role + "\" removed from " + targetUser 
                              : "Failed to remove role. It may not exist or the user was not found.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage());
        }
    }
    
    

    private void handleRemoveUser() {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Remove User");
        inputDialog.setHeaderText(null);
        inputDialog.setContentText("Username:");

        inputDialog.showAndWait().ifPresent(username -> {
            if (username.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Username cannot be empty.");
                return;
            }
            String userRole = databaseHelper.getUserRole(username);
            if (userRole == null) {
                showAlert(Alert.AlertType.ERROR, "User Not Found", "No such user exists.");
                return;
            }
            if ("admin".equalsIgnoreCase(userRole)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Admin accounts cannot be removed.");
                return;
            }
            if (confirmAction("Confirm Deletion", "Are you sure you want to delete '" + username + "'?")) {
                boolean deleted = databaseHelper.deleteUser(username);
                showAlert(deleted ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
                        deleted ? "User Deleted" : "Error", 
                        deleted ? "User '" + username + "' removed."
                                : "Failed to remove user.");
            }
        });
    }

    private void handleSetTemporaryPassword() {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Temporary Password");
        inputDialog.setHeaderText(null);
        inputDialog.setContentText("Username:");

        inputDialog.showAndWait().ifPresent(username -> {
            if (username.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Username cannot be empty.");
                return;
            }
            User user = databaseHelper.getUser(username);
            if (user == null) {
                showAlert(Alert.AlertType.ERROR, "User Not Found", "No such user exists.");
                return;
            }
            String tempPassword = generateTemporaryPassword();
            user.setPassword(tempPassword);
            user.setTemporaryPassword(true);

            boolean updateSuccess = databaseHelper.updateUser(user);
            showAlert(updateSuccess ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
                      updateSuccess ? "Success" : "Error", 
                      updateSuccess ? "Temporary password set for '" + username + "'.\nNew Password: " + tempPassword
                                    : "Failed to update user.");
        });
    }

    private void handleLogout() {
        new UserLoginPage(databaseHelper).show(new Stage());
    }

    private String promptForInput(String title, String header, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        return dialog.showAndWait().orElse(null);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean confirmAction(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder tempPassword = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            tempPassword.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return tempPassword.toString();
    }
}
