package ui.setting;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ui.UiUtil;

import java.io.IOException;

/**
 * Created by 김진성 on 2017-05-05.
 */
public class SettingController extends BorderPane {
    @FXML
    private JFXButton saveBtn;
    @FXML
    private JFXButton cancelBtn;
    @FXML
    private JFXTextField countField;

    private int limitCount;
    private boolean saveState;

    public SettingController() {
        try {
            UiUtil.loadFxml(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveBtn.setOnMouseClicked(event -> {
            saveState = true;
            limitCount = Integer.parseInt(countField.getText());
            ((Stage) getScene().getWindow()).close();
        });
        cancelBtn.setOnMouseClicked(event -> {
            ((Stage) getScene().getWindow()).close();
        });
    }

    public boolean isSaveState() {
        return saveState;
    }

    public int getLimitCount() {
        return limitCount;
    }
}
