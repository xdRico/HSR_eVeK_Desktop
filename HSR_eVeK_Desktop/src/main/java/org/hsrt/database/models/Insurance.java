package org.hsrt.database.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "insurance")
public class Insurance implements Serializable {
    @Id
    @GeneratedValue
    private UUID insuranceId;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "adress", nullable = false)
    private Address address;

    // Constructors, getters, setters
    public Insurance() {}

    public Insurance(UUID insuranceId, String name, Address address) {
        this.insuranceId = insuranceId;
        this.name = name;
        this.address = address;
    }

    // Getters and Setters
    public UUID getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(UUID insuranceId) {
        this.insuranceId = insuranceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
