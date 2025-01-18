package org.hsrt.ui.controllers;

import de.ehealth.evek.api.entity.Address;
import de.ehealth.evek.api.entity.ServiceProvider;
import de.ehealth.evek.api.entity.TransportDetails;
import de.ehealth.evek.api.entity.TransportDocument;
import de.ehealth.evek.api.type.*;
import de.ehealth.evek.api.util.COptional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hsrt.network.DataHandler;

import java.sql.Date;
import java.util.List;

/**
 * Controller class for managing transport details.
 */

public class TransportDetailsController {
    /**
     * Fetches all transports from the database or API.
     *
     * @param transportDocument The transport document to fetch transports for.
     * @return A list of all transports.
     */
    public static ObservableList<TransportDetails> getTransports(TransportDocument transportDocument) {
        DataHandler dataHandler = DataHandler.instance();
        dataHandler.initServerConnection();

        ObservableList<TransportDetails> transports = FXCollections.observableArrayList();
        transports = dataHandler.refreshTransports(transportDocument);

        return transports;
    }

    public static List<String> getTransportproviders() {
        //TODO: Implement this method to fetch transport providers from the API
        return List.of("Provider 1", "Provider 2");
    }

    public static ServiceProvider getTransportproviderFromReference(COptional<Reference<ServiceProvider>> transportProviderReference) {
        //TODO: Implement this method to fetch a transport provider from the API
        return new ServiceProvider(new Id<ServiceProvider>("1"), "Provider 1", "123456789", false, true, new Reference<>(new Id<>("1")), COptional.of("skurr"));
    }

    public static Address getAddressFromReference(COptional<Reference<Address>> startAddressreference) {
        DataHandler dataHandler = DataHandler.instance();
        dataHandler.initServerConnection();

        return dataHandler.getAddressFromReference(startAddressreference);
    }

    public static String getTransportTourNumber(Id<TransportDocument> transportDocumentId) {
        //TODO: Implement this method to fetch a transport tour from the API and add 1 to the amount of tours

        return "10";
    }

    public static TransportDetails createTransport(TransportDocument transportDocument, Date sqlDate) {
        DataHandler dataHandler = DataHandler.instance();

        dataHandler.initServerConnection();

        return dataHandler.createTransport(transportDocument, sqlDate);

    }

    public static void updateTransport(Id<TransportDetails> id, COptional<Address> startAddress, COptional<Address> endAddress, COptional<Direction> direction, COptional<PatientCondition> patientCondition, COptional<String> tourNumber, COptional<Boolean> paymentExemption, String transporterSignature, Date transporterSignatureDate, String patientSignature, Date patientSignatureDate) {
        DataHandler dataHandler = DataHandler.instance();

        dataHandler.initServerConnection();

        dataHandler.updateTransport(id, startAddress, endAddress, direction, patientCondition, tourNumber, paymentExemption, transporterSignature, transporterSignatureDate, patientSignature, patientSignatureDate);
    }
    /*
    public static void deleteTransport(Id<TransportDetails> id) {
        DataHandler dataHandler = DataHandler.instance();

        dataHandler.initServerConnection();

        dataHandler.deleteTransport(id);
    }

     */

    public static Address createAddress(String street, String houseNumber, String zipCode, String city, String country) {
        DataHandler dataHandler = DataHandler.instance();

        dataHandler.initServerConnection();

        return dataHandler.createAddress(new Address(null, COptional.empty(), street, houseNumber, zipCode, city, country));
    }

}
