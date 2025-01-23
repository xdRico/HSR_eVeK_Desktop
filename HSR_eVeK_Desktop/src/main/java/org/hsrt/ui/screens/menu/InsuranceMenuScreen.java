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
import org.hsrt.ui.screens.managementScreens.InvoiceManagement;
import org.hsrt.ui.screens.managementScreens.TransportDocumentManagement;

/**
 * The main menu screen of the application for Insurance users.
 */

public class InsuranceMenuScreen extends Application {
    private User user;



    public InsuranceMenuScreen(User user) {
        this.user = user;
    }

    /**
     * Starts the application and displays the main menu.
     *
     * @param primaryStage The main stage of the application.
     * @throws Exception If an error occurs while starting the application.
     */

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


        InvoiceManagement invoiceManagement = new InvoiceManagement();
        TransportDocumentManagement transportDocumentManagement = new TransportDocumentManagement();
        // Kategorie-Buttons
       Button sentInsuranceButton = createCategoryButton("Versicherungen", transportDocumentManagement.createTransportDocumentManagement(user));

        // Kategorie-Buttons zur VBox hinzufügen
        root.getChildren().addAll(sentInsuranceButton);

        // Scene und Stage konfigurieren
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Versicherungs Menübildschirm");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Creates a new category button with the given text and action.
     * @param text The text of the button.
     * @param stage The stage to show when the button is clicked.
     * @return The created button.
     */

    private Button createCategoryButton(String text, Stage stage) {
        Button button = new Button(text);
        button.setPrefWidth(300);
        button.setOnAction(event -> stage.show());
        return button;
    }
}