package org.hsrt.ui.controllers;

import de.ehealth.evek.api.entity.*;
import de.ehealth.evek.api.type.*;
import de.ehealth.evek.api.util.COptional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hsrt.network.DataHandler;

import java.sql.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller class for managing transport details.
 */
public class TransportDetailsController {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Fetches all transports from the database or API.
     *
     * @param transportDocument The transport document to fetch transports for.
     * @return A list of all transports.
     */
    public static ObservableList<TransportDetails> getTransports(TransportDocument transportDocument, User user) {
        try {
            return executor.submit(() -> {
                DataHandler dataHandler = DataHandler.instance();
                dataHandler.initServerConnection();
                if(user.role() == UserRole.HealthcareDoctor || user.role() == UserRole.HealthcareUser) {
                    return FXCollections.observableArrayList(dataHandler.getCreatedTransports(transportDocument));
                }
                return FXCollections.observableArrayList(dataHandler.refreshTransports(transportDocument));
            }).get(); // Warten auf das Ergebnis
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Abrufen der Transports", e);
        }
    }

    public static List<ServiceProvider> getTransportProviders() {
        try {
            return executor.submit(() -> {
                DataHandler dataHandler = DataHandler.instance();
                dataHandler.initServerConnection();
                return dataHandler.getTransportProviders();
            }).get();
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Abrufen der Transportanbieter", e);
        }
    }

    public static ServiceProvider getTransportproviderFromReference(COptional<Reference<ServiceProvider>> transportProviderReference) {
        try {
            return executor.submit(() -> {
                DataHandler dataHandler = DataHandler.instance();
                dataHandler.initServerConnection();
                return dataHandler.getTransportProviderFromReference(transportProviderReference);
            }).get();
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Abrufen des Transportanbieters", e);
        }
    }

    public static Address getAddressFromReference(COptional<Reference<Address>> startAddressReference) {
        try {
            return executor.submit(() -> {
                DataHandler dataHandler = DataHandler.instance();
                dataHandler.initServerConnection();
                return dataHandler.getAddressFromReference(startAddressReference);
            }).get();
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Abrufen der Adresse", e);
        }
    }



    public static TransportDetails createTransport(TransportDocument transportDocument, Date sqlDate) {
        try {
            return executor.submit(() -> {
                DataHandler dataHandler = DataHandler.instance();
                dataHandler.initServerConnection();
                return dataHandler.createTransport(transportDocument, sqlDate);
            }).get();
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Erstellen des Transports", e);
        }
    }

    public static void updateTransport(Id<TransportDetails> id, COptional<Address> startAddress, COptional<Address> endAddress,
                                       COptional<Direction> direction, COptional<PatientCondition> patientCondition,
                                       COptional<String> tourNumber, COptional<Boolean> paymentExemption,
                                       String transporterSignature, Date transporterSignatureDate,
                                       String patientSignature, Date patientSignatureDate, ServiceProvider serviceProvider) {
        try {
            executor.submit(() -> {
                DataHandler dataHandler = DataHandler.instance();
                dataHandler.initServerConnection();
                dataHandler.updateTransport(id, startAddress, endAddress, direction, patientCondition, tourNumber,
                        paymentExemption, transporterSignature, transporterSignatureDate,
                        patientSignature, patientSignatureDate, Reference.to(serviceProvider != null ? serviceProvider.id() : new Id<>("-1")));
                return null;
            }).get();
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Aktualisieren des Transports", e);
        }
    }

    public static Address createAddress(String street, String houseNumber, String zipCode, String city, String country) {
        try {
            return executor.submit(() -> {
                DataHandler dataHandler = DataHandler.instance();
                dataHandler.initServerConnection();
                return dataHandler.createAddress(new Address(null, COptional.empty(), street, houseNumber, country, zipCode, city));
            }).get();
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Erstellen der Adresse", e);
        }
    }

    /**
     * Beendet den ExecutorService ordnungsgemäß.
     */
    public static void shutdownExecutor() {
        executor.shutdown();
    }

    public static void deleteTransport(TransportDetails transport) {
        try {
            executor.submit(() -> {
                DataHandler dataHandler = DataHandler.instance();
                dataHandler.initServerConnection();
                dataHandler.deleteTransport(transport);
                return null;
            }).get();
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Löschen des Transports", e);
        }
    }

    public static TransportDetails getTransportFromReference(String text) {
        try {
            return executor.submit(() -> {
                DataHandler dataHandler = DataHandler.instance();
                dataHandler.initServerConnection();
                return dataHandler.getTransportFromReference(text);
            }).get();
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Abrufen des Transports", e);
        }
    }

    public static TransportDocument getTransportDocumentFromReference(Reference<TransportDocument> transportDocumentReference) {
        try {
            return executor.submit(() -> {
                DataHandler dataHandler = DataHandler.instance();
                dataHandler.initServerConnection();
                return dataHandler.getTransportDocumentById(transportDocumentReference.id());
            }).get();
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Abrufen des Transportdokuments", e);
        }
    }

    public static TransportDetails closeInvoice(Id<TransportDetails> id) {
        try {
            return executor.submit(() -> {
                DataHandler dataHandler = DataHandler.instance();
                dataHandler.initServerConnection();
                return dataHandler.closeInvoice(id);
            }).get();
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Schließen der Rechnung", e);
        }
    }
}
