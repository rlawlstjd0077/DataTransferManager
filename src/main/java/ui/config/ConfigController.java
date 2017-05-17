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

import static ui.config.ConfigController.Role.ADDABLE;
import static ui.config.ConfigController.Role.SELECTABLE;

/**
 * Config 페이지의 컨트롤러
 */
public class ConfigController extends BorderPane {
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
    private TreeBindManager manager;

    public ConfigController() {
        try {
            UiUtil.loadFxml(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        manager = new TreeBindManager(configTree);
        scanConfigFile();
        configTree.setEditable(true);
        configTree.getSelectionModel().getSelectedItems().addListener((ListChangeListener) c -> {
            ConfigContentViewModel content = getCurrentSelectedItem().getValue();
            switch (content.getRole()) {
                case NONE:
                    setButtonsDisable(true, true, true);
                    break;
                case REMOVEABLE:
                    setButtonsDisable(true, false, true);
                    break;
                case MODIFIABLE:
                    setButtonsDisable(true, true, false);
                    break;
                case ADDABLE:
                case ADDABLE_TARGET:
                    setButtonsDisable(false, true, true);
                    break;
                case SELECTABLE:
                    setButtonsDisable(true, true, false);
                    break;
                default:
                    break;
            }
        });

        addBtn.setOnMouseClicked(event ->
                handleButtons(event, getCurrentSelectedItem().getValue()));
        removeBtn.setOnMouseClicked(event ->
                handleButtons(event, getCurrentSelectedItem().getValue()));
        modifyBtn.setOnMouseClicked(event ->
                handleButtons(event, getCurrentSelectedItem().getValue()));
        saveBtn.setOnMouseClicked(event -> {
            saveState = true;
            JSONManager.bindJsonFile(manager.createConfigFromTree());
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

    private void setButtonsDisable(boolean addState, boolean removeState, boolean modifyState) {
        addBtn.setDisable(addState);
        removeBtn.setDisable(removeState);
        modifyBtn.setDisable(modifyState);
    }

    private void handleButtons(MouseEvent event, ConfigContentViewModel value) {
        switch (((JFXButton) event.getSource()).getId()) {
            case "addBtn":
                TreeItem<ConfigContentViewModel> item = getCurrentSelectedItem();
                if (getCurrentSelectedItem().getValue().getRole() == ADDABLE) {
                    getCurrentSelectedItem().getChildren().add(
                            manager.createSourceInfoDir(item.getValue().getName() + "Data"));
                } else {
                    AddTargetController controller = new AddTargetController();
                    Stage newStage = settingNewStage(new Scene(controller), "Input");
                    newStage.setOnHidden(event12 -> {
                        if (controller.isSaveState()) {
                            if (controller.getInputResult().equals("FTPS")) {
                                getCurrentSelectedItem().getChildren().add(manager.createTargetFTPS(item.getValue().getName()));
                            } else if (controller.getInputResult().equals("SMB")) {
                                getCurrentSelectedItem().getChildren().add(manager.createTargetSMB(item.getValue().getName()));
                            }
                        }
                    });
                    newStage.showAndWait();
                }
                break;
            case "removeBtn":
                removeContent(getCurrentSelectedItem());
                break;
            case "modifyBtn":
                ModifyController controller = null;
                ConfigContentViewModel itemViewModel =
                        configTree.getSelectionModel().getSelectedItem().getValue();
                if (value.getRole().equals(SELECTABLE)) {
                    switch (value.getTag()) {
                        case "protocol":
                            controller = new ModifyController(ModifyController.Mode.PROTOCOL, itemViewModel);
                            break;
                        case "sourceDir":
                            controller = new ModifyController(ModifyController.Mode.SOURCEDIR, itemViewModel);
                            break;
                        case "dataType":
                            controller = new ModifyController(ModifyController.Mode.DATATYPE, itemViewModel);
                            break;
                        default:
                            break;
                    }
                } else {
                    controller = new ModifyController(ModifyController.Mode.DEFAULT);
                }
                final ModifyController con = controller;
                Stage newStage = settingNewStage(new Scene(con), "Input");
                newStage.setOnHidden(event1 -> {
                            if (con.isSuccessInput()) {
                                if (value.getTag().equals("protocol")) {
                                    TreeItem<ConfigContentViewModel> parent = getCurrentSelectedItem().getParent();
                                    if (con.getChangeValue().equals("FTPS")) {
                                        Target target = getCurrentSelectedItem().getValue().getName().equals("FTPS")
                                                ? manager.getFTPSTargetFromTreeItem(parent) : manager.getSMBTargetFromTreeItem(parent);
                                        manager.modifyFTPSTargetTreeItem(getCurrentSelectedItem().getParent(), target);
                                    } else {
                                        Target target = getCurrentSelectedItem().getValue().getName().equals("FTPS")
                                                ? manager.getFTPSTargetFromTreeItem(parent) : manager.getSMBTargetFromTreeItem(parent);
                                        manager.modifySMBTargetTreeItem(getCurrentSelectedItem().getParent(), target);
                                    }
                                } else {
                                    getCurrentSelectedItem().getValue().setName(con.getChangeValue());
                                }
                                configTree.setCellFactory(cell -> new TextFieldTreeCellImpl());
                            }
                        }
                );
                newStage.showAndWait();
            default:
                break;
        }
    }

    /**
     * 해당 item을 remove 하는 메소드
     *
     * @param item
     */
    private void removeContent(TreeItem<ConfigContentViewModel> item) {
        item.getParent().getChildren().remove(item);
    }


    /**
     * 현재 선택된 TreeItem을 반환하는 메소드
     *
     * @return
     */
    private TreeItem<ConfigContentViewModel> getCurrentSelectedItem() {
        return configTree.getSelectionModel().getSelectedItem();
    }

    /**
     * 새로운 Stage의 초기 설정 메소드
     *
     * @param scene
     * @param title
     * @return
     */
    private Stage settingNewStage(Scene scene, String title) {
        Stage newStage = new Stage();
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setScene(scene);
        newStage.setTitle(title);
        return newStage;
    }

    /**
     * ConfigFile을 읽어서 TreeView 뷰를 구성하는 메소드
     */
    private void scanConfigFile() {
        Config config = Config.getConfigFile();
        TreeItem<ConfigContentViewModel> root =
                new TreeItem<>(new ConfigContentViewModel("dataDir", "data", Role.NONE));

        ArrayList<Receive> receiveList = config.getReceive();
        ArrayList<Transfer> transferList = config.getTransfer();

        root.getChildren().add(manager.getTransferData(transferList));
        root.getChildren().add(manager.getReceiveData(receiveList));

        configTree.setRoot(root);
        root.setExpanded(true);
    }


    /**
     * ConfigTreeView의 TreeCell
     */
    private final class TextFieldTreeCellImpl extends TreeCell<ConfigContentViewModel> {
        @Override
        protected void updateItem(ConfigContentViewModel item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setText(item.getTag() == null
                        ? item.getName() : item.getTag() + " : " + item.getName());
            } else {
                setText("");
            }
        }
    }

    /**
     * Config Tree Item들의 종류를 나타내는 Enum
     */
    public enum Role {
        NONE("none"),
        ADDABLE("dir"),
        ADDABLE_TARGET("dir_targret"),
        REMOVEABLE("sub"),
        MODIFIABLE("detail"),
        SELECTABLE("selectable");

        private String keyword;

        Role(String role) {
            this.keyword = role;
        }
    }
}