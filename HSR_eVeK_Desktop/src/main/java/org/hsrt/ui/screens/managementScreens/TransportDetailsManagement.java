package org.hsrt.ui.screens.managementScreens;

import de.ehealth.evek.api.entity.*;
import de.ehealth.evek.api.type.*;
import de.ehealth.evek.api.util.COptional;
import de.ehealth.evek.api.type.ProcessingState;

import javafx.collections.ObservableList;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import org.hsrt.ui.screens.creationScreens.TransportDetailsCreationScreen;
import org.hsrt.ui.controllers.TransportDetailsController;

import java.awt.image.BufferedImage;

import java.time.LocalDate;

import java.util.Optional;

import static org.hsrt.ui.screens.creationScreens.TransportDetailsCreationScreen.getDatePicker;


/**
 * This class represents the window for managing transport details.
 * Here, transports can be displayed, edited, and deleted.
 * New transports can also be created.
 */



public class TransportDetailsManagement {
    private ObservableList<TransportDetails> transports;

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
        Button createButton = createTransportButton(transportDocument, tableView, user);
        if(user.role() == UserRole.InsuranceUser){
            createButton.setDisable(true);
            createButton.setOpacity(0);
        }
        vbox.getChildren().addAll(createButton, tableView);
        return vbox;
    }

    private TableView<TransportDetails> createTransportTable(TransportDocument transportDocument, User user) {
        transports = TransportDetailsController.getTransports(transportDocument, user);
        TableView<TransportDetails> tableView = new TableView<>(transports);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.setRowFactory(tv -> createTableRow(transportDocument, user, tableView));
        addColumnsToTableView(tableView, transportDocument, user);
        return tableView;
    }

    private TableRow<TransportDetails> createTableRow(TransportDocument transportDocument, User user, TableView<TransportDetails> tableview) {
        TableRow<TransportDetails> row = new TableRow<>();
        row.setOnMouseClicked(event -> {
            if (!row.isEmpty() && event.getClickCount() == 1) {
                TransportDetails clickedDetails = row.getItem();
                showOptionsWindow(clickedDetails, transportDocument, user, tableview);
            }
        });
        return row;
    }

    private Button createTransportButton(TransportDocument transportDocument, TableView<TransportDetails> tableView, User user) {
        Button createButton = new Button("Neuen Transport erstellen");
        createButton.setOnAction(e -> {
            Stage creationStage = createTransport(transportDocument, user);
            creationStage.showAndWait();
            transports = TransportDetailsController.getTransports(transportDocument, user);
            tableView.setItems(transports);
        });
        return createButton;
    }

    private Stage createTransport(TransportDocument transportDocument, User user) {
        Stage stage = new Stage();
        stage.setTitle("Neuen Transport erstellen");
        VBox vbox = createTransportForm(transportDocument, user);
        Scene scene = new Scene(vbox, 1000, 600);
        stage.setScene(scene);
        return stage;
    }

    private VBox createTransportForm(TransportDocument transportDocument, User user) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        Label dateLabel = new Label("Datum des Transports:");
        DatePicker datePicker = createDatePicker();
        Button saveButton = createSaveButton(transportDocument, datePicker, user);
        vbox.getChildren().addAll(dateLabel, datePicker, saveButton);
        return vbox;
    }

    private DatePicker createDatePicker() {
        return getDatePicker();
    }

    private Button createSaveButton(TransportDocument transportDocument, DatePicker datePicker, User user) {
        Button saveButton = new Button("Speichern");
        saveButton.setOnAction(event -> {
            LocalDate localDate = datePicker.getValue();
            if (localDate != null) {
                java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
                System.out.println("Saving transport for date: " + sqlDate);

                // Pass the date to the controller's method
                TransportDetailsController.createTransport(transportDocument, sqlDate);

                // Close the stage after saving
                Stage stage = (Stage) saveButton.getScene().getWindow();
                transports = TransportDetailsController.getTransports(transportDocument, user);
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
     *
     * @param tableView         TableView to which the columns should be added
     * @param transportDocument The transport document for which the columns should be added
     */


    private void addColumnsToTableView(TableView<TransportDetails> tableView, TransportDocument transportDocument, User user) {
        TableColumn<TransportDetails, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().id().toString()));

        TableColumn<TransportDetails, String> dateColumn = new TableColumn<>("Datum");
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().transportDate().toString()));

        TableColumn<TransportDetails, String> healthcareProviderColumn = new TableColumn<>("Behandlungsstätte");
        healthcareProviderColumn.setCellValueFactory(data -> {
            COptional<Reference<ServiceProvider>> serviceProvider = COptional.of(transportDocument.healthcareServiceProvider());
            return new SimpleStringProperty(
                    serviceProvider.isPresent() ? serviceProvider.get().toString() : "nicht angegeben"
            );
        });

        TableColumn<TransportDetails, String> transportProviderColumn = new TableColumn<>("TransportDienstleister");
        transportProviderColumn.setCellValueFactory(data -> {
            COptional<Reference<ServiceProvider>> transportProvider = data.getValue().transportProvider();
            return new SimpleStringProperty(
                    transportProvider.isPresent() ? transportProvider.get().toString() : "nicht angegeben"
            );
        });

        TableColumn<TransportDetails, String> patientColumn = new TableColumn<>("Patient");
        patientColumn.setCellValueFactory(data -> {
            COptional<Reference<Patient>> patient = transportDocument.patient();
            return new SimpleStringProperty(
                    patient.isPresent() ? patient.get().toString() : "nicht angegeben"
            );
        });

        TableColumn<TransportDetails, String> processStatusColumn = new TableColumn<>("Bearbeitungsstatus");
        processStatusColumn.setCellValueFactory(data -> {
            ProcessingState processStatus = data.getValue().processingState();
            return new SimpleStringProperty(
                    processStatus != null ? processStatus.toString() : "nicht angegeben"
            );
        });

        //noinspection unchecked
        tableView.getColumns().addAll(idColumn, dateColumn, healthcareProviderColumn, transportProviderColumn, patientColumn, processStatusColumn);
        if(user.role() == UserRole.InsuranceUser){
            TableColumn<TransportDetails, String> invoiceInfo = new TableColumn<>("Abrechnungsdaten");
            invoiceInfo.setCellValueFactory(data -> {
                ServiceProvider invoice = TransportDetailsController.getTransportproviderFromReference(data.getValue().transportProvider());
                return new SimpleStringProperty(
                        invoice.contactInfo().isPresent() ? invoice.contactInfo().toString() : "nicht angegeben"
                );
            });
            tableView.getColumns().add(invoiceInfo);
        }

    }

    /**
    * Displays a window with options for a transport.
    * @param transport The transport for which the options should be displayed
    */

    private void showOptionsWindow(TransportDetails transport, TransportDocument transportDocument, User user, TableView<TransportDetails> tableview) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Optionen für Transport");
        VBox vbox = createOptionsLayout(transport, transportDocument, user, dialogStage, tableview);
        Scene scene = new Scene(vbox, 300, 200);
        dialogStage.setScene(scene);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.showAndWait();
    }

    private VBox createOptionsLayout(TransportDetails transport, TransportDocument transportDocument, User user, Stage dialogStage, TableView<TransportDetails> tableview) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        Label infoLabel = new Label("Optionen für Transport-ID: " + transport.id());
        Button editButton = createEditButton(transport, user, dialogStage, tableview, transportDocument);
        Button showQRButton = createQRButton(transport, dialogStage);
        Button deleteButton = createDeleteButton(transport, dialogStage, tableview, transportDocument, user);

        if(user.role() == UserRole.InsuranceUser){
            deleteButton.setDisable(true);
            deleteButton.setOpacity(0);
        }

        vbox.getChildren().addAll(infoLabel, showQRButton, editButton, deleteButton);
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
        Button copyButton = new Button("ID kopieren");
        Button closeButton = new Button("Schließen");

        // Kopierfunktionalität hinzufügen
        copyButton.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(transport.id().toString());
            clipboard.setContent(content);
        });

        closeButton.setOnAction(e -> stage.close());

        // Elemente zur Oberfläche hinzufügen
        vbox.getChildren().addAll(infoLabel, qrCodeView, copyButton, closeButton);

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

    private Button createEditButton(TransportDetails transport, User user, Stage dialogStage, TableView<TransportDetails> tableview, TransportDocument transportDocument) {
        Button editButton = new Button(user.role() != UserRole.InsuranceUser ? "Bearbeiten" : "Anzeigen");
        editButton.setOnAction(e -> {
            Stage editStage = new TransportDetailsCreationScreen().createTransportDetailsCreationWindow(transport, user);
            editStage.showAndWait();
            transports = TransportDetailsController.getTransports(transportDocument, user);
            tableview.setItems(transports);
            dialogStage.close();
        });
        editButton.setDisable(user.role() == UserRole.HealthcareUser || user.role() == UserRole.HealthcareDoctor);
        return editButton;
    }

    private Button createDeleteButton(TransportDetails transport, Stage dialogStage, TableView<TransportDetails> tableView, TransportDocument transportDocument, User user) {
        Button deleteButton = new Button("Löschen");
        deleteButton.setOnAction(e -> {
            handleDeleteTransport(transport, tableView, transportDocument, user);
            dialogStage.close();
        });
        return deleteButton;
    }







    /**
     * Handles the deletion of a transport.
     * @param transport The transport to delete
     */

    private void handleDeleteTransport(TransportDetails transport, TableView<TransportDetails> tableView, TransportDocument transportDocument, User user) {
        // Dialog für die Bestätigung
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Bestätigung erforderlich");
        confirmationAlert.setHeaderText("Transport löschen");
        confirmationAlert.setContentText("Sind Sie sicher, dass Sie den Transport löschen möchten?");

        // Benutzeraktion abfragen
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Transport löschen, wenn bestätigt
            TransportDetailsController.deleteTransport(transport);
            transports = TransportDetailsController.getTransports(transportDocument, user);
            tableView.setItems(transports);
        } else {
            // Abbrechen, nichts tun
            System.out.println("Löschvorgang abgebrochen");
        }
    }
}
