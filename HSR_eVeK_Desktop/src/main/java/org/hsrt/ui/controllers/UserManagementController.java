package org.hsrt.ui.controllers;

import de.ehealth.evek.api.entity.User;

import javafx.collections.ObservableList;

import org.hsrt.network.DataHandler;


/**
 * Controller class for managing user data.
 */

public class UserManagementController {
    public static void updateUser(User updatedUser) {
        DataHandler dataHandler = DataHandler.instance();
        dataHandler.initServerConnection();
        dataHandler.updateUser(updatedUser);
    }

    /**
     * Fetches all users from the database or API.
     *
     * @return A list of all users.
     */
    public ObservableList<User> fetchUsersFromAPI(User user) {
        DataHandler dataHandler = DataHandler.instance();
        dataHandler.initServerConnection();

        return dataHandler.getUsers(user);

    }

    /**
     * Saves a user to the database or backend.
     *
     * @param user The user to save.
     */
    public static User saveUser(User user, String username, String password) {
        DataHandler dataHandler = DataHandler.instance();
        dataHandler.initServerConnection();
        return dataHandler.createUser(user, username, password);
    }
}
