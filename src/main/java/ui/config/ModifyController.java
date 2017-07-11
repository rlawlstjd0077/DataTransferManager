package ui.config;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import ui.UiUtil;
import util.DataType;

import java.io.File;
import java.io.IOException;

/**
 * Created by dsm_025 on 2017-04-24.
 */
public class ModifyController extends BorderPane {
    @FXML
    private JFXTextField inputField;
    @FXML
    private JFXButton saveBtn;
    @FXML
    private JFXButton cancelBtn;
    @FXML
    private ComboBox inputComboBox;
    @FXML
    private Button chooseButton;
    @FXML
    private Label chooserText;

    private Mode mode;
    private boolean successInput;
    private ConfigContentViewModel viewModel;


    public ModifyController(Mode mode, ConfigContentViewModel itemViewModel) {
        this.mode = mode;
        this.viewModel = itemViewModel;
        switch (mode) {
            case SOURCEDIR:
                load(UiUtil.getFxmlLoader(
                        this.getClass(), "ModifyFileChooser.fxml"));
                break;
            case DATATYPE:
            case PROTOCOL:
                load(UiUtil.getFxmlLoader(
                        this.getClass(), "ModifyComboBox.fxml"));
                break;
            case DEFAULT:
                load(UiUtil.getFxmlLoader(this.getClass()));
                break;
            default:
                break;
        }
        init(mode);


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

    public ModifyController(Mode mode) {
        this(mode, null);
    }

    private void init(Mode mode) {
        switch (mode) {
            case SOURCEDIR:
                chooseButton.setOnMouseClicked(event -> {
                    String path = handleSelectDirectory();
                    chooserText.setText(
                            new File(".").toURI().relativize(new File(path).toURI()).getPath());
                    chooserText.setText(chooserText.getText().substring(0, chooserText.getText().length() - 1));
                });
                break;
            case DATATYPE:
                inputComboBox.setItems(FXCollections.observableArrayList(DataType.values()));
                inputComboBox.setValue(viewModel.getName());
                break;
            case PROTOCOL:
                inputComboBox.setItems(FXCollections.observableArrayList("SMB", "FTPS"));
                inputComboBox.setValue(viewModel.getName());
                break;
            case DEFAULT:
                break;
            default:
                break;
        }
    }

    private String handleSelectDirectory() {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final File dir = directoryChooser.showDialog(getScene().getWindow());
        if (dir != null) {
            chooserText.setText(dir.getPath());
        }
        return dir.getPath();
    }

    private void load(FXMLLoader loader) {
        try {
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getChangeValue() {
        switch (mode) {
            case DATATYPE:
                return inputComboBox.getValue().toString();
            case PROTOCOL:
                return inputComboBox.getValue().toString();
            case SOURCEDIR:
                return chooserText.getText();
            case DEFAULT:
                return inputField.getText();
        }
        return null;
    }

    public boolean isSuccessInput() {
        return successInput;
    }

    /**
     * 수행될 수 있는 여러 Mode 들을 정의해 놓은 enum
     */
    public enum Mode {
        DATATYPE("dataType"),
        SOURCEDIR("sourceDir"),
        PROTOCOL("protocol"),
        DEFAULT("default");

        private String keyword;

        Mode(final String role) {
            this.keyword = role;
        }
    }
}
