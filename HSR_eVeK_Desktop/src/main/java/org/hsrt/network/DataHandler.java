package org.hsrt.network;

import de.ehealth.evek.api.entity.*;
import de.ehealth.evek.api.exception.IllegalProcessException;
import de.ehealth.evek.api.exception.ProcessingException;
import de.ehealth.evek.api.network.IComClientReceiver;
import de.ehealth.evek.api.network.IComClientSender;
import de.ehealth.evek.api.type.*;
import de.ehealth.evek.api.util.COptional;
import de.ehealth.evek.api.util.Log;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hsrt.network.config.SocketConfig;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;


public class DataHandler implements IsInitializedListener {
    private static DataHandler instance;

    public static DataHandler instance() {
        return instance == null ? (instance = new DataHandler()) : instance;
    }

    private final SocketConfig socketConfig = new SocketConfig();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private ServerConnection serverConnection = new ServerConnection();
    private static User loggedInUser = null;

    private final ObservableList<TransportDocument> transportDocuments = FXCollections.observableArrayList();
    private final ObservableList<TransportDetails> transportDetails = FXCollections.observableArrayList();

    private IComClientReceiver receiver;
    private IComClientSender sender;

    public DataHandler() {

    }

    public void initServerConnection() {
        executorService.submit(() -> {
            serverConnection.setServerAddress(socketConfig.get("server.ip"));
            serverConnection.setServerPort(socketConfig.getInt("server.port"));
            serverConnection.addIsInitializedListener(this);
            serverConnection.initConnection();
        });
    }

    @Override
    public void onInitializedStateChanged(boolean isInitialized) {
        if (isInitialized) {
            this.receiver = serverConnection.getComClientReceiver();
            this.sender = serverConnection.getComClientSender();
        }
    }


    public boolean isInitialized() {
        return this.receiver != null && this.sender != null;
    }

    //region Getters and Setters

    /**
     * Versucht, den Benutzer mit den angegebenen Anmeldeinformationen anzumelden.
     *
     * @param username Der Benutzername.
     * @param password Das Passwort.
     * @return `true`, wenn die Anmeldung erfolgreich war, sonst `false`.
     */
    public User tryLogin(String username, String password) {
        try {
            // Sende Anmeldeanfrage an den Server
            this.sender.loginUser(username, password);

            try {
                loggedInUser = this.receiver.receiveUser();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (loggedInUser != null) {
                System.out.println("Login erfolgreich!");
                return loggedInUser;
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public TransportDocument createTransportDocument(COptional<Reference<Patient>> patient,
                                                     COptional<Reference<InsuranceData>> insuranceData,
                                                     TransportReason transportReason,
                                                     Date startDate,
                                                     COptional<Date> endDate,
                                                     COptional<Integer> weeklyFrequency,
                                                     Reference<ServiceProvider> healthcareServiceProvider,
                                                     TransportationType transportationType,
                                                     COptional<String> additionalInfo) throws ProcessingException {
        System.out.println("Creating Transport Document" +
                "Patient: " + patient +
                "InsuranceData: " + insuranceData +
                "TransportReason: " + transportReason +
                "StartDate: " + startDate +
                "EndDate: " + endDate +
                "WeeklyFrequency: " + weeklyFrequency +
                "HealthcareServiceProvider: " + healthcareServiceProvider +
                "TransportationType: " + transportationType +
                "AdditionalInfo: " + additionalInfo +
                "LoggedInUser: " + loggedInUser);


        try {
            TransportDocument.Create cmd = new TransportDocument.Create(patient, insuranceData, transportReason, startDate, endDate,
                    weeklyFrequency, healthcareServiceProvider, transportationType, additionalInfo, Reference.to(loggedInUser.id().value()));
            sender.sendTransportDocument(cmd);
            System.out.println("Transport Document sent");
            TransportDocument created = null;
            created = receiver.receiveTransportDocument();
            System.out.println("Transport Document received");
            addTransportDocument(created);
            return created;
        } catch (Exception e) {
            Log.sendException(e);
            throw new ProcessingException(e);
        }
    }

    public TransportDocument getTransportDocumentById(Id<TransportDocument> transportDocID) throws IllegalProcessException {
        try {
            sender.sendTransportDocument(new TransportDocument.Get(transportDocID));
            return receiver.receiveTransportDocument();
        } catch (Exception e) {
            Log.sendException(e);
            throw new IllegalProcessException(e);
        }
    }

    private void addTransportDocument(TransportDocument transportDocumentToAdd) {
        transportDocuments.add(transportDocumentToAdd);
        //transportDocumentIDs.add(transportDocumentToAdd.id());
    }

    public ObservableList<TransportDocument> getTransportDocuments() {
        return transportDocuments;
    }

    public ObservableList<TransportDocument> refreshTransportDocuments() throws ProcessingException {
        try {
            transportDocuments.clear();

            sender.sendTransportDocument(new TransportDocument.GetList(new TransportDocument.Filter(COptional.empty(), COptional.empty(), COptional.empty(), COptional.empty(), COptional.of(loggedInUser.serviceProvider()), COptional.empty(), COptional.empty())));
            List<TransportDocument> tempTransportDocs = (List<TransportDocument>) receiver.receiveList();
            System.out.println(tempTransportDocs);

            transportDocuments.addAll(tempTransportDocs);
            return transportDocuments;
        } catch (Exception e) {
            Log.sendException(e);
            throw new ProcessingException(e);
        }
    }


    public TransportDocument updateTransportDocument(Id<TransportDocument> id, COptional<Reference<Patient>> patientOpt, COptional<Reference<InsuranceData>> insuranceDataOpt, TransportReason transportReason, Date startDate, COptional<Date> endDateOpt, COptional<Integer> weeklyFrequencyOpt, Reference<ServiceProvider> healthcareServiceProvider, TransportationType transportationType, COptional<String> additionalInfoOpt) {
        try {
            TransportDocument.Update cmd = new TransportDocument.Update(id, transportReason, startDate, endDateOpt, weeklyFrequencyOpt, healthcareServiceProvider, transportationType, additionalInfoOpt, Reference.to(loggedInUser.id()));
            TransportDocument original = getTransportDocumentById(id);

            sender.sendTransportDocument(cmd);
            TransportDocument updated = receiver.receiveTransportDocument();

            if(original.patient().equals(patientOpt)){
                TransportDocument.AssignPatient cmd2 = new TransportDocument.AssignPatient(id, patientOpt.get(), insuranceDataOpt.get());
                sender.sendTransportDocument(cmd2);
                receiver.receiveTransportDocument();
            }

            return updated;
        } catch (Exception e) {
            Log.sendException(e);
            return null;
        }
    }

    public String getInsuranceData(String patientNumber) {
        try {
            sender.sendPatient(new Patient.Get(new Id<Patient>(patientNumber)));
            Patient patient = receiver.receivePatient();
            return patient.insuranceData().toString();
        } catch (Exception e) {
            Log.sendException(e);
            return null;
        }
    }

    public ObservableList<TransportDetails> getTransports(TransportDocument transportDocument) {
        return transportDetails;
    }

    public ObservableList<TransportDetails> refreshTransports(TransportDocument transportDocument) {
        try {
            // Liste leeren
            transportDetails.clear();

            // Anfrage senden
            sender.sendTransportDetails(new TransportDetails.GetList(
                    new TransportDetails.Filter(
                            COptional.of(Reference.to(transportDocument.id())),
                            COptional.empty(),
                            COptional.empty(),
                            COptional.empty(),
                            COptional.empty()
                    )
            ));

            // Antwort empfangen
            List<TransportDetails> tempTransports = (List<TransportDetails>) receiver.receiveList();

            // Null-Prüfung
            if (tempTransports == null) {
                System.err.println("Warnung: receiver.receiveList() hat null zurückgegeben.");
                tempTransports = List.of(); // Ersetze durch leere Liste
            }

            // Daten hinzufügen
            transportDetails.addAll(tempTransports);

            return transportDetails;
        } catch (Exception e) {
            // Fehler protokollieren
            Log.sendException(e);

            // Leere Liste zurückgeben
            return null;
        }
    }


    public TransportDetails createTransport(TransportDocument transportDocument, Date sqlDate) {
        try {
            TransportDetails.Create cmd = new TransportDetails.Create(Reference.to(transportDocument.id()), sqlDate);
            sender.sendTransportDetails(cmd);
            System.out.println(cmd);
            return receiver.receiveTransportDetails();
        } catch (Exception e) {
            Log.sendException(e);
            return null;
        }
    }

    public Address createAddress(Address addressOpt) {
        try{
            sender.sendAddress(new Address.GetList(new Address.Filter(COptional.of(addressOpt.streetName()), COptional.of(addressOpt.postCode()), COptional.of(addressOpt.city()), COptional.empty())));
            List<Address> addresses = (List<Address>) receiver.receiveList();
            if(!addresses.isEmpty()){
                return addresses.getFirst();
            }
        } catch (Exception e) {
            Log.sendException(e);
        }
        try {
            Address.Create cmd = new Address.Create(COptional.empty(), addressOpt.streetName(), addressOpt.houseNumber(), addressOpt.country(), addressOpt.postCode(), addressOpt.city());
            sender.sendAddress(cmd);
            return receiver.receiveAddress();
        } catch (Exception e) {
            Log.sendException(e);
            return null;
        }
    }

    public TransportDetails updateTransport(Id<TransportDetails> id, COptional<Address> startAddressOpt, COptional<Address> endAddressOpt, COptional<Direction> directionOpt, COptional<PatientCondition> patientConditionOpt, COptional<String> tourNumber, COptional<Boolean> paymentExemption, String transporterSignature, Date transporterSignatureDate, String patientSignature, Date patientSignatureDate, Reference<ServiceProvider> serviceProviderReference) {
        {
            try {

                TransportDetails.Update cmd = new TransportDetails.Update(id, COptional.of(Reference.to(startAddressOpt.get().id())), COptional.of(Reference.to(endAddressOpt.get().id())), directionOpt, patientConditionOpt, tourNumber, paymentExemption);
                sender.sendTransportDetails(cmd);
                TransportDetails newTransport = receiver.receiveTransportDetails();
                if(!serviceProviderReference.id().equals(new Id<>("-1"))){
                    sender.sendTransportDetails(new TransportDetails.AssignTransportProvider(id, serviceProviderReference));
                    receiver.receiveTransportDetails();
                }
                System.out.println(transporterSignature);
                System.out.println(transporterSignatureDate);
                System.out.println(patientSignature);
                System.out.println(patientSignatureDate);
                TransportDetails.UpdateTransporterSignature cmd2 = new TransportDetails.UpdateTransporterSignature(newTransport.id(), transporterSignature, transporterSignatureDate);
                TransportDetails.UpdatePatientSignature cmd3 = new TransportDetails.UpdatePatientSignature(newTransport.id(), patientSignature, patientSignatureDate);
                sender.sendTransportDetails(cmd2);
                receiver.receiveTransportDetails();
                sender.sendTransportDetails(cmd3);
                System.out.println(receiver.receiveTransportDetails());
                return newTransport;
            } catch (Exception e) {
                Log.sendException(e);
                System.out.println("FEHLER");
                return null;
            }
        }
    }

    public Address getAddressFromReference(COptional<Reference<Address>> AddressReference) {
        try {
            sender.sendAddress(new Address.Get(AddressReference.get().id()));
            return receiver.receiveAddress();
        } catch (Exception e) {
            Log.sendException(e);
            return null;
        }
    }

    public List<ServiceProvider> getTransportProviders() {
        try {
            sender.sendServiceProvider(new ServiceProvider.GetList(new ServiceProvider.Filter(COptional.empty(), COptional.empty(), COptional.empty(), COptional.of(true), COptional.empty())));
            List<ServiceProvider> providers = (List<ServiceProvider>) receiver.receiveList();

            System.out.println(providers);
            return providers;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //endregion


    }

    public ServiceProvider getTransportProviderFromReference(COptional<Reference<ServiceProvider>> transportProviderReference) {
        try {
            sender.sendServiceProvider(new ServiceProvider.Get(transportProviderReference.get().id()));
            return receiver.receiveServiceProvider();
        } catch (Exception e) {
            Log.sendException(e);
            return null;
        }
    }
}
