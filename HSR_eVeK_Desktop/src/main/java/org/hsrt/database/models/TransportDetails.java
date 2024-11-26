package org.hsrt.database.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "transportDetails")
public class TransportDetails implements Serializable {

    @Id
    @Column(name = "transportId", nullable = false)
    private UUID transportId;

    @ManyToOne
    @JoinColumn(name = "transportDocument", nullable = false)
    private TransportDocument transportDocument;

    @Column(name = "transportDate", nullable = false)
    private Date transportDate;

    @ManyToOne
    @JoinColumn(name = "startAdress")
    private Address startAddress;

    @ManyToOne
    @JoinColumn(name = "endAdress")
    private Address endAddress;

    @Column(name = "direction")
    private String direction;

    @Column(name = "patientCondition")
    private String patientCondition;

    @ManyToOne
    @JoinColumn(name = "transportServiceProvider", nullable = false)
    private ServiceProvider transportServiceProvider;

    @Column(name = "tourNumber")
    private String tourNumber;

    @Column(name = "paymentExemption")
    private Boolean paymentExemption;

    @Column(name = "patientSignature")
    private String patientSignature;

    @Column(name = "patientSignatureDate")
    private Date patientSignatureDate;

    @Column(name = "transporterSignature")
    private String transporterSignature;

    @Column(name = "transporterSignatureDate")
    private Date transporterSignatureDate;

    //Constructors, Getters, Setters

    public TransportDetails() {}

    public TransportDetails(UUID transportId, TransportDocument transportDocument, Date transportDate, Address startAddress, Address endAddress, String direction, String patientCondition, ServiceProvider transportServiceProvider, String tourNumber, Boolean paymentExemption, String patientSignature, Date patientSignatureDate, String transporterSignature, Date transporterSignatureDate) {
        this.transportId = transportId;
        this.transportDocument = transportDocument;
        this.transportDate = transportDate;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.direction = direction;
        this.patientCondition = patientCondition;
        this.transportServiceProvider = transportServiceProvider;
        this.tourNumber = tourNumber;
        this.paymentExemption = paymentExemption;
        this.patientSignature = patientSignature;
        this.patientSignatureDate = patientSignatureDate;
        this.transporterSignature = transporterSignature;
        this.transporterSignatureDate = transporterSignatureDate;
    }

    public UUID getTransportId() {
        return transportId;
    }

    public void setTransportId(UUID transportId) {
        this.transportId = transportId;
    }

    public TransportDocument getTransportDocument() {
        return transportDocument;
    }

    public void setTransportDocument(TransportDocument transportDocument) {
        this.transportDocument = transportDocument;
    }

    public Date getTransportDate() {
        return transportDate;
    }

    public void setTransportDate(Date transportDate) {
        this.transportDate = transportDate;
    }

    public Address getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(Address startAddress) {
        this.startAddress = startAddress;
    }

    public Address getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(Address endAddress) {
        this.endAddress = endAddress;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getPatientCondition() {
        return patientCondition;
    }

    public void setPatientCondition(String patientCondition) {
        this.patientCondition = patientCondition;
    }

    public ServiceProvider getTransportServiceProvider() {
        return transportServiceProvider;
    }

    public void setTransportServiceProvider(ServiceProvider transportServiceProvider) {
        this.transportServiceProvider = transportServiceProvider;
    }

    public String getTourNumber() {
        return tourNumber;
    }

    public void setTourNumber(String tourNumber) {
        this.tourNumber = tourNumber;
    }

    public Boolean getPaymentExemption() {
        return paymentExemption;
    }

    public void setPaymentExemption(Boolean paymentExemption) {
        this.paymentExemption = paymentExemption;
    }

    public String getPatientSignature() {
        return patientSignature;
    }

    public void setPatientSignature(String patientSignature) {
        this.patientSignature = patientSignature;
    }

    public Date getPatientSignatureDate() {
        return patientSignatureDate;
    }

    public void setPatientSignatureDate(Date patientSignatureDate) {
        this.patientSignatureDate = patientSignatureDate;
    }

    public String getTransporterSignature() {
        return transporterSignature;
    }

    public void setTransporterSignature(String transporterSignature) {
        this.transporterSignature = transporterSignature;
    }

    public Date getTransporterSignatureDate() {
        return transporterSignatureDate;
    }

    public void setTransporterSignatureDate(Date transporterSignatureDate) {
        this.transporterSignatureDate = transporterSignatureDate;
    }
}
