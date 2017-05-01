package ui.config;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import ui.UiUtil;

import java.io.IOException;

/**
 * Created by dsm_025 on 2017-04-24.
 */
public class ModifyController extends BorderPane{
    @FXML
    private JFXTextField inputField;
    @FXML
    private JFXButton saveBtn;
    @FXML
    private JFXButton cancelBtn;

    private boolean successInput;


    public ModifyController() {
        try {
            UiUtil.loadFxml(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveBtn.setOnMouseClicked(event -> {
            successInput = true;
            Window window = getScene().getWindow();
            ((Stage) window).close();
        });

        cancelBtn.setOnMouseClicked(event -> {
            successInput = false;
            Window window = getScene().getWindow();
            ((Stage) window).close();
        });
    }

    public String getChangeValue(){
        return inputField.getText();
    }

    public boolean isSuccessInput(){
        return successInput;
    }
}
