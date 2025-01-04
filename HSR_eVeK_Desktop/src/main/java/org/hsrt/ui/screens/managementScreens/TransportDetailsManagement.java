package org.hsrt.ui.screens.managementScreens;

import de.ehealth.evek.api.entity.*;
import de.ehealth.evek.api.type.Direction;
import de.ehealth.evek.api.type.PatientCondition;
import de.ehealth.evek.api.type.Reference;
import de.ehealth.evek.api.type.UserRole;
import de.ehealth.evek.api.util.COptional;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.hsrt.ui.controllers.TransportDetailsController;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;



import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;


/**
 * This class represents the window for managing transport details.
 * Here, transports can be displayed, edited, and deleted.
 * New transports can also be created.
 */



public class TransportDetailsManagement {
    public Stage start(TransportDocument transportDocument, User user) {
        Stage stage = new Stage();
        VBox vbox = createMainLayout(transportDocument, user);
        Scene scene = new Scene(vbox, 1000, 600);
        stage.setScene(scene);
        stage.setTitle("Transportverwaltung");
        return stage;
    }

    private VBox createMainLayout(TransportDocument transportDocument, User user) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        TableView<TransportDetails> tableView = createTransportTable(transportDocument, user);
        Button createButton = createTransportButton(transportDocument, user, tableView);
        vbox.getChildren().addAll(createButton, tableView);
        return vbox;
    }

    private TableView<TransportDetails> createTransportTable(TransportDocument transportDocument, User user) {
        TableView<TransportDetails> tableView = new TableView<>(TransportDetailsController.getTransports(transportDocument));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setRowFactory(tv -> createTableRow(transportDocument, user));
        addColumnsToTableView(tableView);
        return tableView;
    }

    private TableRow<TransportDetails> createTableRow(TransportDocument transportDocument, User user) {
        TableRow<TransportDetails> row = new TableRow<>();
        row.setOnMouseClicked(event -> {
            if (!row.isEmpty() && event.getClickCount() == 1) {
                TransportDetails clickedDetails = row.getItem();
                showOptionsWindow(clickedDetails, transportDocument, user);
            }
        });
        return row;
    }

    private Button createTransportButton(TransportDocument transportDocument, User user, TableView<TransportDetails> tableView) {
        Button createButton = new Button("Neuen Transport erstellen");
        createButton.setOnAction(e -> {
            Stage creationStage = createTransport(transportDocument, user);
            creationStage.showAndWait();
            tableView.setItems(TransportDetailsController.getTransports(transportDocument));
        });
        return createButton;
    }

    private Stage createTransport(TransportDocument transportDocument, User user) {
        Stage stage = new Stage();
        stage.setTitle("Neuen Transport erstellen");
        VBox vbox = createTransportForm(transportDocument);
        Scene scene = new Scene(vbox, 1000, 600);
        stage.setScene(scene);
        return stage;
    }

    private VBox createTransportForm(TransportDocument transportDocument) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        Label dateLabel = new Label("Datum des Transports:");
        DatePicker datePicker = createDatePicker();
        Button saveButton = createSaveButton(transportDocument, datePicker);
        vbox.getChildren().addAll(dateLabel, datePicker, saveButton);
        return vbox;
    }

    private DatePicker createDatePicker() {
        DatePicker datePicker = new DatePicker();
        datePicker.setOnAction(event -> {
            LocalDate localDate = datePicker.getValue();
            if (localDate != null) {
                java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
                System.out.println("Transportdatum: " + sqlDate);
            } else {
                System.out.println("Kein Datum ausgewählt.");
            }
        });
        return datePicker;
    }

    private Button createSaveButton(TransportDocument transportDocument, DatePicker datePicker) {
        Button saveButton = new Button("Speichern");
        saveButton.setOnAction(event -> {
            LocalDate localDate = datePicker.getValue();
            if (localDate != null) {
                java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
                System.out.println("Saving transport for date: " + sqlDate);

                // Pass the date to the controller's method
                TransportDetails newTransport = TransportDetailsController.createTransport(transportDocument, sqlDate);

                // Close the stage after saving
                Stage stage = (Stage) saveButton.getScene().getWindow();
                stage.close();
            } else {
                System.out.println("No date selected. Transport not saved.");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warnung");
                alert.setHeaderText("Kein Datum ausgewählt");
                alert.setContentText("Bitte wählen Sie ein Datum aus, bevor Sie speichern.");
                alert.showAndWait();
            }
        });
        return saveButton;
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
        startAddressColumn.setCellValueFactory(data -> {
            COptional<Reference<Address>> startAddress = data.getValue().startAddress();
            return new SimpleStringProperty(
                    startAddress.isPresent() ? startAddress.get().toString() : "nicht angegeben"
            );
        });

        TableColumn<TransportDetails, String> endAddressColumn = new TableColumn<>("Zieladresse");
        endAddressColumn.setCellValueFactory(data -> {
            COptional<Reference<Address>> endAddress = data.getValue().endAddress();
            return new SimpleStringProperty(
                    endAddress.isPresent() ? endAddress.get().toString() : "nicht angegeben"
            );
        });

        tableView.getColumns().addAll(idColumn, dateColumn, startAddressColumn, endAddressColumn);
    }

    /**
    * Displays a window with options for a transport.
    * @param transport The transport for which the options should be displayed
    */

    private void showOptionsWindow(TransportDetails transport, TransportDocument transportDocument, User user) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Optionen für Transport");
        VBox vbox = createOptionsLayout(transport, transportDocument, user, dialogStage);
        Scene scene = new Scene(vbox, 300, 200);
        dialogStage.setScene(scene);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.showAndWait();
    }

    private VBox createOptionsLayout(TransportDetails transport, TransportDocument transportDocument, User user, Stage dialogStage) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        Label infoLabel = new Label("Optionen für Transport-ID: " + transport.id());
        Button editButton = createEditButton(transport, transportDocument, user, dialogStage);
        Button showQRButton = createQRButton(transport, dialogStage);
        Button deleteButton = createDeleteButton(transport, dialogStage);
        Button closeButton = createCloseButton(dialogStage);
        vbox.getChildren().addAll(infoLabel, showQRButton, editButton, deleteButton, closeButton);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    private Button createQRButton(TransportDetails transport, Stage dialogStage) {
        Button qrButton = new Button("QR-Code anzeigen");
        qrButton.setOnAction(e -> {
            showQRCode(transport);
            dialogStage.close();
        });
        return qrButton;
    }

    public void showQRCode(TransportDetails transport) {
        Stage stage = new Stage();
        stage.setTitle("QR-Code für Transport-ID: " + transport.id());

        // QR-Code generieren
        WritableImage qrCodeImage = (WritableImage) generateQRCodeImage(transport.id().toString());

        // UI-Elemente erstellen
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        Label infoLabel = new Label("QR-Code für Transport-ID: " + transport.id());
        ImageView qrCodeView = new ImageView(qrCodeImage);
        Button closeButton = new Button("Schließen");
        closeButton.setOnAction(e -> stage.close());

        // Elemente zur Oberfläche hinzufügen
        vbox.getChildren().addAll(infoLabel, qrCodeView, closeButton);

        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.show();
    }

    // QR-Code generieren und als WritableImage zurückgeben
    private Image generateQRCodeImage(String text) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 200;
        int height = 200;

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            // Konvertierung von BufferedImage zu Image (JavaFX)
            return SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Button createEditButton(TransportDetails transport, TransportDocument transportDocument, User user, Stage dialogStage) {
        Button editButton = new Button("Bearbeiten");
        editButton.setOnAction(e -> {
            Stage editStage = createTransportDetailsCreationWindow(transport, transportDocument, user);
            editStage.showAndWait();
            dialogStage.close();
        });
        editButton.setDisable(user.role() == UserRole.HealthcareUser || user.role() == UserRole.HealthcareDoctor);
        return editButton;
    }

    private Button createDeleteButton(TransportDetails transport, Stage dialogStage) {
        Button deleteButton = new Button("Löschen");
        deleteButton.setOnAction(e -> {
            handleDeleteTransport(transport);
            dialogStage.close();
        });
        return deleteButton;
    }

    private Button createCloseButton(Stage dialogStage) {
        Button closeButton = new Button("Schließen");
        closeButton.setOnAction(e -> dialogStage.close());
        return closeButton;
    }

    /**
     * Handles the creation of a new transport.
     */

    private Stage createTransportDetailsCreationWindow(TransportDetails existingTransport, TransportDocument transportDocument, User user) {
        Stage stage = new Stage();
        stage.setTitle(existingTransport == null ? "Create Transport" : "Edit Transport");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        // ID Label
        Text idLabel = new Text(existingTransport == null ? "ID wird vergeben" : "ID: " + existingTransport.id());

        // Date Label und Picker
        Label dateLabel = new Label("Datum des Transports:");
        DatePicker datePicker = createDatePicker();
        datePicker.setValue(existingTransport == null ? LocalDate.now() : existingTransport.transportDate().toLocalDate());


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
// Einzelne Werte der Enum Direction hinzufügen
        directionComboBox.getItems().addAll(
                Arrays.stream(Direction.values())
                        .map(Direction::toString)
                        .toList()
        );
        directionComboBox.getItems().add("nicht angegeben");
        directionComboBox.setValue(existingTransport == null ? "nicht angegeben" : existingTransport.direction().get().toString());

// Patient Condition ComboBox
        Label patientConditionLabel = new Label("Patientenzustand:");
        ComboBox<String> patientConditionComboBox = new ComboBox<>();
// Einzelne Werte der Enum PatientCondition hinzufügen
        patientConditionComboBox.getItems().addAll(
                Arrays.stream(PatientCondition.values())
                        .map(PatientCondition::toString)
                        .toList()
        );
        patientConditionComboBox.getItems().add("nicht angegeben");
        patientConditionComboBox.setValue(existingTransport == null ? "nicht angegeben" : existingTransport.patientCondition().get().toString());

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
        COptional<Date> patientSignatureDate = existingTransport == null ? null : existingTransport.patientSignatureDate();
        AtomicReference<COptional<Date>> patientSignatureDateRef = new AtomicReference<>(patientSignatureDate);
        confirmPatientSignatureButton.setOnAction(event -> {
            patientSignatureDateRef.set(COptional.of(new Date(System.currentTimeMillis())));
            System.out.println("Updated patientSignatureDate: " + patientSignatureDateRef.get());
        });

        // Transporter Signature
        Label transporterSignatureLabel = new Label("Transporteurunterschrift:");
        TextField transporterSignatureField = new TextField();
        transporterSignatureField.setText(existingTransport == null ? "" : String.valueOf(existingTransport.transporterSignature()));
        Button confirmTransporterSignatureButton = new Button("Bestätigen");
        COptional<Date> transporterSignatureDate = existingTransport == null ? null : existingTransport.transporterSignatureDate();
        AtomicReference<COptional<Date>> transporterSignatureDateRef = new AtomicReference<>(transporterSignatureDate);
        confirmTransporterSignatureButton.setOnAction(event -> {
            transporterSignatureDateRef.set(COptional.of(new Date(System.currentTimeMillis())));
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



    private GridPane createAddressFields(String addressType, Address address) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Address Fields
        Label streetLabel = new Label(addressType + " Straße:");
        TextField streetField = new TextField();
        streetField.setText(address != null ? address.streetName() : ""); // Set value if address is provided
        gridPane.add(streetLabel, 0, 0);
        gridPane.add(streetField, 1, 0);

        Label houseNumberLabel = new Label(addressType + " Hausnummer:");
        TextField houseNumberField = new TextField();
        houseNumberField.setText(address != null ? address.houseNumber() : "");
        gridPane.add(houseNumberLabel, 0, 1);
        gridPane.add(houseNumberField, 1, 1);

        Label postCodeLabel = new Label(addressType + " Postleitzahl:");
        TextField postCodeField = new TextField();
        postCodeField.setText(address != null ? address.postCode() : "");
        gridPane.add(postCodeLabel, 0, 2);
        gridPane.add(postCodeField, 1, 2);

        Label cityLabel = new Label(addressType + " Stadt:");
        TextField cityField = new TextField();
        cityField.setText(address != null ? address.city() : "");
        gridPane.add(cityLabel, 0, 3);
        gridPane.add(cityField, 1, 3);

        Label countryLabel = new Label(addressType + " Land:");
        TextField countryField = new TextField();
        countryField.setText(address != null ? address.country() : "");
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
