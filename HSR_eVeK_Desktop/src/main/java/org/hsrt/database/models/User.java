package org.hsrt.database.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "user")
public class User implements Serializable {

    @Id
    @Column(name = "userId", nullable = false)
    private UUID userId;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "firstName")
    private String firstName;

    @ManyToOne
    @JoinColumn(name = "adress", nullable = false)
    private Address address;

    @Column(name = "userName", nullable = false)
    private String userName;

    @ManyToOne
    @JoinColumn(name = "serviceProvider", nullable = false)
    private ServiceProvider serviceProvider;

    @Column(name = "role")
    private String role;

    //Constructors, Getters, Setters

    public User() {}

    public User(UUID userId, String lastName, String firstName, Address address, String userName, ServiceProvider serviceProvider, String role) {
        this.userId = userId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.address = address;
        this.userName = userName;
        this.serviceProvider = serviceProvider;
        this.role = role;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


}
