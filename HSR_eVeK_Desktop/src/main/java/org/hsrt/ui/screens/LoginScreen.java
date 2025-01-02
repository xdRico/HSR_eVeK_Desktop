package org.hsrt.ui.screens;

import de.ehealth.evek.api.entity.User;

import de.ehealth.evek.api.type.UserRole;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.scene.control.*;
import org.hsrt.ui.controllers.LoginController;
import org.hsrt.ui.screens.menu.HospitalMenuScreen;
import org.hsrt.ui.screens.menu.InsuranceMenuScreen;
import org.hsrt.ui.screens.menu.MenuScreen;
import org.hsrt.ui.screens.menu.TransportProviderMenuScreen;

public class LoginScreen extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login");

        // Create the layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER); // Center the entire grid
        grid.setVgap(10);
        grid.setHgap(10);

        // Set column constraints for centering
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.NEVER); // No stretching for the label column

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.ALWAYS); // Stretch the input field column to fill available space
        column2.setPercentWidth(70); // Adjust the percentage width of the second column if needed

        grid.getColumnConstraints().addAll(column1, column2);

        // Create UI elements
        Label userNameLabel = new Label("Username:");
        TextField userNameField = new TextField();
        userNameField.setMaxWidth(200);
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setMaxWidth(200);
        Button loginButton = new Button("Login");
        Label messageLabel = new Label();

        // Add elements to the layout
        grid.add(userNameLabel, 0, 0);
        grid.add(userNameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(loginButton, 1, 2);
        grid.add(messageLabel, 1, 3);

        GridPane.setColumnSpan(messageLabel, 2); // Span the message label across both columns for better centering

        // Handle login button action
        loginButton.setOnAction(event -> {
            String username = userNameField.getText();
            String password = passwordField.getText();

            User user = null;
            if ((user = authenticateUser(username, password)) != null) {
                messageLabel.setText("Login successful!");

                try {
                    if(user.role() == UserRole.HealthcareAdmin || user.role() == UserRole.HealthcareDoctor || user.role() == UserRole.HealthcareUser) {
                        HospitalMenuScreen hospitalMenuScreen = new HospitalMenuScreen(user);
                        hospitalMenuScreen.start(primaryStage);
                    } else if(user.role() == UserRole.InsuranceAdmin || user.role() == UserRole.InsuranceUser) {
                        InsuranceMenuScreen insuranceMenuScreen = new InsuranceMenuScreen(user);
                        insuranceMenuScreen.start(primaryStage);
                    } else if(user.role() == UserRole.TransportAdmin || user.role() == UserRole.TransportUser || user.role() == UserRole.TransportDoctor || user.role() == UserRole.TransportInvoice) {
                        TransportProviderMenuScreen transportProviderMenuScreen = new TransportProviderMenuScreen(user);
                        transportProviderMenuScreen.start(primaryStage);
                    } else if(user.role() == UserRole.SuperUser) {
                        MenuScreen menuScreen = new MenuScreen(user);
                        menuScreen.start(primaryStage);
                    }else {
                        messageLabel.setText("Invalid user role.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                messageLabel.setText("Invalid username or password.");
            }
        });

        // Set the scene
        Scene scene = new Scene(grid, 300, 200); // Slightly larger for better centering
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private User authenticateUser(String username, String password) {
        return LoginController.login(username, password);
    }

    public static void main(String[] args) {
        launch(args);
    }
}