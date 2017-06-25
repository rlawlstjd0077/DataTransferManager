package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        primaryStage.setTitle("Data Transfer SW");
        primaryStage.setScene(new Scene(root, 400, 275));
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            Alert closeConfiguration = new Alert(Alert.AlertType.CONFIRMATION);
            closeConfiguration.setTitle("Close DataTransfer Manager");
            closeConfiguration.setHeaderText("Close Application");
            closeConfiguration.setContentText("Are you sure to close the application?");

            Optional<ButtonType> result = closeConfiguration.showAndWait();
            if (result.get().equals(ButtonType.OK)) {
                primaryStage.close();
                System.exit(0);
            }
        });
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        launch(args);
    }
}
