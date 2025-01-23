package org.hsrt.ui.screens.creationScreens;

import de.ehealth.evek.api.entity.*;
import de.ehealth.evek.api.type.*;
import de.ehealth.evek.api.util.COptional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.hsrt.ui.controllers.TransportDetailsController;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class TransportDetailsCreationScreen {

    public Stage createTransportDetailsCreationWindow(TransportDetails existingTransport,  User user) {
        Stage stage = new Stage();
        stage.setTitle(existingTransport == null ? "Create Transport" : "Edit Transport");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

// Überprüfung auf gesperrten Zustand
        boolean isLocked = existingTransport != null &&
                existingTransport.patientSignatureDate().isPresent();

        // ID Label
        Text idLabel = new Text(existingTransport == null ? "ID wird vergeben" : "ID: " + existingTransport.id());

        // Date Label und Picker
        Label dateLabel = new Label("Datum des Transports:");
        DatePicker datePicker = createDatePicker();
        datePicker.setValue(existingTransport == null ? LocalDate.now() : existingTransport.transportDate().toLocalDate());
        datePicker.setDisable(isLocked);

        // Start Address Pane
        Address startAddresstemp = existingTransport.startAddress().equals(COptional.empty()) ? null : TransportDetailsController.getAddressFromReference(existingTransport.startAddress());
        TitledPane startAddressPane = new TitledPane("Startadresse", createAddressFields("Start", startAddresstemp));
        startAddressPane.setDisable(isLocked);

        // End Address Pane
        Address endAddresstemp = existingTransport.endAddress().equals(COptional.empty()) ? null : TransportDetailsController.getAddressFromReference(existingTransport.endAddress());
        TitledPane endAddressPane = new TitledPane("Zieladresse", createAddressFields("Ziel", endAddresstemp));
        endAddressPane.setDisable(isLocked);

        // Direction ComboBox
        Label directionLabel = new Label("Richtung:");
        ComboBox<String> directionComboBox = new ComboBox<>();
        directionComboBox.getItems().addAll(
                Arrays.stream(Direction.values())
                        .map(Direction::toString)
                        .toList()
        );
        directionComboBox.getItems().add("nicht angegeben");
        directionComboBox.setValue(existingTransport.direction().equals(COptional.empty()) ? "nicht angegeben" : existingTransport.direction().get().toString());
        directionComboBox.setDisable(isLocked);

        // Patient Condition ComboBox
        Label patientConditionLabel = new Label("Patientenzustand:");
        ComboBox<String> patientConditionComboBox = new ComboBox<>();
        patientConditionComboBox.getItems().addAll(
                Arrays.stream(PatientCondition.values())
                        .map(PatientCondition::toString)
                        .toList()
        );
        patientConditionComboBox.getItems().add("nicht angegeben");
        patientConditionComboBox.setValue(existingTransport.patientCondition().equals(COptional.empty()) ? "nicht angegeben" : existingTransport.patientCondition().get().toString());
        patientConditionComboBox.setDisable(isLocked);

        // Transport Provider ComboBox
        Label providerLabel = new Label("Transportanbieter:");
        ComboBox<String> transportProviderComboBox = new ComboBox<>();
        List<ServiceProvider> tempProviders = TransportDetailsController.getTransportProviders();
        tempProviders.forEach(provider -> transportProviderComboBox.getItems().add(provider.name()));
        transportProviderComboBox.getItems().add("nicht angegeben");
        transportProviderComboBox.setDisable(true);
        if(user.role() == UserRole.SuperUser && existingTransport.transportProvider().equals(COptional.empty())){
            ServiceProvider transportProvider = existingTransport.transportProvider().equals(COptional.empty()) ? null : TransportDetailsController.getTransportproviderFromReference(existingTransport.transportProvider());
            transportProviderComboBox.setValue(transportProvider == null ? "nicht angegeben" : transportProvider.name());
            transportProviderComboBox.setDisable(false);

        } else if(user.role() == UserRole.SuperUser) {
            ServiceProvider transportProvider = tempProviders.stream()
                    .filter(provider -> provider.id().equals(existingTransport.transportProvider().get().id()))
                    .findFirst()
                    .orElse(null);
            transportProviderComboBox.setValue(transportProvider == null ? "nicht angegeben" : transportProvider.name());
            transportProviderComboBox.setDisable(true);
        } else if(!existingTransport.transportProvider().equals(COptional.empty())){
            ServiceProvider transportProvider = tempProviders.stream()
                    .filter(provider -> provider.id().equals(existingTransport.transportProvider().get().id()))
                    .findFirst()
                    .orElse(null);
            transportProviderComboBox.setValue(transportProvider == null ? "nicht angegeben" : transportProvider.name());
            transportProviderComboBox.setDisable(true);


        } else{
            ServiceProvider userProvider = tempProviders.stream()
                    .filter(provider -> provider.id().equals(user.serviceProvider().id()))
                    .findFirst()
                    .orElse(null);
            transportProviderComboBox.setValue(userProvider == null ? "nicht angegeben" : userProvider.name());
            transportProviderComboBox.setDisable(true);
        }
        //transportProviderComboBox.setDisable(isLocked);

        // Tour Number Label
        String tourNumber = existingTransport.tourNumber().equals(COptional.empty()) ? "" : existingTransport.tourNumber().get();
        Label tourNumberLabel = new Label("Tournummer: ");
        TextField tourNumberField = new TextField();
        tourNumberField.setText(tourNumber);
        tourNumberField.setDisable(isLocked);

        // Payment Exemption ComboBox
        Label paymentExemptionLabel = new Label("Zahlungsbefreiung:");
        ComboBox<Boolean> paymentExemptionComboBox = new ComboBox<>();
        paymentExemptionComboBox.getItems().addAll(true, false);
        paymentExemptionComboBox.setValue(!existingTransport.paymentExemption().equals(COptional.empty()) && existingTransport.paymentExemption().get());
        paymentExemptionComboBox.setDisable(isLocked);

        // Patient Signature
        Label patientSignatureLabel = new Label("Patientenunterschrift:");
        TextField patientSignatureField = new TextField();
        patientSignatureField.setText(existingTransport.patientSignature().equals(COptional.empty()) ? "" : existingTransport.patientSignature().get());
        patientSignatureField.setDisable(isLocked);
        Button confirmPatientSignatureButton = new Button("Bestätigen");
        COptional<Date> patientSignatureDate = existingTransport.patientSignatureDate().equals(COptional.empty()) ? null : existingTransport.patientSignatureDate();
        Label patientDateLabel = new Label(patientSignatureDate == null ? "nicht bestätigt" : patientSignatureDate.get().toString());
        AtomicReference<COptional<Date>> patientSignatureDateRef = new AtomicReference<>(patientSignatureDate);

// Transporter Signature
        Label transporterSignatureLabel = new Label("Transporteurunterschrift:");
        TextField transporterSignatureField = new TextField();
        transporterSignatureField.setText(existingTransport.transporterSignature().equals(COptional.empty()) ? "" : existingTransport.transporterSignature().get());
        transporterSignatureField.setDisable(isLocked);
        Button confirmTransporterSignatureButton = new Button("Bestätigen");
        COptional<Date> transporterSignatureDate = existingTransport.transporterSignatureDate().equals(COptional.empty()) ? null : existingTransport.transporterSignatureDate();
        Label transporterDateLabel = new Label(transporterSignatureDate == null ? "nicht bestätigt" : transporterSignatureDate.get().toString());
        AtomicReference<COptional<Date>> transporterSignatureDateRef = new AtomicReference<>(transporterSignatureDate);

// Bestätigungs-Button-Logik für Patient Signature
        confirmPatientSignatureButton.setDisable(patientSignatureDate != null); // Nur aktivieren, wenn kein Datum vorhanden
        confirmPatientSignatureButton.setOnAction(event -> {
            if (!patientSignatureField.getText().isEmpty() && patientSignatureDateRef.get() == null) {
                patientSignatureDateRef.set(COptional.of(new Date(System.currentTimeMillis())));
                patientDateLabel.setText(patientSignatureDateRef.get().get().toString());
                confirmPatientSignatureButton.setDisable(true); // Nach Bestätigung deaktivieren
                System.out.println("PatientSignatureDate hinzugefügt: " + patientSignatureDateRef.get().get());
            }
        });

// Bestätigungs-Button-Logik für Transporter Signature
        confirmTransporterSignatureButton.setDisable(transporterSignatureDate != null); // Nur aktivieren, wenn kein Datum vorhanden
        confirmTransporterSignatureButton.setOnAction(event -> {
            if (!transporterSignatureField.getText().isEmpty() && transporterSignatureDateRef.get() == null) {
                transporterSignatureDateRef.set(COptional.of(new Date(System.currentTimeMillis())));
                transporterDateLabel.setText(transporterSignatureDateRef.get().get().toString());
                confirmTransporterSignatureButton.setDisable(true); // Nach Bestätigung deaktivieren
                System.out.println("TransporterSignatureDate hinzugefügt: " + transporterSignatureDateRef.get().get());
            }
        });

        // Save Button
        Button saveButton = new Button("Speichern");
        saveButton.setOnAction(event -> {
            if (existingTransport == null || isLocked) return;

            Address startAddress = extractAddressFromFields((GridPane) startAddressPane.getContent());
            Address endAddress = extractAddressFromFields((GridPane) endAddressPane.getContent());

            System.out.println("Startadresse: " + startAddress);
            System.out.println("Zieladresse: " + endAddress);

            System.out.println(transporterSignatureField.getText());
            System.out.println(transporterSignatureDateRef.get());
            System.out.println(patientSignatureField.getText());
            System.out.println(patientSignatureDateRef.get());

            ServiceProvider transportProvider = tempProviders.stream()
                    .filter(provider -> provider.name().equals(transportProviderComboBox.getValue()))
                    .findFirst()
                    .orElse(null);



            TransportDetailsController.updateTransport(
                    existingTransport.id(),
                    COptional.of(startAddress),
                    COptional.of(endAddress),
                    COptional.of(Direction.valueOf(directionComboBox.getValue())),
                    COptional.of(PatientCondition.valueOf(patientConditionComboBox.getValue())),
                    COptional.of(tourNumberField.getText()),
                    COptional.of(paymentExemptionComboBox.getValue()),
                    transporterSignatureField.getText() == null ? "" : transporterSignatureField.getText(),
                    transporterSignatureDateRef.get() == null ? null : transporterSignatureDateRef.get().get(),
                    patientSignatureField.getText() == null ? "" : patientSignatureField.getText(),
                    patientSignatureDateRef.get() == null ? null : patientSignatureDateRef.get().get(),
                    existingTransport.transportProvider().equals(COptional.empty()) ? transportProvider : null
            );
            stage.close();
        });
        // Speichern-Button-Logik
        saveButton.setDisable(true); // Initial deaktivieren

        // Text für Fehlermeldungen
        Text errorLabel = new Text();
        errorLabel.setFill(Color.RED); // Rot für Fehleranzeige
        errorLabel.setVisible(false); // Standardmäßig ausgeblendet

// Methode zur Validierung des Speichern-Buttons und Aktualisierung der Fehlermeldung
        Runnable validateSaveButtonAndStyles = () -> {
            boolean isStartAddressValid = validateAddressField((GridPane) startAddressPane.getContent());
            boolean isEndAddressValid = validateAddressField((GridPane) endAddressPane.getContent());
            boolean isDirectionValid = validateComboBox(directionComboBox);
            boolean isPatientConditionValid = validateComboBox(patientConditionComboBox);
            boolean isTransportProviderValid = validateComboBox(transportProviderComboBox);

            // Speichern-Button deaktivieren, wenn Bedingungen nicht erfüllt sind
            saveButton.setDisable(isLocked);
            if(!isLocked) {
                saveButton.setDisable(!(isStartAddressValid && isEndAddressValid && isDirectionValid &&
                        isPatientConditionValid && isTransportProviderValid));
            }

            // Dynamische Fehlermeldung
            StringBuilder errorMessage = new StringBuilder();
            if (!isStartAddressValid) errorMessage.append("Die Startadresse muss ausgefüllt werden.\n");
            if (!isEndAddressValid) errorMessage.append("Die Zieladresse muss ausgefüllt werden.\n");
            if (!isDirectionValid) errorMessage.append("Bitte wählen Sie eine Richtung aus.\n");
            if (!isPatientConditionValid) errorMessage.append("Bitte wählen Sie einen Patientenzustand aus.\n");
            if (!isTransportProviderValid) errorMessage.append("Bitte wählen Sie einen Transportanbieter aus.\n");

            errorLabel.setText(errorMessage.toString());
            errorLabel.setVisible(!errorMessage.isEmpty());
        };

// Event-Listener für Felder mit Validierung
        addChangeListener((GridPane) startAddressPane.getContent(), validateSaveButtonAndStyles);
        addChangeListener((GridPane) endAddressPane.getContent(), validateSaveButtonAndStyles);
        addComboBoxListener(directionComboBox, validateSaveButtonAndStyles, true);
        addComboBoxListener(patientConditionComboBox, validateSaveButtonAndStyles, true);
        addComboBoxListener(transportProviderComboBox, validateSaveButtonAndStyles, true);

        // Hinzufügen eines "Entsperren"-Buttons
        Button unlockButton = new Button("Entsperren");
        if(user.role() == UserRole.InsuranceUser){
            unlockButton.setDisable(true);
        }else{
            unlockButton.setDisable(!isLocked); // Button nur aktiv, wenn der Transport gesperrt ist
        }

        unlockButton.setOnAction(event -> {
            // Bestätigungsdialog anzeigen
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Bestätigung erforderlich");
            confirmationAlert.setHeaderText("Transport entsperren");
            confirmationAlert.setContentText("Möchten Sie den Transport wirklich entsperren? Dadurch werden alle Unterschriften und zugehörige Daten gelöscht.");

            // Benutzeraktion abfragen
            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                TransportDetails newTransport = new TransportDetails(existingTransport.id(), existingTransport.transportDocument(), existingTransport.transportDate(), existingTransport.startAddress(), existingTransport.endAddress(), existingTransport.direction(), existingTransport.patientCondition(), existingTransport.transportProvider(), existingTransport.tourNumber(), existingTransport.paymentExemption(), COptional.empty(), COptional.empty(), COptional.empty(), COptional.empty(), existingTransport.processingState());
                Stage newstage = createTransportDetailsCreationWindow(newTransport, user);
                stage.close();

                newstage.show();
                System.out.println("Transport wurde entsperrt. Felder wurden zurückgesetzt.");
            } else {
                System.out.println("Entsperrung abgebrochen.");
            }
        });

// Hinzufügen des Buttons zum Layout
        VBox content = new VBox(15, idLabel, dateLabel, datePicker, startAddressPane, endAddressPane,
                directionLabel, directionComboBox, patientConditionLabel, patientConditionComboBox,
                providerLabel, transportProviderComboBox, tourNumberLabel,tourNumberField, paymentExemptionLabel, paymentExemptionComboBox,
                patientSignatureLabel, patientSignatureField, confirmPatientSignatureButton, patientDateLabel,
                transporterSignatureLabel, transporterSignatureField, confirmTransporterSignatureButton, transporterDateLabel,
                unlockButton, errorLabel, saveButton);

        // ScrollPane
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        validateSaveButtonAndStyles.run();

// Gesamtes Layout
        VBox layout = new VBox(10, scrollPane);
        layout.setPadding(new Insets(10));
        Scene scene = new Scene(layout, 600, 600);
        stage.setScene(scene);


        return stage;


    }

    private DatePicker createDatePicker() {
        return getDatePicker();
    }

    public static DatePicker getDatePicker() {
        DatePicker datePicker = new DatePicker();
        datePicker.setOnAction(event -> {
            LocalDate localDate = datePicker.getValue();
            if (localDate != null) {
                Date sqlDate = Date.valueOf(localDate);
                System.out.println("Transportdatum: " + sqlDate);
            } else {
                System.out.println("Kein Datum ausgewählt.");
            }
        });
        return datePicker;
    }

    // Methode zur Validierung von Adressfeldern und Aktualisierung ihres Stils
    private boolean validateAddressField(GridPane gridPane) {
        boolean isValid = true;
        for (Node node : gridPane.getChildren()) {
            if (node instanceof TextField textField) {
                if (textField.getText().trim().isEmpty()) {
                    textField.setStyle("-fx-border-color: red; -fx-border-width: 2;");
                    isValid = false;
                } else {
                    textField.setStyle(""); // Zurücksetzen, wenn gültig
                }
            }
        }
        return isValid;
    }

    // Methode zur Validierung von ComboBoxen und Aktualisierung ihres Stils
    private boolean validateComboBox(ComboBox<?> comboBox) {
        boolean isValid = comboBox.getValue() != null && !comboBox.getValue().toString().equals("nicht angegeben");
        if (!isValid) {
            comboBox.setStyle("-fx-border-color: red; -fx-border-width: 2;");
        } else {
            comboBox.setStyle(""); // Zurücksetzen, wenn gültig
        }
        return isValid;
    }

    // Methode zur Validierung und Initialisierung von Adressfeldern
    private void addChangeListener(GridPane gridPane, Runnable onChange) {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof TextField textField) {
                textField.textProperty().addListener((obs, oldVal, newVal) -> onChange.run());

                onChange.run(); // Initial ausführen

            }
        }
    }

    // Methode zur Validierung und Initialisierung von ComboBoxen
    private void addComboBoxListener(ComboBox<?> comboBox, Runnable onChange, boolean runInitially) {
        comboBox.valueProperty().addListener((obs, oldVal, newVal) -> onChange.run());
        if (runInitially) {
            onChange.run(); // Initial ausführen
        }
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

    private Address extractAddressFromFields(GridPane gridPane) {

        TextField streetField = (TextField) gridPane.getChildren().get(1); // Spalte 1, Zeile 0
        TextField houseNumberField = (TextField) gridPane.getChildren().get(3); // Spalte 1, Zeile 1
        TextField postCodeField = (TextField) gridPane.getChildren().get(5); // Spalte 1, Zeile 2
        TextField cityField = (TextField) gridPane.getChildren().get(7); // Spalte 1, Zeile 3
        TextField countryField = (TextField) gridPane.getChildren().get(9); // Spalte 1, Zeile 4


        return TransportDetailsController.createAddress(
                streetField.getText(),
                houseNumberField.getText(),
                postCodeField.getText(),
                cityField.getText(),
                countryField.getText()
        );
    }

    public Stage assignTransport(User user) {
        AtomicReference<Stage> stage = new AtomicReference<>(new Stage());
        stage.get().setTitle("Transport zuweisen");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        //TansportID Label
        Label transportIDLabel = new Label("Transport ID:");
        TextField transportIDField = new TextField();

        AtomicReference<TransportDetails> transport = new AtomicReference<>();
        AtomicReference<TransportDocument> transportDocument = new AtomicReference<>();

        //Bestätigen Button
        Button confirmButton = new Button("Bestätigen");
        confirmButton.setOnAction(event ->{
            transport.set(TransportDetailsController.getTransportFromReference(transportIDField.getText()));
            transportDocument.set(TransportDetailsController.getTransportDocumentFromReference(transport.get().transportDocument()));
            stage.set(createTransportDetailsCreationWindow(transport.get(), user));
            stage.get().show();
        });

        root.getChildren().addAll(transportIDLabel, transportIDField, confirmButton);



        Scene scene = new Scene(root, 300, 200);
        stage.get().setScene(scene);

        return stage.get();

    }
}
