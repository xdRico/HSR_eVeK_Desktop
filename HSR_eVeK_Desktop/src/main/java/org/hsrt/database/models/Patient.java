package org.hsrt.database.models;
import jakarta.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "patient")
public class Patient {
    @Id
    @GeneratedValue
    private UUID patientId;

    @Column(name = "insuranceDataId")
    private UUID insuranceDataId;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "birthDate", nullable = false)
    private Date birthDate;

    @ManyToOne
    @JoinColumn(name = "adress", nullable = false)
    private Address address;

    // Constructors, getters, setters

    public Patient(UUID patientId, UUID insuranceDataId, String lastName, String firstName, Date birthDate, Address address) {
        this.patientId = patientId;
        this.insuranceDataId = insuranceDataId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.address = address;
    }

    public Patient() {
    }

    // Getters and Setters
    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public UUID getInsuranceDataId() {
        return insuranceDataId;
    }

    public void setInsuranceDataId(UUID insuranceDataId) {
        this.insuranceDataId = insuranceDataId;
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

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddressId(Address addressId) {
        this.address = address;
    }
}
