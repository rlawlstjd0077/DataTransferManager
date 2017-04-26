package ui;

import data.Config;
import data.Receive;
import data.Transfer;
import data.WatchedList;
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

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Button configViewButton;
    @FXML
    private Button settingViewButton;
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configViewButton.setOnMouseClicked(event -> handleButton(event));
        settingViewButton.setOnMouseClicked(event -> handleButton(event));
        refresh();
    }

    public void handleButton(Event event){
        switch (((Button) event.getSource()).getId()){
            case "configViewButton":
                ConfigController configController = new ConfigController();
                Stage newStage = settingNewState(new Scene(configController), "ConfigContent");
                newStage.setOnHidden(event1 -> {
                    if(configController.isChoiceState()){
                        refresh();
                    }
                });
                newStage.showAndWait();
                break;
            case "settingViewButton":
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
        Config config = Config.configFile;
        for(Transfer transfer : config.getTransfer()){
            String folderPath;
            if((folderPath = WatchedList.findEqualFolder(transfer.getSourceDir(), true)) != null){
                folderPath = WatchedList.addToTrasmitFolder(transfer.getSourceDir());
            }
            Thread thread = new Thread(new FolderObserver(folderPath, true));
        }
        for(Receive receive : config.getReceive()){
            String folderPath;
            if((folderPath = WatchedList.findEqualFolder(receive.getSourceDir(), true)) != null){
                folderPath = WatchedList.addToReceiveFolder(receive.getSourceDir());
            }
            Thread thread = new Thread(new FolderObserver(folderPath, false));
        }
    }
}
