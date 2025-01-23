package org.hsrt.ui.screens.menu;

import de.ehealth.evek.api.entity.User;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.function.Supplier;


/**
 * The main menu screen of the application for Superusers.
 */

public class MenuScreen extends Application {
    private final User user;



    public MenuScreen(User user) {
        this.user = user;
    }

    /**
     * Starts the application and displays the main menu.
     *
     * @param primaryStage The main stage of the application.
     * @throws Exception If an error occurs while starting the application.
     */

    @Override
    public void start(Stage primaryStage) {
        // Root layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // User Info Display (oben rechts)
        Text userInfo = new Text(user.firstName() + " " + user.lastName() + " (" + user.role() + ")");
        userInfo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        BorderPane.setAlignment(userInfo, Pos.TOP_RIGHT);
        root.setTop(userInfo);

        // Add category buttons (zentriert in der Mitte)
        VBox buttonContainer = new VBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(
                createCategoryButton("Krankenhaus Menü", () -> new HospitalMenuScreen(user)),
                createCategoryButton("Versicherungs Menü", () -> new InsuranceMenuScreen(user)),
                createCategoryButton("Transportdienstleister Menü", () -> new TransportProviderMenuScreen(user))
        );
        root.setCenter(buttonContainer);

        // Configure Scene and Stage
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Menü Bildschirm");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    /**
     * Creates a new button for a category.
     * @param text The text to display on the button.
     * @param menuSupplier A supplier for the menu screen to open when the button is clicked.
     * @return The created button.
     */

    private Button createCategoryButton(String text, Supplier<Application> menuSupplier) {
        Button button = new Button(text);
        button.setPrefWidth(300);
        button.setOnAction(event -> {
            try {
                Application menuApp = menuSupplier.get();
                Stage menuStage = new Stage();
                menuApp.start(menuStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return button;
    }
}
