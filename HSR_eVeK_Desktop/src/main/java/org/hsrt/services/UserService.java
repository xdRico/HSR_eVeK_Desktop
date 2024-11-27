package org.hsrt.services;

import org.hsrt.database.models.User;


public class UserService {

    public void saveUser (User user) {
        // Logic to save user
        System.out.println("User  saved: " + user.getUserName());
    }
}