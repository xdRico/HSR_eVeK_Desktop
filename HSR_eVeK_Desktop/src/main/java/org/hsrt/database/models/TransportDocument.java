package org.hsrt.database.models;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "transportDocument")
public class TransportDocument implements Serializable {

    @Id
    @Column(name = "transportDocumentId", nullable = false)
    private UUID transportDocumentId;

    @ManyToOne
    @JoinColumn(name = "parient")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "insuranceData", nullable = false)
    private InsuranceData insuranceData;

    @Column(name = "transportReason", nullable = false)
    private String transportReason;

    @Column(name = "startDate", nullable = false)
    private Date startDate;

    @Column(name = "endDate")
    private Date endDate;

    @Column(name = "weeklyFrequency")
    private Integer weeklyFrequency;

    @ManyToOne
    @JoinColumn(name = "healthcareServiceProvider", nullable = false)
    private ServiceProvider healthcareServiceProvider;

    @Column(name = "transportationType", nullable = false)
    private String transportationType;

    @Column(name = "additionalInfo")
    private String additionalInfo;

    @ManyToOne
    @JoinColumn(name = "signature", nullable = false)
    private User signature;

    //Constructors, Getters, Setters

    public TransportDocument() {}

    public TransportDocument(UUID transportDocumentId, Patient patient, InsuranceData insuranceData, String transportReason, Date startDate, Date endDate, Integer weeklyFrequency, ServiceProvider healthcareServiceProvider, String transportationType, String additionalInfo, User signature) {
        this.transportDocumentId = transportDocumentId;
        this.patient = patient;
        this.insuranceData = insuranceData;
        this.transportReason = transportReason;
        this.startDate = startDate;
        this.endDate = endDate;
        this.weeklyFrequency = weeklyFrequency;
        this.healthcareServiceProvider = healthcareServiceProvider;
        this.transportationType = transportationType;
        this.additionalInfo = additionalInfo;
        this.signature = signature;
    }

    public UUID getTransportDocumentId() {
        return transportDocumentId;
    }

    public void setTransportDocumentId(UUID transportDocumentId) {
        this.transportDocumentId = transportDocumentId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public InsuranceData getInsuranceData() {
        return insuranceData;
    }

    public void setInsuranceData(InsuranceData insuranceData) {
        this.insuranceData = insuranceData;
    }

    public String getTransportReason() {
        return transportReason;
    }

    public void setTransportReason(String transportReason) {
        this.transportReason = transportReason;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getWeeklyFrequency() {
        return weeklyFrequency;
    }

    public void setWeeklyFrequency(Integer weeklyFrequency) {
        this.weeklyFrequency = weeklyFrequency;
    }

    public ServiceProvider getHealthcareServiceProvider() {
        return healthcareServiceProvider;
    }

    public void setHealthcareServiceProvider(ServiceProvider healthcareServiceProvider) {
        this.healthcareServiceProvider = healthcareServiceProvider;
    }

    public String getTransportationType() {
        return transportationType;
    }

    public void setTransportationType(String transportationType) {
        this.transportationType = transportationType;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public User getSignature() {
        return signature;
    }

    public void setSignature(User signature) {
        this.signature = signature;
    }
}