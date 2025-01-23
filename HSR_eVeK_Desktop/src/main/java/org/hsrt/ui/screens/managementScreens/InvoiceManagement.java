package org.hsrt.ui.screens.managementScreens;

import de.ehealth.evek.api.entity.*;
import de.ehealth.evek.api.type.ProcessingState;
import de.ehealth.evek.api.type.Reference;
import de.ehealth.evek.api.util.COptional;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.hsrt.ui.controllers.TransportInvoiceController;
import org.hsrt.ui.screens.creationScreens.TransportDetailsCreationScreen;

public class InvoiceManagement {

    ObservableList<TransportDetails> pendingInsuranceTransports;
    ObservableList<TransportDetails> sentInsuranceTransports;
    ObservableList<Insurance> insurances;


    public Stage createInvoiceManagement(User user) {
        Stage stage = new Stage();
        stage.setTitle("Rechnungsverwaltung");


        // Lade gefilterte Daten

        pendingInsuranceTransports = TransportInvoiceController.getPendingInvoiceTransports(user);
        sentInsuranceTransports = TransportInvoiceController.getSentInvoiceTransports(user);
        insurances = TransportInvoiceController.getInsurances(user);



        // Hauptlayout erstellen
        VBox vbox = createMainLayout(user);
        Scene scene = new Scene(vbox, 1000, 600);
        stage.setScene(scene);
        stage.setTitle("Transportverwaltung");
        return stage;
    }

    private VBox createMainLayout(User user) {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(20));
        vbox.setSpacing(20);
        vbox.setAlignment(Pos.CENTER);

        // Button für "Noch zu senden"
        Button pendingInsuranceButton = new Button("Zu senden");
        pendingInsuranceButton.setOnAction(event -> showTransportTable(pendingInsuranceTransports, "Transporte, die der Versicherung geschickt werden müssen", user));

        // Button für "Bereits gesendet"
        Button sentInsuranceButton = new Button("Bereits gesendet");
        sentInsuranceButton.setOnAction(event -> showTransportTable(sentInsuranceTransports, "Transporte, die bereits der Versicherung geschickt wurden", user));

        // Buttons zum Layout hinzufügen
        vbox.getChildren().addAll(pendingInsuranceButton, sentInsuranceButton);

        return vbox;
    }

    private void showTransportTable(ObservableList<TransportDetails> transportList, String title, User user) {
        Stage tableStage = new Stage();
        tableStage.setTitle(title);


        // Tabelle erstellen
        TableView<TransportDetails> tableView = new TableView<>(transportList);


        TableColumn<TransportDetails, String> transportIdColumn = new TableColumn<>("Transport ID");
        transportIdColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().id().toString()));

        TableColumn<TransportDetails, String> transportDocumentColumn = new TableColumn<>("Transport Dokument ID");
        transportDocumentColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().transportDocument().id().toString()));

        TableColumn<TransportDetails, String> dateColumn = new TableColumn<>("Datum");
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().transportDate().toString()));

        TableColumn<TransportDetails, String> processingStateColumn = new TableColumn<>("Bearbeitungsstatus");
        processingStateColumn.setCellValueFactory(data -> {
            ProcessingState processingState = data.getValue().processingState();
            return new SimpleStringProperty(
                    processingState.toString()
            );
        });






        TableColumn<TransportDetails, String> transportProviderColumn = new TableColumn<>("TransportDienstleister");
        transportProviderColumn.setCellValueFactory(data -> {
            COptional<Reference<ServiceProvider>> transportProvider = data.getValue().transportProvider();
            return new SimpleStringProperty(
                    transportProvider.get().toString()
            );
        });





        ObservableList<TransportDocument> transportDocuments = TransportInvoiceController.getTransportDocuments(user);
        System.out.println("Transportdokumente: " + transportDocuments);

        TableColumn<TransportDetails, String> healthcareProviderColumn = new TableColumn<>("Behandlungsstätte");
        healthcareProviderColumn.setCellValueFactory(data -> {
            TransportDocument transportDocument = transportDocuments.stream()
                    .filter(doc -> doc.id().value().equals(data.getValue().transportDocument().id().value()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Transportdokument nicht gefunden"));
            COptional<Reference<ServiceProvider>> serviceProvider = COptional.of(transportDocument.healthcareServiceProvider());
            return new SimpleStringProperty(
                    serviceProvider.isPresent() ? serviceProvider.get().toString() : "nicht angegeben"
            );
        });

        TableColumn<TransportDetails, String> patientColumn = new TableColumn<>("Patient");
        patientColumn.setCellValueFactory(data -> {
            TransportDocument transportDocument = transportDocuments.stream()
                    .filter(doc -> doc.id().value().equals(data.getValue().transportDocument().id().value()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Transportdokument nicht gefunden " + data.getValue().transportDocument().id().value() + " " + data.getValue().id().value()));
            COptional<Reference<Patient>> patient = transportDocument.patient();
            return new SimpleStringProperty(
                    patient.isPresent() ? patient.get().toString() : "nicht angegeben"
            );
        });


        TableColumn<TransportDetails, String> insuranceColumn = new TableColumn<>("Versicherung");
        insuranceColumn.setCellValueFactory(data -> {

            TransportDocument transportDocument = transportDocuments.stream()
                    .filter(doc -> doc.id().value().equals(data.getValue().transportDocument().id().value()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Transportdokument nicht gefunden"));

            COptional<Reference<InsuranceData>> insurance = transportDocument.insuranceData();
            return new SimpleStringProperty(
                    insurance.isPresent() ? insurance.get().toString() : "nicht angegeben"
            );
        });



        tableView.getColumns().addAll(transportIdColumn, transportDocumentColumn, patientColumn, insuranceColumn , dateColumn, processingStateColumn, transportProviderColumn);
        tableView.setRowFactory(tv -> {
            TableRow<TransportDetails> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    TransportDetails selectedTransport = row.getItem();
                    showOptionsDialog(selectedTransport, user);
                }
            });
            return row;
        });

        VBox vbox = new VBox(tableView);
        vbox.setPadding(new Insets(20));
        Scene scene = new Scene(vbox, 800, 400);
        tableStage.setScene(scene);
        tableStage.show();
    }

    private void showOptionsDialog(TransportDetails transport, User user) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Transport Optionen");

        // Button "An Versicherung senden"
        Button sendToInsuranceButton = new Button("An Versicherung senden");
        sendToInsuranceButton.setOnAction(event -> {
            TransportInvoiceController.sendToInsurance(transport);

            refreshTransportData(user);

            dialogStage.close();
        });

        Button editTransportButton = getTransportButton(transport, user, dialogStage);

        VBox vbox = new VBox(sendToInsuranceButton, editTransportButton);
        vbox.setSpacing(20);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 300, 200);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private Button getTransportButton(TransportDetails transport, User user, Stage dialogStage) {
        Button editTransportButton = new Button("Transport bearbeiten");
        editTransportButton.setOnAction(event -> {
            TransportDetailsCreationScreen transportDetailsCreationScreen = new TransportDetailsCreationScreen();
            Stage editTransportDetailsStage = transportDetailsCreationScreen.createTransportDetailsCreationWindow(transport, user);

            // Wait for edit window to close
            editTransportDetailsStage.setOnHiding(e -> refreshTransportData(user));

            editTransportDetailsStage.showAndWait();
            dialogStage.close();
        });
        return editTransportButton;
    }

    private void refreshTransportData(User user) {
        // Re-fetch data and update observable lists
        pendingInsuranceTransports.setAll(TransportInvoiceController.getPendingInvoiceTransports(user));
        sentInsuranceTransports.setAll(TransportInvoiceController.getSentInvoiceTransports(user));
    }
}


