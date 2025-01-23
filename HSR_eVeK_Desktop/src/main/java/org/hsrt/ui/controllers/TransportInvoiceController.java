package org.hsrt.ui.controllers;

import de.ehealth.evek.api.entity.Insurance;
import de.ehealth.evek.api.entity.TransportDetails;
import de.ehealth.evek.api.entity.TransportDocument;
import de.ehealth.evek.api.entity.User;
import de.ehealth.evek.api.type.Id;
import de.ehealth.evek.api.type.Reference;
import javafx.collections.ObservableList;
import org.hsrt.network.DataHandler;

public class TransportInvoiceController {


    public static void sendToInsurance(TransportDetails transport) {
        DataHandler dataHandler = DataHandler.instance();
        dataHandler.initServerConnection();
        TransportDetails updatedTransport = dataHandler.sendToInsurance(transport);
        System.out.println("Transport gesendet");

    }

    public static ObservableList<TransportDetails> getPendingInvoiceTransports(User user) {

        DataHandler dataHandler = DataHandler.instance();
        dataHandler.initServerConnection();
        return dataHandler.getPendingInvoiceTransports(user);


    }

    public static ObservableList<TransportDetails> getSentInvoiceTransports(User user) {
        DataHandler dataHandler = DataHandler.instance();
        dataHandler.initServerConnection();
        return dataHandler.getSentInvoiceTransports(user);

    }

    public static ObservableList<Insurance> getInsurances(User user) {
        DataHandler dataHandler = DataHandler.instance();
        dataHandler.initServerConnection();
        return dataHandler.getInsurances(user);
    }

    public static TransportDocument getTransportDocumentFromReference(Reference<TransportDocument> transportDocumentReference) {
        try {

                DataHandler dataHandler = DataHandler.instance();
                dataHandler.initServerConnection();
                return dataHandler.getTransportDocumentById(transportDocumentReference.id());

        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Abrufen des Transportdokuments", e);
        }
    }

    public static ObservableList<TransportDocument> getTransportDocuments(User user) {
        DataHandler dataHandler = DataHandler.instance();
        dataHandler.initServerConnection();
        try {
            return dataHandler.refreshTransportDocuments();
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Abrufen der Transportdokumente", e);
        }
    }
}
