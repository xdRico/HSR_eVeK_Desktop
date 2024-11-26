package org.hsrt.database.models;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue
    private UUID addressId;

    private String name;

    @Column(name = "streetName", nullable = false)
    private String streetName;

    @Column(name = "houseNumber", nullable = false)
    private String houseNumber;

    @Column(nullable = false)
    private String country;

    @Column(name = "postCode", nullable = false)
    private Integer postCode;

    @Column(nullable = false)
    private String city;

    // Constructors, getters, setters
    public Address() {}

    public Address(UUID addressId, String name, String streetName, String houseNumber, String country, Integer postCode, String city) {
        this.addressId = addressId;
        this.name = name;
        this.streetName = streetName;
        this.houseNumber = houseNumber;
        this.country = country;
        this.postCode = postCode;
        this.city = city;
    }

    public Address(String name, String streetName, String houseNumber, String country, Integer postCode, String city) {
        this.name = name;
        this.streetName = streetName;
        this.houseNumber = houseNumber;
        this.country = country;
        this.postCode = postCode;
        this.city = city;
    }

    // Getters and Setters
    public UUID getAddressId() {
        return addressId;
    }

    public void setAddressId(UUID addressId) {
        this.addressId = addressId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getPostCode() {
        return postCode;
    }

    public void setPostCode(Integer postCode) {
        this.postCode = postCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}