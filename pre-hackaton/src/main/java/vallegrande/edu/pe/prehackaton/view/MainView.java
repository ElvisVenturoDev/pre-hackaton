package vallegrande.edu.pe.prehackaton.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Punto de entrada de la aplicación JavaFX.
 */
public class MainView extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                MainView.class.getResource("main-view.fxml"));

        Scene scene = new Scene(loader.load(), 1180, 640);
        scene.getStylesheets().add(Objects.requireNonNull(
                MainView.class.getResource("styles.css")).toExternalForm());

        stage.setTitle("Registro de Personas - CRUD MySQL");
        stage.setMinWidth(980);
        stage.setMinHeight(560);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
