package org.hsrt.ui.controllers;

import de.ehealth.evek.api.entity.ServiceProvider;
import de.ehealth.evek.api.entity.User;
import de.ehealth.evek.api.type.Id;
import de.ehealth.evek.api.type.Reference;
import de.ehealth.evek.api.type.UserRole;
import de.ehealth.evek.api.type.Reference;

public class LoginController {
    static Reference<ServiceProvider> serviceProviderref = Reference.to("tub");
    public static User login(String username, String password) {
        return new User(
        new Id<User>("userId"),
        "lastName",
        "firstName",
         Reference.to("null"),
         serviceProviderref,
         UserRole.HealthcareAdmin);


         /*

        return null;

         */
    }
}
