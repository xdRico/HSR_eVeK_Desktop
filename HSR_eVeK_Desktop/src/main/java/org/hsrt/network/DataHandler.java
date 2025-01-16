package org.hsrt.network;

import de.ehealth.evek.api.entity.*;
import de.ehealth.evek.api.exception.IllegalProcessException;
import de.ehealth.evek.api.exception.ProcessingException;
import de.ehealth.evek.api.network.IComClientReceiver;
import de.ehealth.evek.api.network.IComClientSender;
import de.ehealth.evek.api.type.Id;
import de.ehealth.evek.api.type.Reference;
import de.ehealth.evek.api.type.TransportReason;
import de.ehealth.evek.api.type.TransportationType;
import de.ehealth.evek.api.util.COptional;
import de.ehealth.evek.api.util.Log;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hsrt.network.config.SocketConfig;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;


public class DataHandler implements IsInitializedListener {
    private static DataHandler instance;

    public static DataHandler instance(){
        return instance == null ? (instance = new DataHandler()) : instance;
    }

    private final SocketConfig socketConfig = new SocketConfig();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private ServerConnection serverConnection = new ServerConnection();
    private static User loggedInUser = null;

    private final ObservableList<TransportDocument> transportDocuments = FXCollections.observableArrayList();
    private final List<Id<TransportDocument>> transportDocumentIDs = new ArrayList<>();

    private IComClientReceiver receiver;
    private IComClientSender sender;

    public DataHandler() {

    }

    public void initServerConnection(){
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

        //TODO find out why this is not working

        try{
            TransportDocument.Create cmd = new TransportDocument.Create(patient, insuranceData, transportReason, startDate, endDate,
                    weeklyFrequency, healthcareServiceProvider,transportationType, additionalInfo, Reference.to(loggedInUser.id()));
            sender.sendTransportDocument(cmd);
            System.out.println("Transport Document sent");
            TransportDocument created = receiver.receiveTransportDocument();
            addTransportDocument(created);
            return created;
        }catch(Exception e){
            Log.sendException(e);
            throw new ProcessingException(e);
        }
    }

    public TransportDocument getTransportDocumentById(Id<TransportDocument> transportDocID) throws IllegalProcessException {
        try {
            sender.sendTransportDocument(new TransportDocument.Get(transportDocID));
            return receiver.receiveTransportDocument();
        }catch(Exception e){
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
            transportDocumentIDs.clear();

            sender.sendTransportDocument(new TransportDocument.GetList(new TransportDocument.Filter(COptional.empty(), COptional.empty(),COptional.empty(),COptional.empty(),COptional.of(loggedInUser.serviceProvider()), COptional.empty(),COptional.empty())));
            List<TransportDocument> tempTransportDocs = (List<TransportDocument>) receiver.receiveList();
            System.out.println(tempTransportDocs);

            transportDocuments.addAll(tempTransportDocs);
            return transportDocuments;
        }catch(Exception e){
            Log.sendException(e);
            throw new ProcessingException(e);
        }
    }
    //endregion


}
