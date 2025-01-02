package org.hsrt.ui.screens.managementScreens;

import de.ehealth.evek.api.entity.TransportDocument;
import de.ehealth.evek.api.type.Id;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Date;

public class TransportDocumentManagement {

    public Stage createTransportDocumentManagement() {
        Stage stage = new Stage();
        stage.setTitle("Transport Document Management");

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));

        ObservableList<TransportDocument> transportDocuments = FXCollections.observableArrayList(
                new TransportDocument(new Id<TransportDocument>("1"), null, null, null, Date.valueOf("2023-01-01"), null, null, null, null, null, null, false),
                new TransportDocument(new Id<TransportDocument>("2"), null, null, null, Date.valueOf("2023-02-01"), null, null, null, null, null, null, false),
                new TransportDocument(new Id<TransportDocument>("3"), null, null, null, Date.valueOf("2023-03-01"), null, null, null, null, null, null, false)
        );

        TableView<TransportDocument> tableView = new TableView<>(transportDocuments);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Spalten hinzufügen
        addColumnsToTableView(tableView);

        Button createButton = new Button("Create Transport Document");
        createButton.setOnAction(e -> {
            Stage createTransportDocumentStage = createTransportDocumentCreationWindow();
            createTransportDocumentStage.show();
        });

        vbox.getChildren().addAll(createButton, tableView);
        Scene scene = new Scene(vbox, 1000, 600);
        stage.setScene(scene);

        return stage;
    }

    private void addColumnsToTableView(TableView<TransportDocument> tableView) {

        TableColumn<TransportDocument, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<TransportDocument, String> patientColumn = new TableColumn<>("Patient");
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("patient"));

        TableColumn<TransportDocument, String> insuranceDataColumn = new TableColumn<>("Insurance Data");
        insuranceDataColumn.setCellValueFactory(new PropertyValueFactory<>("insuranceData"));

        TableColumn<TransportDocument, String> transportReasonColumn = new TableColumn<>("Transport Reason");
        transportReasonColumn.setCellValueFactory(new PropertyValueFactory<>("transportReason"));

        TableColumn<TransportDocument, Date> startDateColumn = new TableColumn<>("Start Date");
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<TransportDocument, Date> endDateColumn = new TableColumn<>("End Date");
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        TableColumn<TransportDocument, Integer> weeklyFrequencyColumn = new TableColumn<>("Weekly Frequency");
        weeklyFrequencyColumn.setCellValueFactory(new PropertyValueFactory<>("weeklyFrequency"));

        TableColumn<TransportDocument, String> serviceProviderColumn = new TableColumn<>("Service Provider");
        serviceProviderColumn.setCellValueFactory(new PropertyValueFactory<>("healthcareServiceProvider"));

        TableColumn<TransportDocument, String> transportationTypeColumn = new TableColumn<>("Transportation Type");
        transportationTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transportationType"));

        TableColumn<TransportDocument, String> additionalInfoColumn = new TableColumn<>("Additional Info");
        additionalInfoColumn.setCellValueFactory(new PropertyValueFactory<>("additionalInfo"));

        TableColumn<TransportDocument, String> signatureColumn = new TableColumn<>("Signature");
        signatureColumn.setCellValueFactory(new PropertyValueFactory<>("signature"));

        TableColumn<TransportDocument, Boolean> isArchivedColumn = new TableColumn<>("Is Archived");
        isArchivedColumn.setCellValueFactory(new PropertyValueFactory<>("isArchived"));

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

    private Stage createTransportDocumentCreationWindow() {
        Stage stage = new Stage();
        stage.setTitle("Create Transport Document");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        // Patientendaten
        Text patientLabel = new Text("Patientendaten");
        TextField patientField = new TextField();
        patientField.setPromptText("Eingabe Patientendaten");

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
        RadioButton otherReason2 = new RadioButton("f) anderer Grund für fahrt mit KTW (z.B. fachgerechtes Lagern, Tragen, Heben erforderlich, Begründung unter 3. und ggf 4. erforderlich)");

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

        // Behandlungstage/Behandlungsfrequenz
        Text treatmentLabel = new Text("2. Behandlungstage/Behandlungsfrequenz und nächsterreichbare, geeignete Behandlungsstätte");
        HBox startDateBox = new HBox(10);
        Text startDateLabel = new Text("Startdatum auswählen:");
        DatePicker treatmentDatePicker = new DatePicker();
        startDateBox.getChildren().addAll(startDateLabel, treatmentDatePicker);

        HBox endDateBox = new HBox(10);
        Text endDateLabel = new Text("Enddatum auswählen: ");
        DatePicker endDatePicker = new DatePicker();
        endDateBox.getChildren().addAll(endDateLabel, endDatePicker);

        TextField treatmentFacilityField = new TextField();
        treatmentFacilityField.setPromptText("Behandlungsstättenkürzel eingeben");

        TextField weeklyTripsField = new TextField();
        weeklyTripsField.setPromptText("Fahrten pro Woche");

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

        VBox transportTypeBox = new VBox(5, transportTypeLabel, taxi, ktw, rtw, naw, otherTransport);

        // Begründung/Sonstiges
        Text justificationLabel = new Text("4. Begründung/Sonstiges");
        TextArea justificationField = new TextArea();
        justificationField.setPromptText("Weitere Informationen eingeben");

        // Submit Button
        Button submitButton = new Button("Speichern");
        submitButton.setDisable(true);

        // Enable button only if required fields are filled
        reasonGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> checkFormCompletion(submitButton, reasonGroup, treatmentDatePicker, transportTypeGroup, justificationField, ktw, otherTransport, exceptionalCase, permanentImpairment, otherReason2));
        treatmentDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> checkFormCompletion(submitButton, reasonGroup, treatmentDatePicker, transportTypeGroup, justificationField, ktw, otherTransport, exceptionalCase, permanentImpairment, otherReason2));
        transportTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> checkFormCompletion(submitButton, reasonGroup, treatmentDatePicker, transportTypeGroup, justificationField, ktw, otherTransport, exceptionalCase, permanentImpairment, otherReason2));
        justificationField.textProperty().addListener((observable, oldValue, newValue) -> checkFormCompletion(submitButton, reasonGroup, treatmentDatePicker, transportTypeGroup, justificationField, ktw, otherTransport, exceptionalCase, permanentImpairment, otherReason2));

        submitButton.setOnAction(e -> {
            // TODO Logik zum Speichern des Dokuments implementieren

            stage.close();
        });

        root.getChildren().addAll(
                patientLabel, patientField,
                reasonBox,
                treatmentBox,
                transportTypeBox,
                justificationLabel, justificationField,
                submitButton
        );

        Scene scene = new Scene(root, 1200, 800);
        stage.setMaximized(true);
        stage.setScene(scene);
        return stage;
    }
    private void checkFormCompletion(Button submitButton, ToggleGroup reasonGroup, DatePicker treatmentDatePicker, ToggleGroup transportTypeGroup, TextArea justificationField, RadioButton ktw, RadioButton otherTransport, RadioButton exceptionalCase, RadioButton permanentImpairment, RadioButton otherReason2) {
        boolean isReasonSelected = reasonGroup.getSelectedToggle() != null;
        boolean isStartDateSelected = treatmentDatePicker.getValue() != null;
        boolean isTransportTypeSelected = transportTypeGroup.getSelectedToggle() != null;

        // Check if justification is required for selected transport type or reason
        boolean isJustificationRequired = (transportTypeGroup.getSelectedToggle() == ktw || transportTypeGroup.getSelectedToggle() == otherTransport
                || reasonGroup.getSelectedToggle() == exceptionalCase || reasonGroup.getSelectedToggle() == permanentImpairment || reasonGroup.getSelectedToggle() == otherReason2);
        boolean isJustificationProvided = !justificationField.getText().trim().isEmpty();

        if (isJustificationRequired) {
            submitButton.setDisable(!(isReasonSelected && isStartDateSelected && isTransportTypeSelected && isJustificationProvided));
        } else {
            submitButton.setDisable(!(isReasonSelected && isStartDateSelected && isTransportTypeSelected));
        }
    }
}
