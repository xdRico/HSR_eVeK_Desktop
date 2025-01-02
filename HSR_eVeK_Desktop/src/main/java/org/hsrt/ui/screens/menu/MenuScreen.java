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

public class MenuScreen extends Application {
    private final User user;

    public MenuScreen(User user) {
        this.user = user;
    }

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
                createCategoryButton("Hospital Menu", () -> new HospitalMenuScreen(user)),
                createCategoryButton("Insurance Menu", () -> new InsuranceMenuScreen(user)),
                createCategoryButton("Transport Provider Menu", () -> new TransportProviderMenuScreen(user))
        );
        root.setCenter(buttonContainer);

        // Configure Scene and Stage
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Menu Screen");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

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
