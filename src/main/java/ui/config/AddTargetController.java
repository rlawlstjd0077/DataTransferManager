package ui.config;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ui.UiUtil;

import java.io.IOException;

/**
 * Target을 추가 하는 경우 사용되는 컨트롤러
 */
public class AddTargetController extends BorderPane {
    @FXML
    private JFXButton FTPSButton;
    @FXML
    private JFXButton SMBButton;

    private String inputResult = null;
    private boolean saveState;

    public AddTargetController() {
        try {
            UiUtil.loadFxml(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FTPSButton.setOnMouseClicked(event -> {
            inputResult = "FTPS";
            saveState = true;
            ((Stage) getScene().getWindow()).close();
        });
        SMBButton.setOnMouseClicked(event -> {
            inputResult = "SMB";
            saveState = true;
            ((Stage) getScene().getWindow()).close();
        });
    }

    public String getInputResult() {
        return inputResult;
    }

    public boolean isSaveState() {
        return saveState;
    }
}