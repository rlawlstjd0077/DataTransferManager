package ui.config;

import data.Config;
import data.Receive;
import data.Target;
import data.Transfer;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dsm_025 on 2017-05-16.
 */
public class TreeBindManager {
    private TreeView<ConfigContentViewModel> configTree;

    public TreeBindManager(TreeView treeView) {
        configTree = treeView;
    }

    public TreeItem<ConfigContentViewModel> getTransferData(List<Transfer> transferList) {
        TreeItem<ConfigContentViewModel> transferItem =
                new TreeItem<>(new ConfigContentViewModel(
                         null, "transfer", ConfigController.Role.ADDABLE));

        for (Transfer transfer : transferList) {
            TreeItem<ConfigContentViewModel> transferData =
                    new TreeItem<>(new ConfigContentViewModel(
                            null, "transferData", ConfigController.Role.REMOVEABLE));

            transferData.getChildren().add(
                    new TreeItem<>(new ConfigContentViewModel(
                            "sourceDir", transfer.getSourceDir(), ConfigController.Role.SELECTABLE)));
            transferData.getChildren().add(
                    new TreeItem<>(new ConfigContentViewModel(
                            "dataType", transfer.getDataType(), ConfigController.Role.SELECTABLE)));
            transferData.getChildren().add(getTransferTarget(transfer));
            transferItem.getChildren().add(transferData);
        }
        return transferItem;
    }

    public TreeItem<ConfigContentViewModel> getReceiveData(List<Receive> receiveList) {
        TreeItem<ConfigContentViewModel> receiveItem =
                new TreeItem<>(new ConfigContentViewModel(
                        null, "receive", ConfigController.Role.ADDABLE));

        for (Receive receive : receiveList) {
            TreeItem<ConfigContentViewModel> receiveData =
                    new TreeItem<>(new ConfigContentViewModel(
                            null, "receiveData", ConfigController.Role.REMOVEABLE));
            receiveData.getChildren().add(
                    new TreeItem<>(new ConfigContentViewModel(
                            "sourceDir", receive.getSourceDir(), ConfigController.Role.SELECTABLE)));
            receiveData.getChildren().add(
                    new TreeItem<>(new ConfigContentViewModel(
                            "dataType", receive.getDataType(), ConfigController.Role.SELECTABLE)));
            receiveData.getChildren().add(getReceiveTarget(receive));
            receiveItem.getChildren().add(receiveData);
        }
        return receiveItem;
    }

    public TreeItem<ConfigContentViewModel> getTransferTarget(Transfer transfer) {
        TreeItem<ConfigContentViewModel> target =
                new TreeItem<>(new ConfigContentViewModel(
                        null, "target", ConfigController.Role.ADDABLE_TARGET));
        for (Target targetData : transfer.getTarget()) {
            TreeItem<ConfigContentViewModel> targetContent;
            targetContent = targetData.getProtocol().equals("FTPS")
                    ? createFTPSTarget(targetData) : createSMBTarget(targetData);
            target.getChildren().add(targetContent);
        }
        return target;
    }

    public TreeItem<ConfigContentViewModel> getReceiveTarget(Receive receive) {
        TreeItem<ConfigContentViewModel> target =
                new TreeItem<>(new ConfigContentViewModel(null, "target", ConfigController.Role.ADDABLE_TARGET));
        for (Target targetData : receive.getTarget()) {
            TreeItem<ConfigContentViewModel> targetContent;
            targetContent = targetData.getProtocol().equals("FTPS")
                    ? createFTPSTarget(targetData) : createSMBTarget(targetData);
            target.getChildren().add(targetContent);
        }
        return target;
    }

    /**
     * TreeView를 읽어 Config 객체로 만들어주는 메소드
     *
     * @return
     */
    public Config createConfigFromTree() {
        Config config = new Config();
        config.setDataDir(configTree.getRoot().getValue().getName());
        TreeItem<ConfigContentViewModel> transfer = configTree.getRoot().getChildren().get(0);
        TreeItem<ConfigContentViewModel> receive = configTree.getRoot().getChildren().get(1);

        ArrayList<Transfer> transferList = new ArrayList<>();
        for (TreeItem<ConfigContentViewModel> transferData : transfer.getChildren()) {
            Transfer tmpTransfer = new Transfer();
            tmpTransfer.setSourceDir(
                    transferData.getChildren().get(0).getValue().getName());
            tmpTransfer.setDataType(
                    transferData.getChildren().get(1).getValue().getName());
            tmpTransfer.setTarget(
                    getTargets(transferData.getChildren().get(2)));
            transferList.add(tmpTransfer);
        }
        config.setTransfer(transferList);

        ArrayList<Receive> receiveList = new ArrayList<>();
        for (TreeItem<ConfigContentViewModel> receiveData : receive.getChildren()) {
            Receive tmpReceive = new Receive();
            tmpReceive.setSourceDir(
                    receiveData.getChildren().get(0).getValue().getName());
            tmpReceive.setDataType(
                    receiveData.getChildren().get(1).getValue().getName());
            tmpReceive.setTarget(
                    getTargets(receiveData.getChildren().get(2)));
            receiveList.add(tmpReceive);
        }

        config.setReceive(receiveList);
        return config;
    }

    public ArrayList<Target> getTargets(TreeItem<ConfigContentViewModel> targetItem) {
        ArrayList<Target> targetList = new ArrayList<>();
        for (TreeItem<ConfigContentViewModel> item : targetItem.getChildren()) {
            Target target = new Target();
            String protocol;
            ObservableList<TreeItem<ConfigContentViewModel>> list = item.getChildren();

            if ((protocol = item.getChildren().get(1).getValue().getName()).equals("FTPS")) {      //FTPS 일 때
                target.setTitle(list.get(0).getValue().getName());
                target.setProtocol(protocol);
                target.setIp(list.get(2).getValue().getName());
                target.setPort(list.get(3).getValue().getName());
                target.setTempDir(list.get(4).getValue().getName());
                target.setRootDir(list.get(5).getValue().getName());
                target.setUser(list.get(6).getValue().getName());
                target.setPassword(list.get(7).getValue().getName());
            } else {
                target.setTitle(list.get(0).getValue().getName());
                target.setProtocol(protocol);
                target.setTempDir(list.get(2).getValue().getName());
                target.setRootDir(list.get(3).getValue().getName());
                target.setUser(list.get(4).getValue().getName());
                target.setPassword(list.get(5).getValue().getName());
            }
            targetList.add(target);
        }
        return targetList;
    }

    public TreeItem<ConfigContentViewModel> createFTPSTarget(Target targetData) {
        TreeItem<ConfigContentViewModel> targetContent =
                new TreeItem<>(new ConfigContentViewModel(
                        null, "target", ConfigController.Role.REMOVEABLE));

        targetContent.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "title", targetData.getTitle(), ConfigController.Role.MODIFIABLE)));
        targetContent.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "protocol", targetData.getProtocol(), ConfigController.Role.SELECTABLE)));
        targetContent.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "ip", targetData.getIp(), ConfigController.Role.MODIFIABLE)));
        targetContent.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "port", targetData.getPort(), ConfigController.Role.MODIFIABLE)));
        targetContent.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "tempDir", targetData.getTempDir(), ConfigController.Role.MODIFIABLE)));
        targetContent.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "rootDir", targetData.getRootDir(), ConfigController.Role.MODIFIABLE)));
        targetContent.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "user", targetData.getUser(), ConfigController.Role.MODIFIABLE)));
        targetContent.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "password", targetData.getPassword(), ConfigController.Role.MODIFIABLE)));
        return targetContent;
    }

    public TreeItem<ConfigContentViewModel> createSMBTarget(Target targetData) {
        TreeItem<ConfigContentViewModel> targetContent =
                new TreeItem<>(new ConfigContentViewModel(
                        null, "target", ConfigController.Role.REMOVEABLE));
        targetContent.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "title", targetData.getTitle(), ConfigController.Role.MODIFIABLE)));
        targetContent.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "protocol", targetData.getProtocol(), ConfigController.Role.SELECTABLE)));
        targetContent.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "tempDir", targetData.getTempDir(), ConfigController.Role.MODIFIABLE)));
        targetContent.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "rootDir", targetData.getRootDir(), ConfigController.Role.MODIFIABLE)));
        targetContent.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "user", targetData.getUser(), ConfigController.Role.MODIFIABLE)));
        targetContent.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "password", targetData.getPassword(), ConfigController.Role.MODIFIABLE)));
        return targetContent;
    }

    public void modifyFTPSTargetTreeItem(TreeItem<ConfigContentViewModel> target, Target targetData) {
        target.getChildren().clear();
        target.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "title", targetData.getTitle(), ConfigController.Role.MODIFIABLE)));
        target.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "protocol", targetData.getProtocol(), ConfigController.Role.SELECTABLE)));
        target.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "ip", targetData.getIp(), ConfigController.Role.MODIFIABLE)));
        target.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "port", targetData.getPort(), ConfigController.Role.MODIFIABLE)));
        target.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "tempDir", targetData.getTempDir(), ConfigController.Role.MODIFIABLE)));
        target.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "rootDir", targetData.getRootDir(), ConfigController.Role.MODIFIABLE)));
        target.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "user", targetData.getUser(), ConfigController.Role.MODIFIABLE)));
        target.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "password", targetData.getPassword(), ConfigController.Role.MODIFIABLE)));
    }

    public void modifySMBTargetTreeItem(TreeItem<ConfigContentViewModel> target, Target targetData) {
        target.getChildren().clear();
        target.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "title", targetData.getTitle(), ConfigController.Role.MODIFIABLE)));
        target.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "protocol", targetData.getProtocol(), ConfigController.Role.SELECTABLE)));
        target.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "tempDir", targetData.getTempDir(), ConfigController.Role.MODIFIABLE)));
        target.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "rootDir", targetData.getRootDir(), ConfigController.Role.MODIFIABLE)));
        target.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "user", targetData.getUser(), ConfigController.Role.MODIFIABLE)));
        target.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "password", targetData.getPassword(), ConfigController.Role.MODIFIABLE)));
    }

    public Target getFTPSTargetFromTreeItem(TreeItem<ConfigContentViewModel> item) {
        Target target = new Target();
        ObservableList<TreeItem<ConfigContentViewModel>> list = item.getChildren();

        target.setTitle(list.get(0).getValue().getName());
        target.setProtocol("SMB");
        target.setIp(list.get(2).getValue().getName());
        target.setPort(list.get(3).getValue().getName());
        target.setTempDir(list.get(4).getValue().getName());
        target.setRootDir(list.get(5).getValue().getName());
        target.setUser(list.get(6).getValue().getName());
        target.setPassword(list.get(7).getValue().getName());
        return target;
    }

    public Target getSMBTargetFromTreeItem(TreeItem<ConfigContentViewModel> item) {
        Target target = new Target();
        ObservableList<TreeItem<ConfigContentViewModel>> list = item.getChildren();

        target.setTitle(list.get(0).getValue().getName());
        target.setProtocol("FTPS");
        target.setTempDir(list.get(2).getValue().getName());
        target.setRootDir(list.get(3).getValue().getName());
        target.setUser(list.get(4).getValue().getName());
        target.setPassword(list.get(5).getValue().getName());
        return target;
    }

    /**
     * Transfer, Receive 와 같은 Directory를 생성해주는 메소드
     *
     * @param itemName
     * @return
     */
    public TreeItem<ConfigContentViewModel> createSourceInfoDir(String itemName) {
        TreeItem<ConfigContentViewModel> treeItem =
                new TreeItem<>(new ConfigContentViewModel(
                        null, itemName, ConfigController.Role.REMOVEABLE));
        treeItem.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "sourceDir", "", ConfigController.Role.SELECTABLE)));
        treeItem.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "dataType", "", ConfigController.Role.SELECTABLE)));
        treeItem.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        null, "target", ConfigController.Role.ADDABLE_TARGET)));
        return treeItem;
    }

    /**
     * FPTS Protocol Target을 생성해주는 메소드
     *
     * @param itemName
     * @return
     */
    public TreeItem<ConfigContentViewModel> createTargetSMB(String itemName) {
        TreeItem<ConfigContentViewModel> treeItem =
                new TreeItem<>(new ConfigContentViewModel(
                        null, itemName, ConfigController.Role.REMOVEABLE));
        treeItem.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "title", "", ConfigController.Role.MODIFIABLE)));
        treeItem.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "protocol", "SMB", ConfigController.Role.SELECTABLE)));
        treeItem.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "tempDir", "", ConfigController.Role.MODIFIABLE)));
        treeItem.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "rootDir", "", ConfigController.Role.MODIFIABLE)));
        return treeItem;
    }

    /**
     * FPTS Protocol Target을 생성해주는 메소드
     *
     * @param itemName
     * @return
     */
    public TreeItem<ConfigContentViewModel> createTargetFTPS(String itemName) {
        TreeItem<ConfigContentViewModel> treeItem =
                new TreeItem<>(new ConfigContentViewModel(
                        null, itemName, ConfigController.Role.REMOVEABLE));
        treeItem.getChildren().add(new TreeItem<>(
                new ConfigContentViewModel(
                        "title", "", ConfigController.Role.MODIFIABLE)));
        treeItem.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "protocol", "FTPS", ConfigController.Role.SELECTABLE)));
        treeItem.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "ip", "", ConfigController.Role.MODIFIABLE)));
        treeItem.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel("" +
                        "port", "", ConfigController.Role.MODIFIABLE)));
        treeItem.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "tempDir", "", ConfigController.Role.MODIFIABLE)));
        treeItem.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "rootDir", "", ConfigController.Role.MODIFIABLE)));
        treeItem.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "user", "", ConfigController.Role.MODIFIABLE)));
        treeItem.getChildren().add(
                new TreeItem<>(new ConfigContentViewModel(
                        "password", "", ConfigController.Role.MODIFIABLE)));
        return treeItem;
    }
}