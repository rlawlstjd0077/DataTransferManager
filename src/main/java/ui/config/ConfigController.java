package ui.config;

import com.jfoenix.controls.JFXButton;
import data.Config;
import data.Receive;
import data.Target;
import data.Transfer;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import manager.JSONManager;
import ui.UiUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ui.config.ConfigController.Role.ADDABLE;
import static ui.config.ConfigController.Role.REMOVEABLE;

/**
 * Config 페이지의 컨트롤러
 */
public class ConfigController extends BorderPane{
    @FXML
    private TreeView<ConfigContentViewModel> configTree;
    @FXML
    private JFXButton saveBtn;
    @FXML
    private JFXButton cancelBtn;
    @FXML
    private JFXButton addBtn;
    @FXML
    private JFXButton removeBtn;
    @FXML
    private JFXButton modifyBtn;

    private boolean saveState;

    public ConfigController(){
        try {
            UiUtil.loadFxml(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        scanConfigFile();
        configTree.setEditable(true);
        configTree.getSelectionModel().getSelectedItems().addListener((ListChangeListener) c -> {
            ConfigContentViewModel content = getCurrentSelectedItem().getValue();
            switch (content.getRole()){
                case NONE:
                    setButtonsDisable(true, true, true);
                    break;
                case REMOVEABLE:
                    setButtonsDisable(true, false, true);
                    break;
                case MODIFIABLE:
                    setButtonsDisable(true, true, false);
                    break;
                case ADDABLE :
                case ADDABLE_TARGET:
                    setButtonsDisable(false, true, true);
                    break;
            }
        });

        addBtn.setOnMouseClicked(event -> handleButtons(event));
        removeBtn.setOnMouseClicked(event -> handleButtons(event));
        modifyBtn.setOnMouseClicked(event -> handleButtons(event));
        saveBtn.setOnMouseClicked(event -> {
            saveState = true;
            JSONManager.bindJsonFile(createConfigFromTree());
            ((Stage) getScene().getWindow()).close();
        });
        cancelBtn.setOnMouseClicked(event -> {
            ((Stage) getScene().getWindow()).close();
        });
        configTree.setCellFactory(p -> new TextFieldTreeCellImpl());
    }

    public boolean isSaveState() {
        return saveState;
    }

    private void setButtonsDisable(boolean addState, boolean removeState, boolean modifyState){
        addBtn.setDisable(addState);
        removeBtn.setDisable(removeState);
        modifyBtn.setDisable(modifyState);
    }

    private void handleButtons(MouseEvent event){
        switch (((JFXButton)event.getSource()).getId()){
            case "addBtn":
                TreeItem<ConfigContentViewModel> item = getCurrentSelectedItem();
                if(getCurrentSelectedItem().getValue().getRole() == ADDABLE){
                    getCurrentSelectedItem().getChildren().add(createSourceInfoDir(item.getValue().getName() + "Data"));
                }else{
                    AddTargetController controller = new AddTargetController();
                    Stage newStage = settingNewStage(new Scene(controller), "Input");
                    newStage.setOnHidden(event12 -> {
                        if(controller.getInputResult().equals("FTPS")){
                            getCurrentSelectedItem().getChildren().add(createTargetFTPS(item.getValue().getName()));
                        }else if(controller.getInputResult().equals("SMB")){
                            getCurrentSelectedItem().getChildren().add(createTargetSMB(item.getValue().getName()));
                        }
                    });
                    newStage.showAndWait();
                }
                break;
            case "removeBtn":
                removeContent(getCurrentSelectedItem());
                break;
            case "modifyBtn":
                ModifyController controller = new ModifyController();
                Stage newStage  = settingNewStage(new Scene(controller), "Input");
                newStage.setOnHidden(event1 -> {
                    if(controller.isSuccessInput()){
                        getCurrentSelectedItem().getValue().setName(controller.getChangeValue());
                        getCurrentSelectedItem().getParent().setExpanded(false);
                    }
                });
                newStage.showAndWait();
                break;
        }
    }

    /**
     * 해당 item을 remove 하는 메소드
     * @param item
     */
    private void removeContent(TreeItem<ConfigContentViewModel> item){
        item.getParent().getChildren().remove(item);
    }

    /**
     * Transfer, Receive 와 같은 Directory를 생성해주는 메소드
     * @param itemName
     * @return
     */
    private TreeItem<ConfigContentViewModel> createSourceInfoDir(String itemName) {
        TreeItem<ConfigContentViewModel> treeItem = new TreeItem<>(new ConfigContentViewModel(null, itemName, Role.REMOVEABLE));
        treeItem.getChildren().add(new TreeItem<>(new ConfigContentViewModel("sourceDir",  "", Role.MODIFIABLE)));
        treeItem.getChildren().add(new TreeItem<>(new ConfigContentViewModel("dataType",  "", Role.MODIFIABLE)));
        treeItem.getChildren().add(new TreeItem<>(new ConfigContentViewModel(null,  "target", Role.ADDABLE_TARGET)));
        return treeItem;
    }

    /**
     * FPTS Protocol Target을 생성해주는 메소드
     * @param itemName
     * @return
     */
    private TreeItem<ConfigContentViewModel> createTargetSMB(String itemName){
        TreeItem<ConfigContentViewModel> treeItem = new TreeItem<>(new ConfigContentViewModel(null, itemName, Role.REMOVEABLE));
        treeItem.getChildren().add(new TreeItem<>(new ConfigContentViewModel("title",  "", Role.MODIFIABLE)));
        treeItem.getChildren().add(new TreeItem<>(new ConfigContentViewModel("protocol",  "SMB", Role.MODIFIABLE)));
        treeItem.getChildren().add(new TreeItem<>(new ConfigContentViewModel("tempDir",  "", Role.MODIFIABLE)));
        treeItem.getChildren().add(new TreeItem<>(new ConfigContentViewModel("rootDir",  "", Role.MODIFIABLE)));
        treeItem.getChildren().add(new TreeItem<>(new ConfigContentViewModel("netWorkInfo",  "", Role.MODIFIABLE)));
        return treeItem;
    }

    /**
     * FPTS Protocol Target을 생성해주는 메소드
     * @param itemName
     * @return
     */
    private TreeItem<ConfigContentViewModel> createTargetFTPS(String itemName){
        TreeItem<ConfigContentViewModel> treeItem = new TreeItem<>(new ConfigContentViewModel(null, itemName, Role.REMOVEABLE));
        treeItem.getChildren().add(new TreeItem<>(new ConfigContentViewModel("title",  "", Role.MODIFIABLE)));
        treeItem.getChildren().add(new TreeItem<>(new ConfigContentViewModel("protocol",  "FTPS", Role.MODIFIABLE)));
        treeItem.getChildren().add(new TreeItem<>(new ConfigContentViewModel("ip",  "", Role.MODIFIABLE)));
        treeItem.getChildren().add(new TreeItem<>(new ConfigContentViewModel("port",  "", Role.MODIFIABLE)));
        treeItem.getChildren().add(new TreeItem<>(new ConfigContentViewModel("tempDir",  "", Role.MODIFIABLE)));
        treeItem.getChildren().add(new TreeItem<>(new ConfigContentViewModel("rootDir",  "", Role.MODIFIABLE)));
        treeItem.getChildren().add(new TreeItem<>(new ConfigContentViewModel("user",  "", Role.MODIFIABLE)));
        treeItem.getChildren().add(new TreeItem<>(new ConfigContentViewModel("password",  "", Role.MODIFIABLE)));
        return treeItem;
    }

    /**
     * 현재 선택된 TreeItem을 반환하는 메소드
     * @return
     */
    private TreeItem<ConfigContentViewModel> getCurrentSelectedItem(){
        return configTree.getSelectionModel().getSelectedItem();
    }

    /**
     * 새로운 Stage의 초기 설정 메소드
     * @param scene
     * @param title
     * @return
     */
    private Stage settingNewStage(Scene scene, String title){
        Stage newStage = new Stage();
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setScene(scene);
        newStage.setTitle(title);
        return newStage;
    }

    /**
     * ConfigFile을 읽어서 TreeView 뷰를 구성하는 메소드
     */
    private void scanConfigFile(){
        Config config = Config.configFile;
        TreeItem<ConfigContentViewModel> root = new TreeItem<>(new ConfigContentViewModel("dataDir", "data", Role.NONE));

        ArrayList<Receive> receiveList = config.getReceive();
        ArrayList<Transfer> transferList = config.getTransfer();

        root.getChildren().add(getTransferDatas(transferList));
        root.getChildren().add(getReceiveDatas(receiveList));

        configTree.setRoot(root);
        root.setExpanded(true);
    }

    private TreeItem<ConfigContentViewModel> getTransferDatas(List<Transfer> transferList) {
        TreeItem<ConfigContentViewModel> transferItem = new TreeItem<>(new ConfigContentViewModel(null, "transfer", Role.ADDABLE));
        for(Transfer transfer : transferList){
            TreeItem<ConfigContentViewModel> transferData = new TreeItem<>(new ConfigContentViewModel(null, "transferData", REMOVEABLE));
            transferData.getChildren().add(new TreeItem<>(new ConfigContentViewModel("sourceDir",  transfer.getSourceDir(), Role.MODIFIABLE)));
            transferData.getChildren().add(new TreeItem<>(new ConfigContentViewModel("dataType",  transfer.getSourceDir(), Role.MODIFIABLE)));
            transferData.getChildren().add(getTransferTarget(transfer));
            transferItem.getChildren().add(transferData);
        }
        return transferItem;
    }

    private TreeItem<ConfigContentViewModel> getReceiveDatas(List<Receive> receiveList){
        TreeItem<ConfigContentViewModel> receiveItem = new TreeItem<>(new ConfigContentViewModel(null, "receive", Role.ADDABLE));
        for(Receive receive : receiveList){
            TreeItem<ConfigContentViewModel> receiveData = new TreeItem<>(new ConfigContentViewModel(null, "receiveData", Role.REMOVEABLE));
            receiveData.getChildren().add(new TreeItem<>(new ConfigContentViewModel("sourceDir",  receive.getSourceDir(), Role.MODIFIABLE)));
            receiveData.getChildren().add(new TreeItem<>(new ConfigContentViewModel("dataType",  receive.getSourceDir(), Role.MODIFIABLE)));
            receiveData.getChildren().add(getReceiveTarget(receive));
            receiveItem.getChildren().add(receiveData);
        }
        return receiveItem;
    }
    private TreeItem<ConfigContentViewModel> getTransferTarget(Transfer transfer){
        TreeItem<ConfigContentViewModel> target = new TreeItem<>(new ConfigContentViewModel(null, "target", Role.ADDABLE_TARGET));
        for(Target targetData : transfer.getTarget()){
            TreeItem<ConfigContentViewModel> targetContent = new TreeItem<>(new ConfigContentViewModel(null, "target" , REMOVEABLE));
            targetContent.getChildren().add(new TreeItem<>(new ConfigContentViewModel("title",  targetData.getTitle(), Role.MODIFIABLE)));
            targetContent.getChildren().add(new TreeItem<>(new ConfigContentViewModel("protocol",  targetData.getProtocol(), Role.MODIFIABLE)));
            targetContent.getChildren().add(new TreeItem<>(new ConfigContentViewModel("ip",  targetData.getIp(), Role.MODIFIABLE)));
            targetContent.getChildren().add(new TreeItem<>(new ConfigContentViewModel("port",  targetData.getPort(), Role.MODIFIABLE)));
            targetContent.getChildren().add(new TreeItem<>(new ConfigContentViewModel("tempDir",  targetData.getTempDir(), Role.MODIFIABLE)));
            targetContent.getChildren().add(new TreeItem<>(new ConfigContentViewModel("rootDir",  targetData.getRootDir(), Role.MODIFIABLE)));
            targetContent.getChildren().add(new TreeItem<>(new ConfigContentViewModel("user",  targetData.getUser(), Role.MODIFIABLE)));
            targetContent.getChildren().add(new TreeItem<>(new ConfigContentViewModel("password",  targetData.getPassword(), Role.MODIFIABLE)));
            target.getChildren().add(targetContent);
        }
        return target;
    }

    private TreeItem<ConfigContentViewModel> getReceiveTarget(Receive receive){
        TreeItem<ConfigContentViewModel> target = new TreeItem<>(new ConfigContentViewModel(null, "target", Role.ADDABLE_TARGET));
        for(Target targetData : receive.getTarget()){
            TreeItem<ConfigContentViewModel> targetContent = new TreeItem<>(new ConfigContentViewModel(null, "target", REMOVEABLE));
            targetContent.getChildren().add(new TreeItem<>(new ConfigContentViewModel("title",  targetData.getTitle(), Role.MODIFIABLE)));
            targetContent.getChildren().add(new TreeItem<>(new ConfigContentViewModel("protocol",  targetData.getProtocol(), Role.MODIFIABLE)));
            targetContent.getChildren().add(new TreeItem<>(new ConfigContentViewModel("tempDir",  targetData.getTempDir(), Role.MODIFIABLE)));
            targetContent.getChildren().add(new TreeItem<>(new ConfigContentViewModel("rootDir",  targetData.getRootDir(), Role.MODIFIABLE)));
            targetContent.getChildren().add(new TreeItem<>(new ConfigContentViewModel("netWorkInfo",  targetData.getTempDir(), Role.MODIFIABLE)));
            target.getChildren().add(targetContent);
        }
        return target;
    }

    /**
     * TreeView를 읽어 Config 객체로 만들어주는 메소드
     * @return
     */
    private Config createConfigFromTree(){
        Config config = new Config();
        config.setDataDir(configTree.getRoot().getValue().getName());
        TreeItem<ConfigContentViewModel> transfer = configTree.getRoot().getChildren().get(0);
        TreeItem<ConfigContentViewModel> receive = configTree.getRoot().getChildren().get(1);

        ArrayList<Transfer> transferList = new ArrayList<>();
        for(TreeItem<ConfigContentViewModel> transferData : transfer.getChildren()){
            Transfer tmpTransfer = new Transfer();
            tmpTransfer.setSourceDir(transferData.getChildren().get(0).getValue().getName());
            tmpTransfer.setDataType(transferData.getChildren().get(1).getValue().getName());
            tmpTransfer.setTarget(getTargets(transferData.getChildren().get(2)));
            transferList.add(tmpTransfer);
        }
        config.setTransfer(transferList);

        ArrayList<Receive> receiveList = new ArrayList<>();
        for(TreeItem<ConfigContentViewModel> receiveData : receive.getChildren()){
            Receive tmpReceive = new Receive();
            tmpReceive.setSourceDir(receiveData.getChildren().get(0).getValue().getName());
            tmpReceive.setDataType(receiveData.getChildren().get(1).getValue().getName());
            tmpReceive.setTarget(getTargets(receiveData.getChildren().get(2)));
            receiveList.add(tmpReceive);
        }

        config.setReceive(receiveList);
        return config;
    }

    private ArrayList<Target> getTargets(TreeItem<ConfigContentViewModel> targetItem){
        ArrayList<Target> targetList = new ArrayList<>();
        for(TreeItem<ConfigContentViewModel> item : targetItem.getChildren()){
            Target target = new Target();
            String protocol;
            if((protocol = item.getChildren().get(1).getValue().getName()).equals("FTPS")){      //FTPS 일 때
                target.setTitle(item.getChildren().get(0).getValue().getName());
                target.setProtocol(protocol);
                target.setIp(item.getChildren().get(2).getValue().getName());
                target.setPort(item.getChildren().get(3).getValue().getName());
                target.setTempDir(item.getChildren().get(4).getValue().getName());
                target.setRootDir(item.getChildren().get(5).getValue().getName());
                target.setUser(item.getChildren().get(6).getValue().getName());
                target.setPassword(item.getChildren().get(7).getValue().getName());
            }else{
                target.setTitle(item.getChildren().get(0).getValue().getName());
                target.setProtocol(protocol);
                target.setTempDir(item.getChildren().get(2).getValue().getName());
                target.setRootDir(item.getChildren().get(3).getValue().getName());
                target.setNetWorkInfo(item.getChildren().get(4).getValue().getName());
            }
            targetList.add(target);
        }
        return targetList;
    }

    /**
     * ConfigTreeView의 TreeCell
     */
    private final class TextFieldTreeCellImpl extends TreeCell<ConfigContentViewModel> {
        @Override
        protected void updateItem(ConfigContentViewModel item, boolean empty) {
            super.updateItem(item, empty);
            if(!empty) {
                setText(item.getTag() == null ? item.getName() : item.getTag() + " : " + item.getName());
            }else{
                setText("");
            }
        }
    }

    public enum Role {
        NONE("none"),
        ADDABLE("dir"),
        ADDABLE_TARGET("dir_targret"),
        REMOVEABLE("sub"),
        MODIFIABLE("detail");

        private String keyword;

        Role(String role){
            this.keyword = role;
        }
    }
}