package org.hsrt.ui.controllers;

import de.ehealth.evek.api.entity.Address;
import de.ehealth.evek.api.entity.ServiceProvider;
import de.ehealth.evek.api.entity.TransportDetails;
import de.ehealth.evek.api.entity.TransportDocument;
import de.ehealth.evek.api.type.*;
import de.ehealth.evek.api.util.COptional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
        //TODO: Implement this method to fetch transports from the API
        Reference<TransportDocument> transportDocumentReference = Reference.to(transportDocument.id().toString());
        Reference<Address> addressReference = Reference.to("address");
        Reference<Address> addressReference2 = Reference.to("address2");
        Date date = Date.valueOf("2023-01-01");

        ObservableList<TransportDetails> transports = FXCollections.observableArrayList(
                new TransportDetails(new Id<TransportDetails>("1"), transportDocumentReference,date , COptional.empty(), COptional.of(addressReference2), COptional.of(Direction.Outward), COptional.of(PatientCondition.LyingDown), null, null, null, null, COptional.of(date), null, COptional.of(date)),
                new TransportDetails(new Id<TransportDetails>("2"), transportDocumentReference,date, COptional.of(addressReference2), COptional.of(addressReference), COptional.of(Direction.Return), COptional.of(PatientCondition.CarryingChair), null, null, null, null, COptional.of(date), null, COptional.of(date))
        );

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

    public static Address getAddressFromReference(Reference<Address> startAddressreference) {
        //TODO: Implement this method to fetch an address from the API
        return new Address(new Id<Address>("1"), null, "street", "25", "GER", "72147", "Nehren");
    }

    public static String getTransportTour(Id<TransportDocument> transportDocumentId) {
        //TODO: Implement this method to fetch a transport tour from the API and add 1 to the amount of tours

        return "10";
    }

    public static TransportDetails createTransport(TransportDocument transportDocument, Date sqlDate) {
        //TODO: Implement this method to create a new transport
        TransportDetails transport = new TransportDetails(new Id<TransportDetails>("3"), Reference.to(transportDocument.id().toString()), sqlDate, COptional.empty(), COptional.empty(), COptional.empty(), COptional.empty(), null, null, null, null, COptional.empty(), null, COptional.empty());

        return transport;
    }
}
