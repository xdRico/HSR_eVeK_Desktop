package org.hsrt.ui.screens;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.*;
import org.hsrt.database.models.User;
import org.hsrt.ui.controllers.LoginController;


public class LoginScreen extends Application {
    User User;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login");

        // Create the layout
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        // Create UI elements
        Label userNameLabel = new Label("Username:");
        TextField userNameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Label messageLabel = new Label();

        // Add elements to the layout
        grid.add(userNameLabel, 0, 0);
        grid.add(userNameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(loginButton, 1, 2);
        grid.add(messageLabel, 1, 3);

        // Handle login button action
        loginButton.setOnAction(event -> {
            String username = userNameField.getText();
            String password = passwordField.getText();

            if ((User = authenticateUser(username, password)) != null) {
                messageLabel.setText("Login successful!");
                // Proceed to the next screen or functionality
            } else {
                messageLabel.setText("Invalid username or password.");
            }
        });

        // Set the scene
        Scene scene = new Scene(grid, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private User authenticateUser (String username, String password) {

        return LoginController.login(username, password);
    }

    public static void main(String[] args) {
        launch(args);
    }
}