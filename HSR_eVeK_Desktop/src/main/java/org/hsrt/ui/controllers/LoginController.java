package org.hsrt.ui.controllers;

import de.ehealth.evek.api.entity.ServiceProvider;
import de.ehealth.evek.api.entity.User;
import de.ehealth.evek.api.type.Id;
import de.ehealth.evek.api.type.Reference;
import de.ehealth.evek.api.type.UserRole;
import de.ehealth.evek.api.type.Reference;
import org.hsrt.network.DataHandler;

public class LoginController {
    static Reference<ServiceProvider> serviceProviderref = Reference.to("tub");

    /**
     * Logs in a user with the given credentials.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The logged in user or null if the login failed.
     */
    public static User login(String username, String password) {
        DataHandler dataHandler = DataHandler.instance();
        // Initialisiere die Serververbindung
        dataHandler.initServerConnection();

        // Warte, bis die Verbindung initialisiert ist
        while (!dataHandler.isInitialized()) {
            try {
                System.out.println("Waiting for server connection...");
                Thread.sleep(100); // Warte 100ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Trying to login...");

        User user = dataHandler.tryLogin(username, password);
        if (user != null) {
            System.out.println("Login successful!");
            return user;
        } else{
            System.out.println("Login failed!");
            return null;
        }
        /*
        return new User(

        new Id<User>("1"),
        "lastName",
        "firstName",
         Reference.to("null"),
         serviceProviderref,
         UserRole.SuperUser);

         */


         /*

        return null;

         */
    }
}
