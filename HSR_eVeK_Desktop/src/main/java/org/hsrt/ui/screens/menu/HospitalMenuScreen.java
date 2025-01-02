package org.hsrt.ui.screens.menu;

import de.ehealth.evek.api.entity.User;
import de.ehealth.evek.api.type.UserRole;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.hsrt.ui.screens.managementScreens.TransportDocumentManagement;
import org.hsrt.ui.screens.managementScreens.UserManagement;

public class HospitalMenuScreen extends Application {
    private User user;

    public HospitalMenuScreen(User user) {
        this.user = user;
    }

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

        if (user.role() == UserRole.HealthcareAdmin || user.role() == UserRole.HealthcareDoctor || user.role() == UserRole.TransportDoctor || user.role() == UserRole.SuperUser) {
            Button transportDocumentButton = createCategoryButton("Transport Document Management", new TransportDocumentManagement().createTransportDocumentManagement());
            buttonBox.getChildren().add(transportDocumentButton);
        }
        if (user.role() == UserRole.HealthcareAdmin || user.role() == UserRole.SuperUser) {
            Button userButton = createCategoryButton("User Management", new UserManagement().createUserManagement(user));
            buttonBox.getChildren().add(userButton);
        }

        root.setCenter(buttonBox);

        // Scene und Stage konfigurieren
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Hospital Menu Screen");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    // Hilfsmethode zum Erstellen eines Kategorie-Buttons
    private Button createCategoryButton(String text, Stage stage) {
        Button button = new Button(text);
        button.setPrefWidth(300);
        button.setOnAction(event -> stage.show());
        return button;
    }
}
