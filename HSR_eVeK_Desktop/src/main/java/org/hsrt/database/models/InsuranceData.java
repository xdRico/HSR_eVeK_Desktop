package org.hsrt.database.models;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "insuranceData")
public class InsuranceData {
    @Id
    @GeneratedValue
    private UUID insuranceDataId;

    @Column(nullable = false)
    private UUID patientId;

    @Column(name = "insuranceId", nullable = false)
    private UUID insuranceId;

    @Column(nullable = false)
    private Integer insuranceStatus;

    // Constructors, getters, setters
    public InsuranceData() {}

    public InsuranceData(UUID insuranceDataId, UUID patientId, UUID insuranceId, Integer insuranceStatus) {
        this.insuranceDataId = insuranceDataId;
        this.patientId = patientId;
        this.insuranceId = insuranceId;
        this.insuranceStatus = insuranceStatus;
    }

    // Getters and Setters
    public UUID getInsuranceDataId() {
        return insuranceDataId;
    }

    public void setInsuranceDataId(UUID insuranceDataId) {
        this.insuranceDataId = insuranceDataId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public UUID getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(UUID insuranceId) {
        this.insuranceId = insuranceId;
    }

    public Integer getInsuranceStatus() {
        return insuranceStatus;
    }

    public void setInsuranceStatus(Integer insuranceStatus) {
        this.insuranceStatus = insuranceStatus;
    }

}