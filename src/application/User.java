package application;

import java.security.MessageDigest;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

public class User {
    private String userName;
    private String password;
    private Set<String> roles;
    private boolean temporaryPassword;
    private String name;
    private String email;

    // Constructor for regular users
    public User(String userName, String password, Set<String> roles, String name, String email) {
        this.userName = userName;
        this.password = hashPassword(password);
        this.roles = new HashSet<>(roles);
        this.temporaryPassword = false;
        this.name = name;
        this.email = email;
    }

    // Constructor for temporary users
    public User(String userName, String password, Set<String> roles, boolean temporaryPassword) {
        this.userName = userName;
        this.password = hashPassword(password);
        this.roles = new HashSet<>(roles);
        this.temporaryPassword = temporaryPassword;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(Integer.toHexString(0xff & b));
            }
            return hexString.toString(); // Return the hashed password as a hex string
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }

    // Add role to user
    public void addRole(String role) {
        if (isValidRole(role)) {
            this.roles.add(role);
        } else {
            System.out.println("Invalid role: " + role);
        }
    }

    // Remove role from user
    public void removeRole(String role) {
        this.roles.remove(role);
    }

    // Check if user has a specific role
    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    // Check if the entered password matches the stored password
    public boolean checkPassword(String enteredPassword) {
        return this.password.equals(hashPassword(enteredPassword));
    }

    // Getters and setters
    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setRoles(Set<String> newRoles) {
        this.roles = new HashSet<>(newRoles); // Create a copy!
    }

    public Set<String> getRoles() {
        return new HashSet<>(roles); // Returns a COPY
    }

    // Returns the original roles
    public Set<String> getOriginalRoles() {
        return roles;
    }

    // Check if the user has a temporary password
    public boolean hasTemporaryPassword() {
        return temporaryPassword;
    }

    // Set whether the user has a temporary password
    public void setTemporaryPassword(boolean temporaryPassword) {
        this.temporaryPassword = temporaryPassword;
    }

    // Set the user's password with a new password
    public void setPassword(String password) {
        this.password = hashPassword(password); // Store the hashed password
    }

    // Simple role validation 
    private boolean isValidRole(String role) {
        //assume a basic role validation
        return role.equals("admin") || role.equals("user") || role.equals("staff") || role.equals("reviewer");
    }
    
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public StringProperty nameProperty() {
        return new SimpleStringProperty(name);
    }

    public StringProperty userNameProperty() {
        return new SimpleStringProperty(userName);
    }

    public StringProperty roleProperty() {
        return new SimpleStringProperty(String.join(", ", roles));
    }

    public StringProperty emailProperty() {
        return new SimpleStringProperty(email);
    }
}

