package sample;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import sample.manager.JSONManager;

import java.io.*;
import java.nio.file.*;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        JSONManager.bindJsonFile(null);
        JSONManager.parseJsonFile();
//        monitoringDir("C:/Users/dsm_025/Downloads");
    }

    static void copyFile(String inPath, String outPath){
        try{
            FileInputStream inputStream = new FileInputStream(inPath);
            FileOutputStream outputStream = new FileOutputStream(outPath);
            int data = 0;
            while((data = inputStream.read()) != -1){
                System.out.println(data);
                outputStream.write(data);
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        launch(args);
    }
}
