package org.hsrt.ui.controllers;

import de.ehealth.evek.api.entity.*;
import de.ehealth.evek.api.type.TransportationType;



import de.ehealth.evek.api.type.Id;
import de.ehealth.evek.api.type.Reference;
import de.ehealth.evek.api.type.TransportReason;
import de.ehealth.evek.api.util.COptional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.sql.Date;

public class TransportDocumentController {
    public static ObservableList<TransportDocument> getTransportDocuments(User user) {
        //TODO: Fetch patient data from the API via controller
        Patient patient = new Patient(new Id<Patient>("1"), new Reference<>(new Id<>("1")), "Mustermann", "Max", Date.valueOf("1990-01-01"), new Reference<>(new Id<>("1")));
        Reference<Patient> patientReference = new Reference<>(new Id<>(patient.insuranceNumber().toString()));
        Reference<InsuranceData> insuranceDataReference = new Reference<>(new Id<InsuranceData>("3"));
        Reference<User> userReference = Reference.to(user.id().toString());

        ObservableList<TransportDocument> transportDocuments = FXCollections.observableArrayList(
                new TransportDocument(new Id<TransportDocument>("1"), COptional.of(patientReference), COptional.of(insuranceDataReference), TransportReason.EmergencyTransport, Date.valueOf("2023-01-01"), null, null, null, TransportationType.KTW, null, userReference, false),
                new TransportDocument(new Id<TransportDocument>("2"), COptional.of(patientReference),  COptional.of(insuranceDataReference), TransportReason.HighFrequent, Date.valueOf("2023-02-01"), null, null, null, TransportationType.RTW, null, userReference, false),
                new TransportDocument(new Id<TransportDocument>("3"), COptional.of(patientReference),  COptional.of(insuranceDataReference), TransportReason.ContinuousImpairment, Date.valueOf("2023-03-01"), null, null, null, TransportationType.NAWorNEF, null, userReference, false)
        );
        return transportDocuments;

    }
}
