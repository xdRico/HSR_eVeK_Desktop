package org.hsrt.ui.screens.managementScreens;

import de.ehealth.evek.api.entity.Address;
import de.ehealth.evek.api.entity.User;
import de.ehealth.evek.api.type.Reference;
import de.ehealth.evek.api.type.UserRole;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class UserManagement {
    private User user;

    public Stage createUserManagement(User user) {
        this.user = user;
        Stage stage = new Stage();
        stage.setTitle("User  Management");

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));

        Button createUser  = new Button("Create User");
        createUser .setOnAction(e -> {
            Stage createUserStage = openCreateUserWindow();
            createUserStage.show();
        });

        vbox.getChildren().addAll(createUser );
        Scene scene = new Scene(vbox, 800, 600);
        stage.setScene(scene);

        return stage;
    }

    private Stage openCreateUserWindow() {
        Stage stage = new Stage();
        stage.setTitle("Create User");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Input fields for user details
        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField();
        gridPane.add(firstNameLabel, 0, 0);
        gridPane.add(firstNameField, 1, 0);

        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField();
        gridPane.add(lastNameLabel, 0, 1);
        gridPane.add(lastNameField, 1, 1);

        Label streetLabel = new Label("Street:");
        TextField streetField = new TextField();
        gridPane.add(streetLabel, 0, 2);
        gridPane.add(streetField, 1, 2);

        Label houseNumberLabel = new Label("House Number:");
        TextField houseNumberField = new TextField();
        gridPane.add(houseNumberLabel, 0, 3);
        gridPane.add(houseNumberField, 1, 3);

        Label postCodeLabel = new Label("Post Code:");
        TextField postCodeField = new TextField();
        gridPane.add(postCodeLabel, 0, 4);
        gridPane.add(postCodeField, 1, 4);

        Label cityLabel = new Label("City:");
        TextField cityField = new TextField();
        gridPane.add(cityLabel, 0, 5);
        gridPane.add(cityField, 1, 5);

        Label countryLabel = new Label("Country:");
        TextField countryField = new TextField();
        gridPane.add(countryLabel, 0, 6);
        gridPane.add(countryField, 1, 6);

        // Dropdown for role selection
        Label roleLabel = new Label("Role:");
        ComboBox<UserRole> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll(getAvailableRoles());
        gridPane.add(roleLabel, 0, 7);
        gridPane.add(roleComboBox, 1, 7);


        // Label for ServiceProvider (auto-filled)
        Label serviceProviderLabel = new Label("Service Provider:");
        Label serviceProviderValueLabel = new Label(user.serviceProvider().toString());
        gridPane.add(serviceProviderLabel, 0, 8);
        gridPane.add(serviceProviderValueLabel, 1, 8);

        // Create button
        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            // Validation and user creation logic
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String street = streetField.getText();
            String houseNumber = houseNumberField.getText();
            String postCode = postCodeField.getText();
            String city = cityField.getText();
            String country = countryField.getText();
            UserRole selectedRole = roleComboBox.getValue();

            if (firstName.isEmpty() || lastName.isEmpty() || selectedRole == null) {
                showAlert("Error", "All fields are required.");
                return;
            }

            Address address = new Address(null, null, street, houseNumber, country, postCode, city);
            Reference<Address> addressref = Reference.to(String.valueOf(address));
            User newUser = new User(null, firstName, lastName, addressref, user.serviceProvider(), selectedRole);

            // Save user to the database or API call
            saveUser(newUser);

            stage.close();
        });

        VBox vbox = new VBox(10, gridPane, createButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 400, 500);
        stage.setScene(scene);
        return stage;
    }

    private void saveUser(User user) {
        // Logic to save the user to the database or backend
        System.out.println("User saved: " + user);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private List<UserRole> getAvailableRoles() {
        List<UserRole> roles = null;
        switch (user.role()) {
            case HealthcareAdmin:
                roles = Arrays.asList( UserRole.HealthcareAdmin, UserRole.HealthcareDoctor, UserRole.HealthcareUser);
                break;
            case InsuranceAdmin:
                roles = Arrays.asList(UserRole.InsuranceAdmin, UserRole.InsuranceUser);
                break;
            case TransportAdmin:
                roles = Arrays.asList(UserRole.TransportAdmin, UserRole.TransportDoctor, UserRole.TransportUser);
                break;
            case SuperUser:
                roles = Arrays.asList(UserRole.values());
                break;
        }
        return roles; // Gibt alle verfügbaren UserRoles zurück
    }
}