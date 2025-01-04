package org.hsrt.ui.controllers;

import de.ehealth.evek.api.entity.Address;
import de.ehealth.evek.api.entity.ServiceProvider;
import de.ehealth.evek.api.entity.User;
import de.ehealth.evek.api.type.Reference;
import de.ehealth.evek.api.type.UserRole;

import java.util.List;

/**
 * Controller class for managing user data.
 */

public class UserManagementController {
    static Reference<ServiceProvider> serviceProviderref = Reference.to("tub");
    static Reference<Address> addressReference = Reference.to("address");

    /**
     * Fetches all users from the database or API.
     *
     * @return A list of all users.
     */
    public List<User> fetchUsersFromAPI() {
        // Simulate API call or database query
        // TODO Replace this with actual implementation using your backend service
        System.out.println("Fetching users from API...");
        return List.of(
                new User(null, "Doe", "John", addressReference,serviceProviderref , UserRole.HealthcareUser),
                new User(null, "Smith", "Jane", addressReference,serviceProviderref , UserRole.InsuranceAdmin)
        );
    }

    /**
     * Saves a user to the database or backend.
     *
     * @param user The user to save.
     */
    public static boolean saveUser(User user) {
        // TODO Logic to save the user to the database or backend
        System.out.println("User saved: " + user);
        return true;
    }
}
