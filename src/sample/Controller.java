package sample;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        saveButton.setOnMouseClicked(event -> handleButton(event));
    }

    public void handleButton(Event event){
        switch (((Button) event.getSource()).getId()){
            case "saveButton":
                break;
            case "cancelButton":
                break;
        }
    }
}
