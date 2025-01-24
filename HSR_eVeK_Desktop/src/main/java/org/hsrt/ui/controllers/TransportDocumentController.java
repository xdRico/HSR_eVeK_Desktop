package org.hsrt.ui.controllers;

import de.ehealth.evek.api.entity.*;
import de.ehealth.evek.api.type.TransportationType;



import de.ehealth.evek.api.type.Id;
import de.ehealth.evek.api.type.Reference;
import de.ehealth.evek.api.type.TransportReason;
import de.ehealth.evek.api.util.COptional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hsrt.network.DataHandler;


import java.sql.Date;

public class TransportDocumentController {
    public static ObservableList<TransportDocument> getTransportDocuments(User user) {
        DataHandler dataHandler = DataHandler.instance();
        // Initialisiere die Serververbindung
        dataHandler.initServerConnection();

        // Warte, bis die Verbindung initialisiert ist
        while (!dataHandler.isInitialized()) {
            try {
                Thread.sleep(100); // Warte 100ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            dataHandler.refreshTransportDocuments();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ObservableList<TransportDocument> transportDocuments = null;
        try {
            transportDocuments = dataHandler.getTransportDocuments();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        Patient patient = new Patient(new Id<Patient>("1"), new Reference<>(new Id<>("1")), "Mustermann", "Max", Date.valueOf("1990-01-01"), new Reference<>(new Id<>("1")));
        Reference<Patient> patientReference = new Reference<>(new Id<>(patient.insuranceNumber().toString()));
        Reference<InsuranceData> insuranceDataReference = new Reference<>(new Id<InsuranceData>("3"));
        Reference<User> userReference = Reference.to(user.id().toString());

        ObservableList<TransportDocument> transportDocuments = FXCollections.observableArrayList(
                new TransportDocument(new Id<TransportDocument>("1"), COptional.of(patientReference), COptional.of(insuranceDataReference), TransportReason.EmergencyTransport, Date.valueOf("2023-01-01"), null, null, null, TransportationType.KTW, null, userReference, false),
                new TransportDocument(new Id<TransportDocument>("2"), COptional.of(patientReference),  COptional.of(insuranceDataReference), TransportReason.HighFrequent, Date.valueOf("2023-02-01"), null, null, null, TransportationType.RTW, null, userReference, false),
                new TransportDocument(new Id<TransportDocument>("3"), COptional.of(patientReference),  COptional.of(insuranceDataReference), TransportReason.ContinuousImpairment, Date.valueOf("2023-03-01"), null, null, null, TransportationType.NAWorNEF, null, userReference, false)
        );

         */

        return transportDocuments;
    }

    public static TransportDocument createTransportDocument(COptional<Reference<Patient>> patientOpt, COptional<Reference<InsuranceData>> insuranceDataOpt, TransportReason transportReason, Date startDate, COptional<Date> endDateOpt, COptional<Integer> weeklyFrequencyOpt, Reference<ServiceProvider> healthcareServiceProvider, TransportationType transportationType, COptional<String> additionalInfoOpt) {
        DataHandler dataHandler = DataHandler.instance();
        // Initialisiere die Serververbindung
        dataHandler.initServerConnection();

        // Warte, bis die Verbindung initialisiert ist
        while (!dataHandler.isInitialized()) {
            try {
                Thread.sleep(100); // Warte 100ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        TransportDocument transportDocument = null;

        try {
            transportDocument = dataHandler.createTransportDocument(patientOpt, insuranceDataOpt, transportReason, startDate, endDateOpt, weeklyFrequencyOpt, healthcareServiceProvider, transportationType, additionalInfoOpt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transportDocument;
    }

    public static TransportDocument updateTransportDocument(Id<TransportDocument> id, COptional<Reference<Patient>> patientOpt, COptional<Reference<InsuranceData>> insuranceDataOpt, TransportReason transportReason, Date startDate, COptional<Date> endDateOpt, COptional<Integer> weeklyFrequencyOpt, Reference<ServiceProvider> healthcareServiceProvider, TransportationType transportationType, COptional<String> additionalInfoOpt) {
        DataHandler dataHandler = DataHandler.instance();
        // Initialisiere die Serververbindung
        dataHandler.initServerConnection();

        // Warte, bis die Verbindung initialisiert ist
        while (!dataHandler.isInitialized()) {
            try {
                Thread.sleep(100); // Warte 100ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        TransportDocument transportDocument = null;

        try {
            transportDocument = dataHandler.updateTransportDocument(id, patientOpt, insuranceDataOpt, transportReason, startDate, endDateOpt, weeklyFrequencyOpt, healthcareServiceProvider, transportationType, additionalInfoOpt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transportDocument;
    }

    public static InsuranceData getInsuranceData(String patientNumber) {
        DataHandler dataHandler = DataHandler.instance();
        // Initialisiere die Serververbindung
        dataHandler.initServerConnection();

        // Warte, bis die Verbindung initialisiert ist
        while (!dataHandler.isInitialized()) {
            try {
                Thread.sleep(100); // Warte 100ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        InsuranceData insuranceData = null;

        try {
            insuranceData = dataHandler.getInsuranceData(patientNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return insuranceData;
    }

    public static ServiceProvider getHealthcareProvider( User user) {
        DataHandler dataHandler = DataHandler.instance();
        // Initialisiere die Serververbindung
        dataHandler.initServerConnection();

        // Warte, bis die Verbindung initialisiert ist
        while (!dataHandler.isInitialized()) {
            try {
                Thread.sleep(100); // Warte 100ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ServiceProvider healthcareProvider = null;

        try {
            healthcareProvider = dataHandler.getHealthcareProvider(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return healthcareProvider;

    }

    public static TransportDocument archiveTransportDocument(Id<TransportDocument> id) {
        DataHandler dataHandler = DataHandler.instance();
        // Initialisiere die Serververbindung
        dataHandler.initServerConnection();

        // Warte, bis die Verbindung initialisiert ist
        while (!dataHandler.isInitialized()) {
            try {
                Thread.sleep(100); // Warte 100ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        TransportDocument transportDocument = null;

        try {
            transportDocument = dataHandler.archiveTransportDocument(id);
            return transportDocument;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static InsuranceData createInsuranceData(COptional<Reference<Patient>> patientOpt, Reference<Insurance> insurance, String insuranceStatus) {
        DataHandler dataHandler = DataHandler.instance();
        // Initialisiere die Serververbindung
        dataHandler.initServerConnection();



        InsuranceData insuranceData;

        try {
            insuranceData = dataHandler.createInsuranceData(patientOpt, insurance, insuranceStatus);
            return insuranceData;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("TransportDocumentController");
    }
        return null;
    }

    public static InsuranceData getInsuranceDataByID(Id<InsuranceData> id) {
        DataHandler dataHandler = DataHandler.instance();
        // Initialisiere die Serververbindung
        dataHandler.initServerConnection();

        // Warte, bis die Verbindung initialisiert ist
        while (!dataHandler.isInitialized()) {
            try {
                Thread.sleep(100); // Warte 100ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        InsuranceData insuranceData = null;

        try {
            insuranceData = dataHandler.getInsuranceDataByID(id);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return insuranceData;
    }
}
