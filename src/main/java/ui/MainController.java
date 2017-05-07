package ui;

import data.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import manager.FolderObserver;
import manager.JSONManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.config.ConfigController;
import ui.setting.SettingController;

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
    private ThreadGroup observerGroup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configViewButton.setOnMouseClicked(event -> handleButton(event));
        settingViewButton.setOnMouseClicked(event -> handleButton(event));
        observerGroup = new ThreadGroup("observerGroup");
        refresh();
    }

    public void handleButton(Event event){
        Stage newStage;
        switch (((Button) event.getSource()).getId()){
            case "configViewButton":
                ConfigController configController = new ConfigController();
                newStage = settingNewState(new Scene(configController), "ConfigContentView");
                newStage.setOnHidden(event1 -> {
                    if(configController.isSaveState()){
                        refresh();
                    }
                });
                newStage.showAndWait();
                break;
            case "settingViewButton":
                SettingController settingController = new SettingController();
                newStage = settingNewState(new Scene(settingController), "SettingView");
                newStage.setOnHidden(event1 -> {
                    if(settingController.isSaveState()){
                        Setting.FILE_SEND_FAIL_LIMIT = settingController.getLimitCount();
                        logger.debug("Fail Limit Count is Changed");
                    }
                });
                newStage.showAndWait();
                break;
        }
    }

    private Stage settingNewState(Scene scene, String title){
        Stage newStage = new Stage();
        newStage.setScene(scene);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setTitle(title);
        return newStage;
    }

    private void refresh(){
        logger.debug("Config File Refresh");
        JSONManager.parseJsonFile();
        traceFolder();
    }

    private void traceFolder(){
        observerGroup.interrupt();

        Config config = Config.configFile;
        String folderPath;
        for(Transfer transfer : config.getTransfer()){
            if((folderPath = WatchedList.findEqualFolder(transfer.getSourceDir(), true)) == null){
                folderPath = WatchedList.addToTrasmitFolder(transfer.getSourceDir());
            }
            Thread thread = new Thread(observerGroup, new FolderObserver(folderPath, true));
            thread.start();
        }
        for(Receive receive : config.getReceive()){
            if((folderPath = WatchedList.findEqualFolder(receive.getSourceDir(), true)) == null){
                folderPath = WatchedList.addToReceiveFolder(receive.getSourceDir());
            }
            Thread thread = new Thread(observerGroup, new FolderObserver(folderPath, false));
            thread.start();
        }
    }
}
