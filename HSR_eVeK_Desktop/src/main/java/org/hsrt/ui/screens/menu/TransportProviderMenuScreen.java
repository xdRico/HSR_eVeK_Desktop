package org.hsrt.ui.screens.menu;

import de.ehealth.evek.api.entity.User;
import de.ehealth.evek.api.type.UserRole;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.hsrt.ui.screens.creationScreens.TransportDetailsCreationScreen;
import org.hsrt.ui.screens.managementScreens.InvoiceManagement;
import org.hsrt.ui.screens.managementScreens.TransportDocumentManagement;
import org.hsrt.ui.screens.managementScreens.UserManagement;

public class TransportProviderMenuScreen extends Application {
        private User user;

        /**
         * Creates a new instance of the HospitalMenuScreen.
         *
         * @param user The currently logged-in user.
         */

        public TransportProviderMenuScreen(User user) {
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
            BorderPane root = new BorderPane();
            root.setPadding(new Insets(20));

            // UserInfo oben rechts platzieren
            Text userInfo = new Text(user.firstName() + " " + user.lastName() + " (" + user.role() + ")");
            StackPane userInfoPane = new StackPane(userInfo);
            userInfoPane.setAlignment(Pos.TOP_RIGHT);
            root.setTop(userInfoPane);

            // Buttons in der Mitte
            VBox buttonBox = new VBox(20);
            buttonBox.setAlignment(Pos.CENTER);

            if (user.role() == UserRole.HealthcareDoctor || user.role() == UserRole.HealthcareUser || user.role() == UserRole.TransportDoctor || user.role() == UserRole.SuperUser) {
                Button transportDocumentButton = createCategoryButton("Transport Dokumenten Verwaltung", new TransportDocumentManagement().createTransportDocumentManagement(user));
                buttonBox.getChildren().add(transportDocumentButton);
            }

            if(user.role() == UserRole.TransportUser || user.role() == UserRole.SuperUser){
                Button transportDocumentButton = createCategoryButton("Transport zuweisen", new TransportDetailsCreationScreen().assignTransport(user));
                buttonBox.getChildren().add(transportDocumentButton);
            }

            if (user.role() == UserRole.HealthcareAdmin || user.role() == UserRole.SuperUser || user.role() == UserRole.TransportAdmin || user.role() == UserRole.InsuranceAdmin) {
                Button userButton = createCategoryButton("User Management", new UserManagement().createUserManagement(user));
                buttonBox.getChildren().add(userButton);
            }

            if (user.role() == UserRole.TransportInvoice || user.role() == UserRole.SuperUser){
                Button invoiceButton = createCategoryButton("Invoice Management", new InvoiceManagement().createInvoiceManagement(user));
            }

            root.setCenter(buttonBox);

            // Scene und Stage konfigurieren
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setTitle("Transport Menu Screen");
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();
        }

        /**
         * Creates a new button for a category.
         * @param text
         * @param stage
         * @return
         */

        private Button createCategoryButton(String text, Stage stage) {
            Button button = new Button(text);
            button.setPrefWidth(300);
            button.setOnAction(event -> stage.show());
            return button;
        }
    }
