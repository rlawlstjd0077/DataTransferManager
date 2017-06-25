package ui;

import data.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import manager.FileMoveManager;
import manager.FileSender;
import manager.FolderObserver;
import manager.JSONManager;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.config.ConfigController;
import ui.setting.SettingController;
import util.DataType;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main Page의 컨트롤러
 */
public class MainController implements Initializable {
    @FXML
    private Button configViewButton;
    @FXML
    private Button settingViewButton;

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private final String TRANSMIT_DATA_FOLDER = "Data/Transmit/Data";
    private final String RECEIVE_DATA_FOLDER = "Data/Receive/Data";

    private ThreadGroup observerGroup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configViewButton.setOnMouseClicked(event -> handleButton(event));
        settingViewButton.setOnMouseClicked(event -> handleButton(event));
        observerGroup = new ThreadGroup("observerGroup");
        refresh();
    }

    public void handleButton(Event event) {
        Stage newStage;
        switch (((Button) event.getSource()).getId()) {
            case "configViewButton":
                ConfigController configController = new ConfigController();
                newStage = settingNewState(new Scene(configController), "ConfigContentView");
                newStage.setOnHidden(event1 -> {
                    if (configController.isSaveState()) {
                        refresh();
                    }
                });
                newStage.showAndWait();
                break;
            case "settingViewButton":
                SettingController settingController = new SettingController();
                newStage = settingNewState(new Scene(settingController), "SettingView");
                newStage.setOnHidden(event1 -> {
                    if (settingController.isSaveState()) {
                        Setting.FILE_SEND_FAIL_LIMIT = settingController.getLimitCount();
                        logger.debug("Fail limit count is changed");
                    }
                });
                newStage.showAndWait();
                break;
            default:
                break;
        }
    }

    private Stage settingNewState(Scene scene, String title) {
        Stage newStage = new Stage();
        newStage.setScene(scene);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setTitle(title);
        return newStage;
    }

    private void refresh() {
        logger.debug("Refresh Config File - config.config");
        JSONManager.parseJsonFile();
        createDataFolder();
        transmitRemainFile();
        traceFolder();
    }

    /**
     * Config의 모든 sourceDir을 체크
     */
    private void transmitRemainFile() {
        Config config = Config.getConfigFile();

        for (Transfer transfer : config.getTransfer()) {
            checkFolder(transfer.getSourceDir(), true);
        }

        for (Receive receive : config.getReceive()) {
            checkFolder(receive.getSourceDir(), false);
        }
    }

    /**
     * 해당 folderPath에 파일이 있으면 move와 전송 기능을 수행
     * @param folderPath
     * @param state : true (Transmit), false(Receive)
     */
    private void checkFolder(String folderPath, boolean state) {
        for (File file : new File(folderPath).listFiles()) {
            String dataFilePath;
            if (state) {
                dataFilePath = FileMoveManager.TRANSMIT_DATA_FOLDER + "/" + new File(folderPath).getName() + "/"
                        + DataType.fromFilename(file.getName()) + "/" + file.getName();
            } else {
                dataFilePath = FileMoveManager.RECEIVE_DATA_FOLDER + "/" + new File(folderPath).getName() + "/"
                        + DataType.fromFilename(file.getName()) + "/" + file.getName();
            }

            if (new File(dataFilePath).exists()) {        //동일한 파일이 있는 경우
                new File(dataFilePath).renameTo(new File(FileMoveManager.getValidDuplicateFile(new File(dataFilePath))));
            }

            FileMoveManager.moveFileToData(folderPath + "/" + file.getName(), dataFilePath);
            FileSender sender = new FileSender(dataFilePath, folderPath, state);
            sender.run();
        }
    }

    private void traceFolder() {
        observerGroup.interrupt();
        Config config = Config.getConfigFile();
        String folderPath;

        for (Transfer transfer : config.getTransfer()) {
            folderPath = transfer.getSourceDir();
            Thread thread = new Thread(observerGroup, new FolderObserver(folderPath, true));
            thread.start();
        }

        for (Receive receive : config.getReceive()) {
            folderPath = receive.getSourceDir();
            Thread thread = new Thread(observerGroup, new FolderObserver(folderPath, false));
            thread.start();
        }
    }

    /**
     * Config에 존재하는 모든 SourceDir 과 DataDir을 craete 하는 메소드
     */
    private void createDataFolder(){
        Config config = Config.getConfigFile();

        for (Transfer transfer : config.getTransfer()) {
            File dataFolder = new File(TRANSMIT_DATA_FOLDER + "/" +FilenameUtils.getBaseName(transfer.getSourceDir()));
            if(!dataFolder.exists()) {
                new File(transfer.getSourceDir()).mkdirs();
                dataFolder.mkdir();
            }
        }

        for (Receive receive : config.getReceive()) {
            File dataFolder = new File(RECEIVE_DATA_FOLDER + "/" +FilenameUtils.getBaseName(receive.getSourceDir()));
            if(!dataFolder.exists()) {
                new File(receive.getSourceDir()).mkdirs();
                dataFolder.mkdir();
            }
        }
    }
}