package org.hsrt.ui.screens.menu;

import de.ehealth.evek.api.entity.User;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class InsuranceMenuScreen extends Application {
    private User user;
    public InsuranceMenuScreen(User user) {
        this.user = user;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox root = new VBox(20); // Vertikales Layout mit Abstand zwischen Kategorie-Buttons
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Text userInfo = new Text(user.firstName() + " " + user.lastName() + " (" + user.role() + ")");
        BorderPane.setMargin(userInfo, new Insets(10));
        BorderPane topRight = new BorderPane();
        topRight.setRight(userInfo);
        root.setAlignment(Pos.TOP_RIGHT);

        // Kategorie-Buttons
        Button addressButton = createCategoryButton("Transport Document Management", createTransportDocumentManagement());
        Button userButton = createCategoryButton("User Management", createUserManagement());
        Button accountingButton = createCategoryButton("Accounting Management", createAccountingManagement());

        // Kategorie-Buttons zur VBox hinzufügen
        root.getChildren().addAll(addressButton, userButton, accountingButton);

        // Scene und Stage konfigurieren
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Insurance Menu Screen");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Hilfsmethode zum Erstellen eines Kategorie-Buttons
    private Button createCategoryButton(String text, Stage stage) {
        Button button = new Button(text);
        button.setPrefWidth(300);
        button.setOnAction(event -> stage.show());
        return button;
    }

    // Fenster für TransportDocument Management
    private Stage createTransportDocumentManagement() {
        Stage stage = new Stage();
        stage.setTitle("Transport Document Management");

        VBox vbox = new VBox(10); // Vertikale Anordnung mit 10 Pixel Abstand
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));


        Button createTransportDocument = createButton("Create Transport Document");
        Button updateTransportDocument = createButton("Update Transport Document");
        Button deleteTransportDocument = createButton("Delete Transport Document");
        Button createTransportDocumentData = createButton("Create Trip");
        Button updateTransportDocumentData = createButton("Update Trip");
        Button deleteTransportDocumentData = createButton("Delete Trip");
        vbox.getChildren().addAll(createTransportDocument, updateTransportDocument, deleteTransportDocument, createTransportDocumentData, updateTransportDocumentData, deleteTransportDocumentData);

        Scene scene = new Scene(vbox, 800, 600);
        stage.setScene(scene);

        return stage;
    }

    // Fenster für Insurance Management
    private Stage createUserManagement() {
        Stage stage = new Stage();
        stage.setTitle("Insurance Management");

        VBox vbox = new VBox(10); // Vertikale Anordnung mit 10 Pixel Abstand
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));

        Button createInsurance = createButton("Create Insurance");
        Button updateInsurance = createButton("Update Insurance");
        Button moveInsurance = createButton("Move Insurance");
        Button deleteInsurance = createButton("Delete Insurance");
        Button createInsuranceData = createButton("Create Insurance Data");
        Button deleteInsuranceData = createButton("Delete Insurance Data");
        vbox.getChildren().addAll(createInsurance, updateInsurance, moveInsurance, deleteInsurance, createInsuranceData, deleteInsuranceData);

        Scene scene = new Scene(vbox, 800, 600);
        stage.setScene(scene);

        return stage;
    }

    // Fenster für Patient Management
    private Stage createAccountingManagement() {
        Stage stage = new Stage();
        stage.setTitle("Patient Management");

        VBox vbox = new VBox(10); // Vertikale Anordnung mit 10 Pixel Abstand
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));

        Button createPatient = createButton("Create Patient");
        Button movePatient = createButton("Move Patient");
        Button updatePatient = createButton("Update Patient");
        Button deletePatient = createButton("Delete Patient");
        vbox.getChildren().addAll(createPatient, movePatient, updatePatient, deletePatient);

        Scene scene = new Scene(vbox, 800, 600);
        stage.setScene(scene);

        return stage;
    }

    // Hilfsmethode zum Erstellen eines Buttons, der ein neues Fenster öffnet
    private Button createButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setOnAction(event -> {
            // Neues Fenster erstellen
            Stage newStage = new Stage();
            newStage.setTitle(text);

            // Inhalt des neuen Fensters
            VBox vbox = new VBox();
            vbox.setAlignment(Pos.CENTER);
            vbox.setPadding(new Insets(20));
            vbox.getChildren().add(new Button("This is the " + text + " window"));

            // Szene setzen und Fenster anzeigen
            Scene scene = new Scene(vbox, 400, 300);
            newStage.setScene(scene);
            newStage.show();
        });
        return button;
    }


    // Hilfsmethode zum Erstellen eines GridPane für eine Kategorie
    private GridPane createCategoryGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        return gridPane;
    }
}
