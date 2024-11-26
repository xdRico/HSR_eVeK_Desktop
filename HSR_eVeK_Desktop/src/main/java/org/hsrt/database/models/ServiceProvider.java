package org.hsrt.database.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "serviceProvider")
public class ServiceProvider implements Serializable {

    @Id
    @Column(name = "serviceProviderId", nullable = false)
    private UUID serviceProviderId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "isHealthcareProvider", nullable = false)
    private boolean isHealthcareProvider;

    @Column(name = "isTransportProvider", nullable = false)
    private boolean isTransportProvider;

    @ManyToOne
    @JoinColumn(name = "adress", nullable = false)
    private Address address;

    @Column(name = "contactInfo")
    private String contactInfo;

    //Constructors, Getters, Setters

    public ServiceProvider() {}

    public ServiceProvider(UUID serviceProviderId, String name, String type, boolean isHealthcareProvider, boolean isTransportProvider, Address address, String contactInfo) {
        this.serviceProviderId = serviceProviderId;
        this.name = name;
        this.type = type;
        this.isHealthcareProvider = isHealthcareProvider;
        this.isTransportProvider = isTransportProvider;
        this.address = address;
        this.contactInfo = contactInfo;
    }

    public UUID getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(UUID serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isHealthcareProvider() {
        return isHealthcareProvider;
    }

    public void setHealthcareProvider(boolean healthcareProvider) {
        isHealthcareProvider = healthcareProvider;
    }

    public boolean isTransportProvider() {
        return isTransportProvider;
    }

    public void setTransportProvider(boolean transportProvider) {
        isTransportProvider = transportProvider;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }


}