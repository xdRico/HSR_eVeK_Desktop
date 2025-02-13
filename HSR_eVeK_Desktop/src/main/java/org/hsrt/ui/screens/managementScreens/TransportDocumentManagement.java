package org.hsrt.ui.screens.managementScreens;

import de.ehealth.evek.api.entity.*;
import de.ehealth.evek.api.type.*;
import de.ehealth.evek.api.util.COptional;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.hsrt.ui.controllers.TransportDocumentController;

import java.sql.Date;
import java.util.concurrent.atomic.AtomicReference;


/**
 * This class represents the window for managing transport documents.
 * Here, transport documents can be displayed, edited, and deleted.
 * New transport documents can also be created.
 * by clicking onto a row in the table, the user can view individual transports and edit the transport document.
 */

public class TransportDocumentManagement {
    private ObservableList<TransportDocument> transportDocuments;
    private User user;

    /**
     * Creates the window for managing transport documents.
     * The user can view, create, edit, and delete transport documents.
     *
     * @param user The currently logged-in user.
     *
     * @return The created stage.
     */

    public Stage createTransportDocumentManagement(User user) {

        this.user = user;

        Stage stage = new Stage();
        stage.setTitle("Transport Document Management");


        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));

        transportDocuments = TransportDocumentController.getTransportDocuments(user);



        TableView<TransportDocument> tableView = new TableView<>(transportDocuments);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.setRowFactory(tv -> {
            TableRow<TransportDocument> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    TransportDocument clickedDoc = row.getItem();
                    showOptionsWindow(clickedDoc, user);
                }
            });
            return row;
        });

        // Spalten hinzufügen
        addColumnsToTableView(tableView);

        Button createButton = new Button("Transportdokument erstellen");
        createButton.setOnAction(e -> {
            Stage createTransportDocumentStage = createTransportDocumentCreationWindow(null);
            createTransportDocumentStage.showAndWait();
            transportDocuments = TransportDocumentController.getTransportDocuments(user);
        });
        if(user.role() == UserRole.InsuranceUser){
            createButton.setDisable(true);
            createButton.setOpacity(0);
        }

        vbox.getChildren().addAll(createButton, tableView);
        Scene scene = new Scene(vbox, 1000, 600);
        stage.setScene(scene);

        return stage;
    }

    /**
     * Adds columns to the TableView.
     *
     * @param tableView TableView to which the columns should be added
     */

    private void addColumnsToTableView(TableView<TransportDocument> tableView) {

        TableColumn<TransportDocument, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().id() != null ? data.getValue().id().toString() : "N/A"));

        TableColumn<TransportDocument, String> patientColumn = new TableColumn<>("Patient");
        patientColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().patient().isPresent() ? data.getValue().patient().get().toString() : "N/A"));

        TableColumn<TransportDocument, String> insuranceDataColumn = new TableColumn<>("Versicherungsdaten");
        insuranceDataColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().insuranceData().isPresent() ? data.getValue().insuranceData().get().toString() : "N/A"));

        TableColumn<TransportDocument, String> transportReasonColumn = new TableColumn<>("Transport Grund");
        transportReasonColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().transportReason() != null ? data.getValue().transportReason().toString() : "N/A"));

        TableColumn<TransportDocument, String> startDateColumn = new TableColumn<>("Startdatum");
        startDateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().startDate() != null ? data.getValue().startDate().toString() : "N/A"));

        TableColumn<TransportDocument, String> endDateColumn = new TableColumn<>("Enddatum");
        endDateColumn.setCellValueFactory(data -> {
            var endDateOptional = data.getValue().endDate();
            return new SimpleStringProperty(!data.getValue().endDate().equals(COptional.empty()) ? endDateOptional.get().toString() : "N/A");});

        TableColumn<TransportDocument, String> weeklyFrequencyColumn = new TableColumn<>("Wöchentliche Häufigkeit");
        weeklyFrequencyColumn.setCellValueFactory(data ->{
            var weeklyFrequencyOptional = data.getValue().weeklyFrequency();
            return new SimpleStringProperty(!data.getValue().weeklyFrequency().equals(COptional.empty()) ? weeklyFrequencyOptional.get().toString() : "N/A");});

        TableColumn<TransportDocument, String> serviceProviderColumn = new TableColumn<>("Gesundheitsdienstleister");
        serviceProviderColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().healthcareServiceProvider() != null ? data.getValue().healthcareServiceProvider().toString() : "N/A"));

        TableColumn<TransportDocument, String> transportationTypeColumn = new TableColumn<>("Transportfahrzeug");
        transportationTypeColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().transportationType() != null ? data.getValue().transportationType().toString() : "N/A"));

        TableColumn<TransportDocument, String> additionalInfoColumn = new TableColumn<>("Zusätzliche Informationen");
        additionalInfoColumn.setCellValueFactory(data ->{
            var additionalInfoOptional = data.getValue().additionalInfo();
            return new SimpleStringProperty(!data.getValue().additionalInfo().equals(COptional.empty()) ? additionalInfoOptional.get() : "N/A");});


        TableColumn<TransportDocument, String> signatureColumn = new TableColumn<>("Signatur");
        signatureColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().signature() != null ? data.getValue().signature().toString() : "N/A"));

        TableColumn<TransportDocument, String> isArchivedColumn = new TableColumn<>("Archivierungsstatus");
        isArchivedColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().isArchived() ? "Ja" : "Nein"));


        //noinspection unchecked
        tableView.getColumns().addAll(
                idColumn,
                patientColumn,
                insuranceDataColumn,
                transportReasonColumn,
                startDateColumn,
                endDateColumn,
                weeklyFrequencyColumn,
                serviceProviderColumn,
                transportationTypeColumn,
                additionalInfoColumn,
                signatureColumn,
                isArchivedColumn
        );
    }

    /**
     * Creates the window for creating or editing a transport document.
     *
     * @param existingDocument The existing transport document to edit or null if a new document should be created.
     * @return The created stage.
     */

    private Stage createTransportDocumentCreationWindow(TransportDocument existingDocument) {
        Stage stage = new Stage();
        stage.setTitle(existingDocument == null ? "Transportdokument erstellen" : "Transportdokument bearbeiten");


        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(root);

        // Patientendaten
        Text patientLabel = new Text("Patientendaten");
        TextField patientField = new TextField();
        patientField.setPromptText("Patientendaten eingeben");
        if (existingDocument != null && existingDocument.patient().isPresent()) {
            patientField.setText(existingDocument.patient().get().toString());
        }

        // Insurance Daten
        TextField insuranceField = new TextField();
        insuranceField.setPromptText("Versicherungsdaten eingeben");
        if (existingDocument != null && existingDocument.insuranceData().isPresent()) {
            insuranceField.setText(existingDocument.insuranceData().get().toString());
        }



        AtomicReference<InsuranceData> insurance = new AtomicReference<InsuranceData>();
        HBox patientInsuranceBox = getHBox(insuranceField, patientField, existingDocument, insurance);
        VBox patientBox = new VBox(5, patientLabel, patientInsuranceBox);


        // Grund der Beförderung
        Text reasonLabel = new Text("1. Grund der Beförderung");
        ToggleGroup reasonGroup = new ToggleGroup();

        RadioButton emergencyTransport = new RadioButton("a1) Notfalltransport");
        RadioButton inpatientTreatment = new RadioButton("a2) voll-/teilstationäre Krankenhausbehandlung");
        RadioButton prePostTreatment = new RadioButton("a3) vor-/nachstationäre Behandlung");
        RadioButton outpatientTreatment = new RadioButton("b) ambulante Behandlung (nur Taxi/Mietwagen)");
        RadioButton otherReason1 = new RadioButton("c) anderer Grund (Genehmigungsfrei, z.B. Fahrten zu Hospizen)");
        RadioButton frequentTreatment = new RadioButton("d1) hochfrequente Behandlung (Dialyse, Onkol-, Chemo- oder Strahlentherapie)");
        RadioButton exceptionalCase = new RadioButton("d2) vergleichbarer Ausnahmefall (wie d1, Begründung unter 4. erforderlich)");
        RadioButton permanentImpairment = new RadioButton("e) dauerhafte Mobilitätsbeeinträchtigung vergleichbar mit b und Behandlungsdauer mindestens 6 Monate (Begründung unter 4. erforderlich)");
        RadioButton otherReason2 = new RadioButton("f) anderer Grund für fahrt mit KTW (z.B. fachgerechtes Lagern, Tragen, Heben erforderlich, Begründung ggf unter 4. erforderlich)");

        emergencyTransport.setToggleGroup(reasonGroup);
        inpatientTreatment.setToggleGroup(reasonGroup);
        prePostTreatment.setToggleGroup(reasonGroup);
        outpatientTreatment.setToggleGroup(reasonGroup);
        otherReason1.setToggleGroup(reasonGroup);
        frequentTreatment.setToggleGroup(reasonGroup);
        exceptionalCase.setToggleGroup(reasonGroup);
        permanentImpairment.setToggleGroup(reasonGroup);
        otherReason2.setToggleGroup(reasonGroup);

        VBox reasonBox = new VBox(5, reasonLabel, emergencyTransport, inpatientTreatment, prePostTreatment, outpatientTreatment, otherReason1, frequentTreatment, exceptionalCase, permanentImpairment, otherReason2);
        if (existingDocument != null) {
            switch (existingDocument.transportReason()) {
                case EmergencyTransport -> reasonGroup.selectToggle(emergencyTransport);
                case FullPartStationary -> reasonGroup.selectToggle(inpatientTreatment);
                case PrePostStationary -> reasonGroup.selectToggle(prePostTreatment);
                case AmbulantTaxi -> reasonGroup.selectToggle(outpatientTreatment);
                case OtherPermitFree -> reasonGroup.selectToggle(otherReason1);
                case HighFrequent -> reasonGroup.selectToggle(frequentTreatment);
                case HighFrequentAlike -> reasonGroup.selectToggle(exceptionalCase);
                case ContinuousImpairment -> reasonGroup.selectToggle(permanentImpairment);
                case OtherKTW -> reasonGroup.selectToggle(otherReason2);
            }
        }
        // Behandlungstage/Behandlungsfrequenz
        Text treatmentLabel = new Text("2. Behandlungstage/Behandlungsfrequenz und nächst erreichbare, geeignete Behandlungsstätte");
        HBox startDateBox = new HBox(10);
        Text startDateLabel = new Text("Startdatum auswählen:");
        DatePicker treatmentDatePicker = new DatePicker();
        startDateBox.getChildren().addAll(startDateLabel, treatmentDatePicker);
        if (existingDocument != null && existingDocument.startDate() != null) {
            treatmentDatePicker.setValue(existingDocument.startDate().toLocalDate());
        }


        HBox endDateBox = new HBox(10);
        Text endDateLabel = new Text("Enddatum auswählen: ");
        DatePicker endDatePicker = new DatePicker();
        endDateBox.getChildren().addAll(endDateLabel, endDatePicker);
        if (existingDocument != null && existingDocument.endDate() != null) {
            endDatePicker.setValue(existingDocument.endDate().isPresent() ? existingDocument.endDate().get().toLocalDate() : null);
        }

        TextField treatmentFacilityField = new TextField();
        treatmentFacilityField.setPromptText("Behandlungsstättenkürzel eingeben");
        if(existingDocument != null && existingDocument.healthcareServiceProvider() != null) {
            treatmentFacilityField.setText(existingDocument.healthcareServiceProvider().toString());
        } else if (user.role() == UserRole.HealthcareUser || user.role() == UserRole.HealthcareDoctor) {
            ServiceProvider healthcareProvider = TransportDocumentController.getHealthcareProvider(user);
            treatmentFacilityField.setText(healthcareProvider.id().toString());


        }

        TextField weeklyTripsField = new TextField();
        weeklyTripsField.setPromptText("Fahrten pro Woche");
        if(existingDocument != null && existingDocument.weeklyFrequency() != null) {
            weeklyTripsField.setText(existingDocument.weeklyFrequency().isPresent() ? existingDocument.weeklyFrequency().get().toString() : "");
        }

        VBox treatmentBox = new VBox(5, treatmentLabel, startDateBox, endDateBox, treatmentFacilityField, weeklyTripsField);

        // Art und Ausstattung der Beförderung
        Text transportTypeLabel = new Text("3. Art und Ausstattung der Beförderung");
        ToggleGroup transportTypeGroup = new ToggleGroup();

        RadioButton taxi = new RadioButton("Taxi/Mietwagen");
        RadioButton ktw = new RadioButton("KTW, da medizinisch-fachliche Betreuung und/oder Einrichtung notwendig ist (Begründung unter 4. erforderlich)");
        RadioButton rtw = new RadioButton("RTW");
        RadioButton naw = new RadioButton("NAW/NEF");
        RadioButton otherTransport = new RadioButton("andere (Begründung unter 4. erforderlich)");

        taxi.setToggleGroup(transportTypeGroup);
        ktw.setToggleGroup(transportTypeGroup);
        rtw.setToggleGroup(transportTypeGroup);
        naw.setToggleGroup(transportTypeGroup);
        otherTransport.setToggleGroup(transportTypeGroup);

        if (existingDocument != null) {
            switch (existingDocument.transportationType()) {
                case Taxi -> transportTypeGroup.selectToggle(taxi);
                case KTW -> transportTypeGroup.selectToggle(ktw);
                case RTW -> transportTypeGroup.selectToggle(rtw);
                case NAWorNEF -> transportTypeGroup.selectToggle(naw);
                case Other -> transportTypeGroup.selectToggle(otherTransport);
            }
        }


        VBox transportTypeBox = new VBox(5, transportTypeLabel, taxi, ktw, rtw, naw, otherTransport);

        // Begründung/Sonstiges
        Text justificationLabel = new Text("4. Begründung/Sonstiges");
        TextArea justificationField = new TextArea();
        justificationField.setPromptText("Weitere Informationen eingeben");
        if (existingDocument != null && existingDocument.additionalInfo() != null) {
            justificationField.setText(String.valueOf(existingDocument.additionalInfo().isPresent() ? existingDocument.additionalInfo().get() : ""));
        }

        Text errorLabel = new Text();
        errorLabel.setFill(Color.RED); // Fehlertext in Rot
        errorLabel.setVisible(false); // Standardmäßig ausgeblendet


        // Submit Button
        Button submitButton = new Button(existingDocument == null ? "Erstellen" : "Speichern");
        // Dynamische Validierung für den Submit-Button
        submitButton.setDisable(true);

        // Listener für dynamische Überprüfung hinzufügen
        reasonGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
                checkFormCompletion(submitButton, reasonGroup, treatmentDatePicker, transportTypeGroup, justificationField,
                        ktw, otherTransport, exceptionalCase, permanentImpairment, otherReason2, weeklyTripsField, endDatePicker, errorLabel, treatmentFacilityField)
        );
        treatmentDatePicker.valueProperty().addListener((observable, oldValue, newValue) ->
                checkFormCompletion(submitButton, reasonGroup, treatmentDatePicker, transportTypeGroup, justificationField,
                        ktw, otherTransport, exceptionalCase, permanentImpairment, otherReason2, weeklyTripsField, endDatePicker, errorLabel, treatmentFacilityField)
        );
        transportTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
                checkFormCompletion(submitButton, reasonGroup, treatmentDatePicker, transportTypeGroup, justificationField,
                        ktw, otherTransport, exceptionalCase, permanentImpairment, otherReason2, weeklyTripsField, endDatePicker, errorLabel, treatmentFacilityField)
        );
        justificationField.textProperty().addListener((observable, oldValue, newValue) ->
                checkFormCompletion(submitButton, reasonGroup, treatmentDatePicker, transportTypeGroup, justificationField,
                        ktw, otherTransport, exceptionalCase, permanentImpairment, otherReason2, weeklyTripsField, endDatePicker, errorLabel, treatmentFacilityField)
        );
        weeklyTripsField.textProperty().addListener((observable, oldValue, newValue) ->
                checkFormCompletion(submitButton, reasonGroup, treatmentDatePicker, transportTypeGroup, justificationField,
                        ktw, otherTransport, exceptionalCase, permanentImpairment, otherReason2, weeklyTripsField, endDatePicker, errorLabel, treatmentFacilityField)
        );
        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) ->
                checkFormCompletion(submitButton, reasonGroup, treatmentDatePicker, transportTypeGroup, justificationField,
                        ktw, otherTransport, exceptionalCase, permanentImpairment, otherReason2, weeklyTripsField, endDatePicker, errorLabel, treatmentFacilityField)
        );

        treatmentFacilityField.textProperty().addListener((observable, oldValue, newValue) ->
                checkFormCompletion(submitButton, reasonGroup, treatmentDatePicker, transportTypeGroup, justificationField,
                        ktw, otherTransport, exceptionalCase, permanentImpairment, otherReason2, weeklyTripsField, endDatePicker, errorLabel, treatmentFacilityField)
        );


        submitButton.setOnAction(e -> {
            try {
                System.out.println("Submit Button clicked");
                // Extrahiere die Werte aus den Eingabefeldern
                COptional<Reference<Patient>> patientOpt = patientField.getText().isEmpty() ? COptional.empty() : COptional.of(new Reference<>(new Id<>(patientField.getText())));
                System.out.println("Patient: " + patientOpt);
                AtomicReference<COptional<Reference<InsuranceData>>> insuranceDataOpt = new AtomicReference<>(COptional.empty());
                System.out.println("Insurance: " + insuranceDataOpt);
                patientInsuranceBox.getChildren().forEach(child -> {
                    if (child instanceof HBox) {
                        HBox hBox = (HBox) child;
                        TextField insuranceStatusField = (TextField) hBox.getChildren().get(3);

                        InsuranceData insuranceData = TransportDocumentController.createInsuranceData(patientOpt, Reference.to(new Id<>(insuranceField.getText())), insuranceStatusField.getText());
                        if (insuranceData != null) {
                            insuranceDataOpt.set(COptional.of(Reference.to(insuranceData.id())));
                        }

                    }
                });

                // Extrahiere den Transportgrund aus dem ToggleGroup
                TransportReason transportReason = switch (reasonGroup.getSelectedToggle()) {
                    case RadioButton b when b == emergencyTransport -> TransportReason.EmergencyTransport;
                    case RadioButton b when b == inpatientTreatment -> TransportReason.FullPartStationary;
                    case RadioButton b when b == prePostTreatment -> TransportReason.PrePostStationary;
                    case RadioButton b when b == outpatientTreatment -> TransportReason.AmbulantTaxi;
                    case RadioButton b when b == otherReason1 -> TransportReason.OtherPermitFree;
                    case RadioButton b when b == frequentTreatment -> TransportReason.HighFrequent;
                    case RadioButton b when b == exceptionalCase -> TransportReason.HighFrequentAlike;
                    case RadioButton b when b == permanentImpairment -> TransportReason.ContinuousImpairment;
                    case RadioButton b when b == otherReason2 -> TransportReason.OtherKTW;
                        default -> throw new IllegalStateException("Unexpected transport reason");
                };

                Date startDate = Date.valueOf(treatmentDatePicker.getValue());
                COptional<Date> endDateOpt = endDatePicker.getValue() != null ?
                        COptional.of(Date.valueOf(endDatePicker.getValue())) : COptional.empty();

                COptional<Integer> weeklyFrequencyOpt = !weeklyTripsField.getText().isEmpty() ?
                        COptional.of(Integer.parseInt(weeklyTripsField.getText())) : COptional.empty();

                Reference<ServiceProvider> healthcareServiceProvider = new Reference<>(new Id<>(treatmentFacilityField.getText()));

                // Extrahiere den Transporttyp aus der ToggleGroup
                TransportationType transportationType = switch (transportTypeGroup.getSelectedToggle()) {
                    case RadioButton b when b == taxi -> TransportationType.Taxi;
                    case RadioButton b when b == ktw -> TransportationType.KTW;
                    case RadioButton b when b == rtw -> TransportationType.RTW;
                    case RadioButton b when b == naw -> TransportationType.NAWorNEF;
                    case RadioButton b when b == otherTransport -> TransportationType.Other;
                        default -> throw new IllegalStateException("Unexpected transportation type");
                };

                COptional<String> additionalInfoOpt = !justificationField.getText().isEmpty() ?
                        COptional.of(justificationField.getText()) : COptional.empty();

                TransportDocument newDocument;

                // Rufe die createTransportDocument-Methode auf
                if(existingDocument != null) {
                    newDocument = TransportDocumentController.updateTransportDocument(
                            existingDocument.id(),
                            patientOpt,
                            insuranceDataOpt.get(),
                            transportReason,
                            startDate,
                            endDateOpt,
                            weeklyFrequencyOpt,
                            healthcareServiceProvider,
                            transportationType,
                            additionalInfoOpt
                    );
                }else {
                    newDocument = TransportDocumentController.createTransportDocument(
                            patientOpt, insuranceDataOpt.get(), transportReason, startDate, endDateOpt,
                            weeklyFrequencyOpt, healthcareServiceProvider, transportationType, additionalInfoOpt
                    );
                }

                // Feedback für den Benutzer
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION,
                        "Das Transportdokument wurde erfolgreich " + (existingDocument == null ?  "erstellt" : "aktualisiert") + " mit ID: " + newDocument.id());
                successAlert.showAndWait();
                transportDocuments = TransportDocumentController.getTransportDocuments(user);
                stage.close();

            } catch (Exception ex) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR,
                        "Fehler beim Erstellen des Transportdokuments: " + ex.getMessage());
                errorAlert.showAndWait();
            }
        });

        // Direktes Validieren nach Initialisierung
        checkFormCompletion(submitButton, reasonGroup, treatmentDatePicker, transportTypeGroup, justificationField,
                ktw, otherTransport, exceptionalCase, permanentImpairment, otherReason2, weeklyTripsField, endDatePicker, errorLabel, treatmentFacilityField);

        root.getChildren().addAll(
                patientBox,
                reasonBox,
                treatmentBox,
                transportTypeBox,
                justificationLabel, justificationField,
                errorLabel,
                submitButton
        );


        Scene scene = new Scene(scrollPane, 1200, 800);
        stage.setMaximized(true);
        stage.setScene(scene);
        return stage;
    }

    private static HBox getHBox(TextField insuranceField, TextField patientField, TransportDocument existingDocument, AtomicReference<InsuranceData> insurance) {
        TextField insuranceStatusField = new TextField();
        insuranceStatusField.setPromptText("Versicherungsstatus eingeben");

        if (existingDocument != null && existingDocument.insuranceData().isPresent()) {
            InsuranceData insuranceData = TransportDocumentController.getInsuranceDataByID(existingDocument.insuranceData().get().id());
            insuranceField.setText(insuranceData.insurance().id().value());
            insuranceStatusField.setText(String.valueOf(insuranceData.insuranceStatus()));
        }

        Button fetchInsuranceDataButton = new Button("Versicherungs-Nummer holen");
        fetchInsuranceDataButton.setOnAction(e -> {
            System.out.println("Versicherungsdaten holen");
            insurance.set(TransportDocumentController.getInsuranceData(patientField.getText()));
            System.out.println("Versicherungsdaten: " + insurance.get());
            insuranceField.setText( insurance.get() == null ? "Keine Versicherungsdaten gefunden" : insurance.get().insurance().id().value());
            insuranceStatusField.setText(insurance.get() == null ? "Kein Versicherungsstatus gefunden" : String.valueOf(insurance.get().insuranceStatus()));
        });




        // Layout für Patient und Insurance Data
        return new HBox(10, patientField, fetchInsuranceDataButton, insuranceField, insuranceStatusField);
    }

    /**
     * Checks if all required fields are filled and enables the submit button accordingly.
     *
     * @param submitButton           The submit button to enable or disable.
     * @param reasonGroup            The toggle group for the transport reason.
     * @param treatmentDatePicker    The date picker for the treatment start date.
     * @param transportTypeGroup     The toggle group for the transport type.
     * @param justificationField     The text area for the justification.
     * @param ktw                    The radio button for KTW transport.
     * @param otherTransport         The radio button for other transport types.
     * @param exceptionalCase        The radio button for exceptional cases.
     * @param permanentImpairment    The radio button for permanent impairments.
     * @param otherReason2           The radio button for other reasons.
     * @param weeklyTripsField       The text field for the weekly trips.
     * @param endDatePicker          The date picker for the treatment end date.
     * @param errorLabel             The label for displaying error messages.
     * @param treatmentFacilityField The text field for the treatment facility.
     */

    private void checkFormCompletion(
            Button submitButton,
            ToggleGroup reasonGroup,
            DatePicker treatmentDatePicker,
            ToggleGroup transportTypeGroup,
            TextArea justificationField,
            RadioButton ktw,
            RadioButton otherTransport,
            RadioButton exceptionalCase,
            RadioButton permanentImpairment,
            RadioButton otherReason2,
            TextField weeklyTripsField,
            DatePicker endDatePicker,
            Text errorLabel,
            TextField treatmentFacilityField) {
        boolean isReasonSelected = reasonGroup.getSelectedToggle() != null;
        boolean isStartDateSelected = treatmentDatePicker.getValue() != null;
        boolean isTransportTypeSelected = transportTypeGroup.getSelectedToggle() != null;

        // Weekly Frequency validation
        boolean isEndDateProvided = endDatePicker.getValue() != null;
        boolean isWeeklyFrequencyEntered = !weeklyTripsField.getText().trim().isEmpty();
        boolean isWeeklyFrequencyValid = !isEndDateProvided || isWeeklyFrequencyEntered;

        //Treatment Facility validation
        boolean isTreatmentFacilityEntered = !treatmentFacilityField.getText().trim().isEmpty();

        // Check if justification is required for selected transport type or reason
        boolean isJustificationRequired = (transportTypeGroup.getSelectedToggle() == ktw || transportTypeGroup.getSelectedToggle() == otherTransport
                || reasonGroup.getSelectedToggle() == exceptionalCase || reasonGroup.getSelectedToggle() == permanentImpairment || reasonGroup.getSelectedToggle() == otherReason2);
        boolean isJustificationProvided = !justificationField.getText().trim().isEmpty();

        // Dynamische Fehlermeldung
        StringBuilder errorMessage = new StringBuilder();

        if (!isReasonSelected) errorMessage.append("Bitte wählen Sie einen Grund der Beförderung aus.\n");
        if (!isStartDateSelected) errorMessage.append("Bitte wählen Sie ein Startdatum aus.\n");
        if (!isTreatmentFacilityEntered) errorMessage.append("Bitte geben Sie ein Behandlungsstättenkürzel ein.\n");
        if (!isTransportTypeSelected) errorMessage.append("Bitte wählen Sie eine Beförderungsart aus.\n");
        if (isEndDateProvided && !isWeeklyFrequencyEntered) errorMessage.append("Bitte geben Sie die Anzahl der Fahrten pro Woche ein, wenn ein Enddatum gesetzt ist.\n");
        if (isWeeklyFrequencyEntered && !isEndDateProvided) errorMessage.append("Bitte setzen Sie ein Enddatum, wenn die Anzahl der Fahrten pro Woche eingetragen ist.\n");
        if (isJustificationRequired && !isJustificationProvided) errorMessage.append("Bitte geben Sie eine Begründung ein.\n");

        errorLabel.setText(errorMessage.toString());
        errorLabel.setVisible(!errorMessage.isEmpty());

        // Visuelle Hervorhebung für Felder
        treatmentDatePicker.setStyle(isStartDateSelected ? "-fx-border-color: none;" : "-fx-border-color: red;");
        endDatePicker.setStyle(!isEndDateProvided && isWeeklyFrequencyEntered ? "-fx-border-color: red;" : "-fx-border-color: none;");
        weeklyTripsField.setStyle(isWeeklyFrequencyValid ? "-fx-border-color: none;" : "-fx-border-color: red;");
        justificationField.setStyle(isJustificationRequired && !isJustificationProvided ? "-fx-border-color: red;" : "-fx-border-color: none;");
        treatmentFacilityField.setStyle(isTreatmentFacilityEntered ? "-fx-border-color: none;" : "-fx-border-color: red;");

        // Visuelle Hervorhebung für RadioButtons
        for (Toggle toggle : reasonGroup.getToggles()) {
            ((RadioButton) toggle).setStyle(isReasonSelected ? "-fx-border-color: none;" : "-fx-border-color: red;");
        }
        for (Toggle toggle : transportTypeGroup.getToggles()) {
            ((RadioButton) toggle).setStyle(isTransportTypeSelected ? "-fx-border-color: none;" : "-fx-border-color: red;");
        }
        if(reasonGroup.getSelectedToggle() == otherReason2){
            transportTypeGroup.selectToggle(ktw);
        }

        submitButton.setDisable(!errorMessage.isEmpty());
    }




    /**
     * Displays a window with options for a transport document.
     * The user can edit the transport Document or view, create, edit or delete transports for the document.
     *
     * @param transportDocument The transport document for which the options should be displayed.
     */

    private void showOptionsWindow(TransportDocument transportDocument, User user) {
        Stage optionsStage = new Stage();
        optionsStage.setTitle("Optionen");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.CENTER);

        Label infoLabel = new Label("Wählen Sie eine Aktion für das Transportdokument mit ID: " + transportDocument.id());

        Button createTransportButton = new Button("Transporte anzeigen " + (user.role() != UserRole.InsuranceUser ? "und bearbeiten" : ""));
        createTransportButton.setOnAction(e -> {
            Stage createTransportStage = new TransportDetailsManagement().start(transportDocument, user);
            createTransportStage.centerOnScreen();
            createTransportStage.show();
            System.out.println("Transport wird erstellt für Dokument: " + transportDocument.id());
            optionsStage.close();
        });
        Button editDocumentButton = new Button("TransportDocument bearbeiten");
        editDocumentButton.setOnAction(e -> {
            // Logik zum Bearbeiten des Dokuments
            Stage editDocumentStage = createTransportDocumentCreationWindow(transportDocument);
            editDocumentStage.show();
            optionsStage.close();

        });
        editDocumentButton.setDisable(user.role() != UserRole.HealthcareUser && user.role() != UserRole.HealthcareDoctor && user.role() != UserRole.SuperUser);

        root.getChildren().addAll(infoLabel, createTransportButton, editDocumentButton);
        if(user.role() == UserRole.InsuranceUser){
            editDocumentButton.setDisable(true);
            editDocumentButton.setOpacity(0);

            Button archiveButton = new Button("Transportdokument archivieren");
            archiveButton.setOnAction(e -> {
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Sind Sie sicher, dass Sie dieses Transportdokument archivieren möchten?", ButtonType.YES, ButtonType.NO);
                confirmationAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        TransportDocumentController.archiveTransportDocument(transportDocument.id());
                        transportDocuments = TransportDocumentController.getTransportDocuments(user);
                        optionsStage.close();
                    }
                });
            });

            root.getChildren().add(archiveButton);
        }





        Scene scene = new Scene(root, 300, 200);
        optionsStage.setScene(scene);
        optionsStage.show();
    }

}
