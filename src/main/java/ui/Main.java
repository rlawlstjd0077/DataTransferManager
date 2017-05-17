package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        primaryStage.setTitle("Data Transfer SW");
        primaryStage.setScene(new Scene(root, 400, 275));
        primaryStage.show();
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        launch(args);
    }
}
