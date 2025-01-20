package org.hsrt.ui.screens.managementScreens;

import de.ehealth.evek.api.entity.Address;
import de.ehealth.evek.api.entity.ServiceProvider;
import de.ehealth.evek.api.entity.TransportDetails;
import de.ehealth.evek.api.entity.User;
import de.ehealth.evek.api.type.Reference;
import de.ehealth.evek.api.type.UserRole;

import de.ehealth.evek.api.util.COptional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hsrt.ui.controllers.TransportDetailsController;
import org.hsrt.ui.controllers.UserManagementController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents the window for managing users.
 * Here, users can be created, displayed, edited, and deleted.
 */

public class UserManagement {
    private ObservableList<User> userList;
    private User user;

    /**
     * Creates a new instance of the UserManagement.
     *
     * @param user The currently logged-in user.
     */


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

        Button viewEditUsers = new Button("View/Edit Users");
        viewEditUsers.setOnAction(e -> {
            Stage viewEditStage = openViewEditUserWindow();
            viewEditStage.show();
        });
        vbox.getChildren().addAll(createUser, viewEditUsers);

        Scene scene = new Scene(vbox, 800, 600);
        stage.setScene(scene);

        return stage;
    }

    /**
     * Opens a window for creating a new user.
     *
     * @return The created stage.
     */

    private Stage openCreateUserWindow() {
        Stage stage = new Stage();
        stage.setTitle("Create User");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Input fields for user details
        Label userNameLabel = new Label("Username:");
        TextField userNameField = new TextField();
        gridPane.add(userNameLabel, 0, 0);
        gridPane.add(userNameField, 1, 0);

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordField, 1, 1);


        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField();
        gridPane.add(firstNameLabel, 0, 2);
        gridPane.add(firstNameField, 1, 2);

        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField();
        gridPane.add(lastNameLabel, 0, 3);
        gridPane.add(lastNameField, 1, 3);

        Label streetLabel = new Label("Street:");
        TextField streetField = new TextField();
        gridPane.add(streetLabel, 0, 4);
        gridPane.add(streetField, 1, 4);

        Label houseNumberLabel = new Label("House Number:");
        TextField houseNumberField = new TextField();
        gridPane.add(houseNumberLabel, 0, 5);
        gridPane.add(houseNumberField, 1, 5);

        Label postCodeLabel = new Label("Post Code:");
        TextField postCodeField = new TextField();
        gridPane.add(postCodeLabel, 0, 6);
        gridPane.add(postCodeField, 1, 6);

        Label cityLabel = new Label("City:");
        TextField cityField = new TextField();
        gridPane.add(cityLabel, 0, 7);
        gridPane.add(cityField, 1, 7);

        Label countryLabel = new Label("Country:");
        TextField countryField = new TextField();
        gridPane.add(countryLabel, 0, 8);
        gridPane.add(countryField, 1, 8);

        // Dropdown for role selection
        Label roleLabel = new Label("Role:");
        ComboBox<UserRole> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll(getAvailableRoles());
        gridPane.add(roleLabel, 0, 9);
        gridPane.add(roleComboBox, 1, 9);

        // TextField for ServiceProvider (user input)
        Label serviceProviderLabel = new Label("Service Provider:");
        TextField serviceProviderField = new TextField();
        gridPane.add(serviceProviderLabel, 0, 10);
        gridPane.add(serviceProviderField, 1, 10);

        // Check if the current user is a SuperUser
        if (user.role() == UserRole.SuperUser) {
            // Allow SuperUsers to edit the ServiceProvider field
            serviceProviderField.setDisable(false);
        } else {
            // Auto-fill ServiceProvider for non-SuperUsers and disable the field
            serviceProviderField.setText(user.serviceProvider().toString());
            serviceProviderField.setDisable(true);
        }

        // Create button
        Button createButton = new Button("Create");
        createButton.setDisable(true); // Initially disable the button
        createButton.setOnAction(e -> {
            String username = userNameField.getText();
            String password = passwordField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String street = streetField.getText();
            String houseNumber = houseNumberField.getText();
            String postCode = postCodeField.getText();
            String city = cityField.getText();
            String country = countryField.getText();
            UserRole selectedRole = roleComboBox.getValue();
            String serviceProvider = serviceProviderField.getText();

            Address address = TransportDetailsController.createAddress(street, houseNumber, postCode, city, country);
            Reference<ServiceProvider> serviceProviderReference = Reference.to(serviceProvider);
            User newUser = new User(null, firstName, lastName, Reference.to(address.id()), serviceProviderReference, selectedRole);

            UserManagementController.saveUser(newUser, username, password);

            stage.close();
        });

        // Enable the button when a role is selected
        roleComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            createButton.setDisable(newValue == null);
        });

        VBox vbox = new VBox(10, gridPane, createButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 400, 500);
        stage.setScene(scene);
        return stage;
    }

    /**
     * Returns a list of available roles based on the current user's role.
     *
     * @return A list of available roles.
     */

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

    /**
     * Opens a window for viewing and editing users.
     *
     * @return The created stage.
     */

    private Stage openViewEditUserWindow() {
        Stage stage = new Stage();
        stage.setTitle("View/Edit Users");

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));

        TableView<User> userTable = new TableView<>();
        userTable.setItems(getFilteredUsers());

        TableColumn<User, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().firstName()));
        TableColumn<User, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().lastName()));
        TableColumn<User, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().role().toString()));

        userTable.getColumns().addAll(firstNameColumn, lastNameColumn, roleColumn);

        // Listener für Doppelklick auf eine Zeile
        userTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    User selectedUser = row.getItem();
                    Stage editUserStage = openEditUserWindow(selectedUser);
                    editUserStage.show();
                }
            });
            return row;
        });

        Button editButton = new Button("Edit Selected User");
        editButton.setOnAction(e -> {
            User selectedUser = userTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                Stage editUserStage = openEditUserWindow(selectedUser);
                editUserStage.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a user to edit.");
                alert.showAndWait();
            }
        });

        vbox.getChildren().addAll(userTable, editButton);

        Scene scene = new Scene(vbox, 600, 400);
        stage.setScene(scene);
        return stage;
    }

    /**
     * Returns a list of users based on the current user's role.
     *
     * @return A list of users.
     */

    private ObservableList<User> getFilteredUsers() {
        UserManagementController controller = new UserManagementController();
        userList = controller.fetchUsersFromAPI(user); // Neue API-Aufruf-Methode

        return userList;
    }

    /**
     * Opens a window for editing a user.
     *
     * @param userToEdit The user to edit.
     * @return The created stage.
     */

    private Stage openEditUserWindow(User userToEdit) {
        Stage stage = new Stage();
        stage.setTitle("Edit User: " + userToEdit.firstName() + " " + userToEdit.lastName());

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField(userToEdit.firstName());
        gridPane.add(firstNameLabel, 0, 0);
        gridPane.add(firstNameField, 1, 0);


        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField(userToEdit.lastName());
        gridPane.add(lastNameLabel, 0, 1);
        gridPane.add(lastNameField, 1, 1);

        Address oldAddress = TransportDetailsController.getAddressFromReference(COptional.of(userToEdit.address()));

        Label streetLabel = new Label("Street:");
        TextField streetField = new TextField(oldAddress.streetName());
        gridPane.add(streetLabel, 0, 2);
        gridPane.add(streetField, 1, 2);


        Label houseNumberLabel = new Label("House Number:");
        TextField houseNumberField = new TextField(oldAddress.houseNumber());
        gridPane.add(houseNumberLabel, 0, 3);
        gridPane.add(houseNumberField, 1, 3);

        Label postCodeLabel = new Label("Post Code:");
        TextField postCodeField = new TextField(oldAddress.postCode());
        gridPane.add(postCodeLabel, 0, 4);
        gridPane.add(postCodeField, 1, 4);

        Label cityLabel = new Label("City:");
        TextField cityField = new TextField(oldAddress.city());
        gridPane.add(cityLabel, 0, 5);
        gridPane.add(cityField, 1, 5);

        Label countryLabel = new Label("Country:");
        TextField countryField = new TextField(oldAddress.country());
        gridPane.add(countryLabel, 0, 6);
        gridPane.add(countryField, 1, 6);


        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            String newFirstName = firstNameField.getText();
            String newLastName = lastNameField.getText();

            Address newAddress = TransportDetailsController.createAddress(streetField.getText(), houseNumberField.getText(), postCodeField.getText(), cityField.getText(), countryField.getText());

            // Save the updated user
            User updatedUser = userToEdit.updateWith(newLastName, newFirstName, Reference.to(newAddress.id()), userToEdit.serviceProvider());
            UserManagementController.updateUser(updatedUser);

            stage.close();
        });

        VBox vbox = new VBox(10, gridPane, saveButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 400, 300);
        stage.setScene(scene);
        return stage;
    }

}