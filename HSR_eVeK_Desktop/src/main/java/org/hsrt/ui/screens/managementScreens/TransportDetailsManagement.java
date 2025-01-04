package org.hsrt.ui.screens.managementScreens;

import de.ehealth.evek.api.entity.Address;
import de.ehealth.evek.api.entity.ServiceProvider;
import de.ehealth.evek.api.type.Direction;
import de.ehealth.evek.api.type.PatientCondition;
import de.ehealth.evek.api.type.Reference;
import de.ehealth.evek.api.util.COptional;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.hsrt.ui.controllers.TransportDetailsController;

import de.ehealth.evek.api.entity.TransportDetails;
import de.ehealth.evek.api.entity.TransportDocument;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Date;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;


/**
 * This class represents the window for managing transport details.
 * Here, transports can be displayed, edited, and deleted.
 * New transports can also be created.
 */


public class TransportDetailsManagement {
    public Stage start(TransportDocument transportDocument) {
        Stage stage = new Stage();

        // Layout und Hauptcontainer
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));


        // Tabelle für Transporte
        TableView<TransportDetails> tableView = new TableView<>(TransportDetailsController.getTransports(transportDocument));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Klick-Handler für Zeilen
        tableView.setRowFactory(tv -> {
            TableRow<TransportDetails> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    TransportDetails clickedDetails = row.getItem();
                    showOptionsWindow(clickedDetails, transportDocument);
                }
            });
            return row;
        });

        // Spalten hinzufügen
        addColumnsToTableView(tableView);

        // Erstellungsbutton
        Button createButton = new Button("Neuen Transport erstellen");
        createButton.setOnAction(e -> {
            Stage creationStage = createTransportDetailsCreationWindow(null, transportDocument);
            creationStage.showAndWait();
            tableView.setItems(TransportDetailsController.getTransports(transportDocument));
        });

        // Elemente zum Layout hinzufügen
        vbox.getChildren().addAll(createButton, tableView);

        // Szene und Stage
        Scene scene = new Scene(vbox, 1000, 600);
        stage.setScene(scene);
        stage.setTitle("Transportverwaltung");

        return stage;
    }


    /**
     * Adds columns to the TableView.
     * @param tableView TableView to which the columns should be added
     */


    private void addColumnsToTableView(TableView<TransportDetails> tableView) {
        TableColumn<TransportDetails, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().id().toString()));

        TableColumn<TransportDetails, String> dateColumn = new TableColumn<>("Datum");
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().transportDate().toString()));

        TableColumn<TransportDetails, String> startAddressColumn = new TableColumn<>("Startadresse");
        startAddressColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().startAddress().orElse(null).toString()));

        TableColumn<TransportDetails, String> endAddressColumn = new TableColumn<>("Zieladresse");
        endAddressColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().endAddress().orElse(null).toString()));

        tableView.getColumns().addAll(idColumn, dateColumn, startAddressColumn, endAddressColumn);
    }

    /**
    * Displays a window with options for a transport.
    * @param transport The transport for which the options should be displayed
    */

    private void showOptionsWindow(TransportDetails transport, TransportDocument transportDocument) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Optionen für Transport");

        // Layout für die Optionen
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label infoLabel = new Label("Optionen für Transport-ID: " + transport.id());

        Button editButton = new Button("Bearbeiten");
        editButton.setOnAction(e -> {
            createTransportDetailsCreationWindow(transport, transportDocument);
            dialogStage.close();
        });

        Button deleteButton = new Button("Löschen");
        deleteButton.setOnAction(e -> {
            handleDeleteTransport(transport);
            dialogStage.close();
        });

        Button closeButton = new Button("Schließen");
        closeButton.setOnAction(e -> dialogStage.close());

        vbox.getChildren().addAll(infoLabel, editButton, deleteButton, closeButton);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 300, 200);
        dialogStage.setScene(scene);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.showAndWait();
    }

    /**
     * Handles the creation of a new transport.
     */

    private Stage createTransportDetailsCreationWindow(TransportDetails existingTransport, TransportDocument transportDocument) {
        Stage stage = new Stage();
        stage.setTitle(existingTransport == null ? "Create Transport" : "Edit Transport");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        // ID Label
        Text idLabel = new Text(existingTransport == null ? "ID wird vergeben" : "ID: " + existingTransport.id());

        // Date Label und Picker
        Label dateLabel = new Label("Datum des Transports:");
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(existingTransport == null ? null : existingTransport.transportDate().toLocalDate());

        // Start Address Pane
        Reference<Address> startAddressReference = existingTransport == null ? null : existingTransport.startAddress().orElse(null);
        Address startAddress = TransportDetailsController.getAddressFromReference(startAddressReference);
        TitledPane startAddressPane = new TitledPane("Startadresse", createAddressFields("Start", startAddress));

        // End Address Pane
        Reference<Address> endAddressReference = existingTransport == null ? null : existingTransport.endAddress().orElse(null);
        Address endAddress = TransportDetailsController.getAddressFromReference(endAddressReference);
        TitledPane endAddressPane = new TitledPane("Zieladresse", createAddressFields("Ziel", endAddress));

        // Direction ComboBox
        Label directionLabel = new Label("Richtung:");
        ComboBox<String> directionComboBox = new ComboBox<>();
        directionComboBox.getItems().addAll(Arrays.toString(Direction.values()), "nicht angegeben");
        directionComboBox.setValue(existingTransport == null ? "nicht angegeben" : existingTransport.direction().toString());

        // Patient Condition ComboBox
        Label patientConditionLabel = new Label("Patientenzustand:");
        ComboBox<String> patientConditionComboBox = new ComboBox<>();
        patientConditionComboBox.getItems().addAll(Arrays.toString(PatientCondition.values()), "nicht angegeben");
        patientConditionComboBox.setValue(existingTransport == null ? "nicht angegeben" : existingTransport.patientCondition().toString());

        // Transport Provider ComboBox
        Label providerLabel = new Label("Transportanbieter:");
        ComboBox<String> transportProviderComboBox = new ComboBox<>();
        transportProviderComboBox.getItems().addAll(TransportDetailsController.getTransportproviders());
        ServiceProvider transportProvider = existingTransport == null ? null : TransportDetailsController.getTransportproviderFromReference(existingTransport.transportProvider());
        transportProviderComboBox.setValue(existingTransport == null ? "nicht angegeben" : transportProvider.name());

        // Tour Number Label
        String tourNumber = existingTransport == null ? TransportDetailsController.getTransportTour(transportDocument.id()) : String.valueOf(existingTransport.tourNumber());
        Label tourNumberLabel = new Label("Tournummer: " + tourNumber);

        // Payment Exemption ComboBox
        Label paymentExemptionLabel = new Label("Zahlungsbefreiung:");
        ComboBox<Boolean> paymentExemptionComboBox = new ComboBox<>();
        paymentExemptionComboBox.getItems().addAll(true, false);
        paymentExemptionComboBox.setValue(existingTransport == null ? false : Boolean.parseBoolean(String.valueOf(existingTransport.paymentExemption())));

        // Patient Signature
        Label patientSignatureLabel = new Label("Patientenunterschrift:");
        TextField patientSignatureField = new TextField();
        patientSignatureField.setText(existingTransport == null ? "" : String.valueOf(existingTransport.patientSignature()));
        Button confirmPatientSignatureButton = new Button("Bestätigen");
        Date patientSignatureDate = existingTransport == null ? null : Date.valueOf(String.valueOf(existingTransport.patientSignatureDate()));
        AtomicReference<Date> patientSignatureDateRef = new AtomicReference<>(patientSignatureDate);
        confirmPatientSignatureButton.setOnAction(event -> {
            patientSignatureDateRef.set(new Date(System.currentTimeMillis()));
            System.out.println("Updated patientSignatureDate: " + patientSignatureDateRef.get());
        });

        // Transporter Signature
        Label transporterSignatureLabel = new Label("Transporteurunterschrift:");
        TextField transporterSignatureField = new TextField();
        transporterSignatureField.setText(existingTransport == null ? "" : String.valueOf(existingTransport.transporterSignature()));
        Button confirmTransporterSignatureButton = new Button("Bestätigen");
        Date transporterSignatureDate = existingTransport == null ? null : Date.valueOf(String.valueOf(existingTransport.transporterSignatureDate()));
        AtomicReference<Date> transporterSignatureDateRef = new AtomicReference<>(transporterSignatureDate);
        confirmTransporterSignatureButton.setOnAction(event -> {
            transporterSignatureDateRef.set(new Date(System.currentTimeMillis()));
            System.out.println("Updated transporterSignatureDate: " + transporterSignatureDateRef.get());
        });

        // Save Button
        Button saveButton = new Button("Speichern");
        saveButton.setOnAction(event -> {
            // Logik zur Speicherung hinzufügen
            stage.close();
        });

        // Layout für den Scroll-Inhalt
        VBox content = new VBox(15, idLabel, dateLabel, datePicker, startAddressPane, endAddressPane,
                directionLabel, directionComboBox, patientConditionLabel, patientConditionComboBox,
                providerLabel, transportProviderComboBox, tourNumberLabel, paymentExemptionLabel, paymentExemptionComboBox,
                patientSignatureLabel, patientSignatureField, confirmPatientSignatureButton,
                transporterSignatureLabel, transporterSignatureField, confirmTransporterSignatureButton);

        // ScrollPane
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);

        // Gesamtes Layout
        VBox layout = new VBox(10, scrollPane, saveButton);
        layout.setPadding(new Insets(10));
        Scene scene = new Scene(layout, 600, 600);
        stage.setScene(scene);

        return stage;

        //TODO: fix this mess
    }



    private GridPane createAddressFields(String addressType, Address Address) {
        //TODO: set the text fields to the values of the address if it is not null
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Adresse Felder
        Label streetLabel = new Label(addressType + " Straße:");
        TextField streetField = new TextField();
        gridPane.add(streetLabel, 0, 0);
        gridPane.add(streetField, 1, 0);

        Label houseNumberLabel = new Label(addressType + " Hausnummer:");
        TextField houseNumberField = new TextField();
        gridPane.add(houseNumberLabel, 0, 1);
        gridPane.add(houseNumberField, 1, 1);

        Label postCodeLabel = new Label(addressType + " Postleitzahl:");
        TextField postCodeField = new TextField();
        gridPane.add(postCodeLabel, 0, 2);
        gridPane.add(postCodeField, 1, 2);

        Label cityLabel = new Label(addressType + " Stadt:");
        TextField cityField = new TextField();
        gridPane.add(cityLabel, 0, 3);
        gridPane.add(cityField, 1, 3);

        Label countryLabel = new Label(addressType + " Land:");
        TextField countryField = new TextField();
        gridPane.add(countryLabel, 0, 4);
        gridPane.add(countryField, 1, 4);

        return gridPane;
    }

    private void handleEditTransport(TransportDetails transport) {
        System.out.println("Bearbeite Transport: " + transport);
    }

    /**
     * Handles the deletion of a transport.
     * @param transport The transport to delete
     */

    private void handleDeleteTransport(TransportDetails transport) {
        System.out.println("Lösche Transport: " + transport);
    }
}
