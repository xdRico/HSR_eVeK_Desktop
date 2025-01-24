package org.hsrt.network;

import de.ehealth.evek.api.entity.*;
import de.ehealth.evek.api.exception.IllegalProcessException;
import de.ehealth.evek.api.exception.ProcessingException;
import de.ehealth.evek.api.network.IComClientReceiver;
import de.ehealth.evek.api.network.IComClientSender;
import de.ehealth.evek.api.type.*;
import de.ehealth.evek.api.util.COptional;
import de.ehealth.evek.api.util.Log;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.hsrt.network.config.SocketConfig;

import java.io.IOException;
import java.sql.Date;
import java.util.*;
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
    private final ObservableList<TransportDetails> createdTransports = FXCollections.observableArrayList();

    private IComClientReceiver receiver;
    private IComClientSender sender;


    public DataHandler() {

    }

    /**
     * Initialisiert die Serververbindung.
     */


    public void initServerConnection() {
        executorService.submit(() -> {
            serverConnection.setServerAddress(socketConfig.get("server.ip"));
            serverConnection.setServerPort(socketConfig.getInt("server.port"));
            serverConnection.addIsInitializedListener(this);
            serverConnection.initConnection();
        });
    }

    /**
     * Wird aufgerufen, wenn sich der Initialisierungszustand der Serververbindung ändert.
     *
     * @param isInitialized `true`, wenn die Verbindung initialisiert ist, sonst `false`.
     */

    @Override
    public void onInitializedStateChanged(boolean isInitialized) {
        if (isInitialized) {
            this.receiver = serverConnection.getComClientReceiver();
            this.sender = serverConnection.getComClientSender();
        }
    }


    /**
     * Gibt an, ob die Serververbindung initialisiert ist.
     *
     * @return `true`, wenn die Verbindung initialisiert ist, sonst `false`.
     */

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






        try {
            TransportDocument.Create cmd = new TransportDocument.Create(patient.equals(COptional.empty()) ? COptional.empty() : patient, insuranceData.equals(COptional.empty()) ? COptional.empty() : insuranceData, transportReason, startDate, endDate,
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
            sender.sendTransportDocument(new TransportDocument.GetList(new TransportDocument.Filter(COptional.empty(), COptional.empty(), COptional.empty(), COptional.empty(), COptional.empty() , COptional.empty(), COptional.empty())));
            List<TransportDocument> tempTransportDocs = (List<TransportDocument>) receiver.receiveList();
            System.out.println(tempTransportDocs);

            transportDocuments.addAll(tempTransportDocs);
            return transportDocuments;
        } catch (Exception e) {
            System.out.println("Fehler beim Aktualisieren der Transportdokumente.");
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

    public InsuranceData getInsuranceData(String patientNumber) {
        try {
            Patient patient = getPatient(patientNumber);
            sender.sendInsuranceData(new InsuranceData.Get(patient.insuranceData().id()));
            return receiver.receiveInsuranceData();
        } catch (Exception e) {
            Log.sendException(e);
            return null;
        }
    }

    private Patient getPatient(String patientNumber) {
        try {
            sender.sendPatient(new Patient.Get(new Id<Patient>(patientNumber)));
            return receiver.receivePatient();
        } catch (Exception e) {

            Log.sendException(e);
            return null;
        }
    }

    public ObservableList<TransportDetails> getCreatedTransports(TransportDocument transportDocument) {
        return createdTransports.filtered(transportDetails -> transportDetails.transportDocument().equals(Reference.to(transportDocument.id())));
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
            System.err.println("Fehler beim Aktualisieren der Transporte.");
            ObservableList<TransportDetails> emptyList = FXCollections.observableArrayList();
            // Leere Liste zurückgeben
            return emptyList;
        }
    }


    public TransportDetails createTransport(TransportDocument transportDocument, Date sqlDate) {
        try {
            TransportDetails.Create cmd = new TransportDetails.Create(Reference.to(transportDocument.id()), sqlDate);
            sender.sendTransportDetails(cmd);
            System.out.println(cmd);
            TransportDetails newTransportDetails = receiver.receiveTransportDetails();
            System.out.println(newTransportDetails);
            addTransport(newTransportDetails);
            return newTransportDetails;
        } catch (Exception e) {
            Log.sendException(e);
            System.out.println("FEHLER");
            return null;
        }
    }

    private ObservableList<TransportDetails> addTransport(TransportDetails newTransportDetails) {
        createdTransports.add(newTransportDetails);
        return createdTransports;
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
                if(transporterSignature != null && transporterSignatureDate != null){
                    TransportDetails.UpdateTransporterSignature cmd2 = new TransportDetails.UpdateTransporterSignature(newTransport.id(), transporterSignature, transporterSignatureDate);
                    sender.sendTransportDetails(cmd2);
                    receiver.receiveTransportDetails();
                }
                if(patientSignature != null && patientSignatureDate != null){
                    TransportDetails.UpdatePatientSignature cmd3 = new TransportDetails.UpdatePatientSignature(newTransport.id(), patientSignature, patientSignatureDate);


                    sender.sendTransportDetails(cmd3);
                    System.out.println(receiver.receiveTransportDetails());
                }

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

    public void deleteTransport(TransportDetails transport) {
        try {
            createdTransports.remove(transport);
            sender.sendTransportDetails(new TransportDetails.Delete(transport.id()));
            receiver.receiveTransportDetails();
        } catch (Exception e) {
            Log.sendException(e);
        }
    }

    public TransportDetails getTransportFromReference(String transportReference) {
        try {
            sender.sendTransportDetails(new TransportDetails.Get(new Id<TransportDetails>(transportReference)));
            return receiver.receiveTransportDetails();
        } catch (Exception e) {
            Log.sendException(e);
            return null;
        }
    }

    public ServiceProvider getHealthcareProvider(User user) {
        try {
            sender.sendServiceProvider(new ServiceProvider.Get(user.serviceProvider().id()));
            return receiver.receiveServiceProvider();
        } catch (Exception e) {
            Log.sendException(e);
            return null;
        }
    }

    public ObservableList<User> getUsers(User user) {
        try {
            sender.sendUser(new User.GetList(new User.Filter(COptional.empty(), COptional.empty(), COptional.empty(), user.role() == UserRole.SuperUser ? COptional.empty() : COptional.of(user.serviceProvider()), COptional.empty())));
            List<User> users = (List<User>) receiver.receiveList();
            return FXCollections.observableArrayList(users);
        } catch (Exception e) {
            Log.sendException(e);
            return FXCollections.observableArrayList();
        }

    }

    public User createUser(User user, String username, String password) {
        try {
            System.out.println(user);
            System.out.println(username);
            System.out.println(password);
            sender.sendUser(new User.Create(username, password, user.lastName(), user.firstName(), user.address(), user.serviceProvider(), user.role()));
            User createdUser = receiver.receiveUser();
            System.out.println(createdUser);
            return createdUser;
        } catch (Exception e) {
            Log.sendException(e);
            return null;
        }
    }

    public void updateUser(User updatedUser) {
        try {
            sender.sendUser(new User.Update(updatedUser.id(), updatedUser.lastName(), updatedUser.firstName(), updatedUser.address(), updatedUser.serviceProvider()));
            receiver.receiveUser();
        } catch (Exception e) {
            Log.sendException(e);
        }
    }

    public void updateUserCredentials(User user, String username, String oldpassword, String newpassword) {
        try {
            sender.sendUser(new User.UpdateCredentials(user.id(), username, oldpassword, newpassword));
            receiver.receiveUser();
        } catch (Exception e) {
            Log.sendException(e);
        }
    }

    public TransportDetails sendToInsurance(TransportDetails transport) {
        try {
            System.out.println(transport);
            sender.sendTransportDetails(new TransportDetails.Invoice(transport.id()));
            TransportDetails sentTransport = receiver.receiveTransportDetails();
            System.out.println(sentTransport);

            return sentTransport;
        } catch (Exception e) {
            Log.sendException(e);
            return null;
        }
    }

    public ObservableList<TransportDetails> getPendingInvoiceTransports(User user) {
        try {
            System.out.println(user.serviceProvider());
            sender.sendTransportDetails(new TransportDetails.GetList(new TransportDetails.Filter(COptional.empty(), COptional.empty(), COptional.empty(), COptional.empty() , COptional.of(loggedInUser.serviceProvider()) ,COptional.of(ProcessingState.signed))));
            List<TransportDetails> transports = (List<TransportDetails>) receiver.receiveList();
            System.out.println(transports);
            return FXCollections.observableArrayList(transports);
        }
        catch (Exception e) {
            Log.sendException(e);
            System.out.println("FEHLER getPendingInvoiceTransports");
            System.out.println(e);
            return FXCollections.observableArrayList();
        }

    }

    public ObservableList<TransportDetails> getSentInvoiceTransports(User user) {
        try{
            sender.sendTransportDetails(new TransportDetails.GetList(new TransportDetails.Filter(COptional.empty(), COptional.empty(), COptional.empty(), COptional.empty() , COptional.of(loggedInUser.serviceProvider()) ,COptional.of(ProcessingState.invoiced))));
            List<TransportDetails> transports = (List<TransportDetails>) receiver.receiveList();
            System.out.println("gesendete Transports" + transports);
            return FXCollections.observableArrayList(transports);
        }
        catch (Exception e) {
            System.out.println("FEHLER getSentInvoiceTransports");
            Log.sendException(e);
            return FXCollections.observableArrayList();
        }
    }

    public ObservableList<Insurance> getInsurances(User user) {
        try {
            sender.sendInsurance(new Insurance.GetList(new Insurance.Filter(COptional.empty(),COptional.empty())));
            List<Insurance> insurances = (List<Insurance>) receiver.receiveList();
            return FXCollections.observableArrayList(insurances);
        } catch (Exception e) {
            Log.sendException(e);
            return FXCollections.observableArrayList();
        }

    }

    public TransportDocument archiveTransportDocument(Id<TransportDocument> id) {
        try {
            sender.sendTransportDocument(new TransportDocument.Archive(id));
            return receiver.receiveTransportDocument();
        } catch (Exception e) {
            Log.sendException(e);
            return null;
        }
    }

    public TransportDetails closeInvoice(Id<TransportDetails> id) {
        try {
            sender.sendTransportDetails(new TransportDetails.Close(id));
            return receiver.receiveTransportDetails();
        } catch (Exception e) {
            Log.sendException(e);
            return null;
        }
    }

    public InsuranceData createInsuranceData(COptional<Reference<Patient>> patientOpt, Reference<Insurance> insurance, String insuranceStatus) {
        try {
            sender.sendInsuranceData(new InsuranceData.Create(patientOpt.get(),insurance, Integer.parseInt(insuranceStatus)));
            return receiver.receiveInsuranceData();
        } catch (Exception e) {
            Log.sendException(e);
            System.out.println("FEHLER createInsuranceData");
            return null;
        }
    }

    public InsuranceData getInsuranceDataByID(Id<InsuranceData> id) {
        try {
            sender.sendInsuranceData(new InsuranceData.Get(id));
            return receiver.receiveInsuranceData();
        } catch (Exception e) {
            Log.sendException(e);
            return null;
        }
    }
}

