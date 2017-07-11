package manager;

import data.*;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DataType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 감지된 폴더명과 일치하는 Target 들에게 File을 send 하는 클래스
 */
public class FileSender implements Runnable {
    private String filePath; // Local Saved Fila Path
    private String folderPath; // Monitoring Target File Path
    private String folderName; // Monitroing Target File Name
    private String fileName; // Local Saved File Name
    private boolean state;
    private static final Logger logger =
            LoggerFactory.getLogger(FileSender.class);

    /**
     * File Sender 생성자
     * @param folderPath : 폴더 경로
     * @param folderName : 폴더 명
     * @param fileName : 복사 대상 파일이름
     * @param state      : true (transfer), false : (receive)
     */
    public FileSender(String folderPath, String folderName, String fileName, boolean state) {
        this.folderPath = folderPath;
        this.folderName = folderName;
        this.fileName = fileName;
        this.state = state;
        this.filePath = setLocalBackUpFolderPath();
    }

    @Override
    public void run() {
        moveFiles();
        sendFiles();
    }

    private void sendFiles() {
        if (state) {          //transfer 일 경우
            ArrayList<Transfer> folderList = Config.getConfigFile().getTransfer();
            for (Transfer transfer : folderList) {
                if (FilenameUtils.getBaseName(transfer.getSourceDir()).equals(folderName) &&
                        transfer.getDataType().equals(DataType.fromFilename(fileName).toString())) {
                    doSend(transfer.getTarget());
                }
            }
        } else {              //receive 일 경우
            ArrayList<Receive> folderList = Config.getConfigFile().getReceive();
            for (Receive receive : folderList) {
                if (FilenameUtils.getBaseName(receive.getSourceDir()).equals(folderName) &&
                        receive.getDataType().equals(DataType.fromFilename(fileName).toString())) {
                    doSend(receive.getTarget());
                }
            }
        }
    }

    private void moveFiles() {
        String dataFilePath = setLocalBackUpFolderPath();

        if (new File(dataFilePath).exists()) {        //동일한 파일이 있는 경우
            new File(dataFilePath).renameTo(new File(FileMoveManager.getValidDuplicateFile(new File(dataFilePath))));
        }
        FileMoveManager.moveFileToData(folderPath + "/" + fileName, dataFilePath);
    }

    private String setLocalBackUpFolderPath() {
        DataType type = DataType.fromFilename(fileName);

        File dataFolder;
        if (state) {
            dataFolder = new File(FileMoveManager.TRANSMIT_DATA_FOLDER + "/"
                    + folderName + "/" + type);
        } else {
            dataFolder = new File(FileMoveManager.RECEIVE_DATA_FOLDER + "/"
                    + folderName + "/" + type);
        }
        dataFolder.mkdirs();
        return dataFolder.getPath() + "/" + fileName;
    }

    /**
     * 해당 sourceDir의 target 들에게 file을 send 하는 메소드
     *
     * @param targetList
     */
    public void doSend(ArrayList<Target> targetList) {
        FTPSClientManager ftpsClient = new FTPSClientManager();
        SMBClientManager smbClient = new SMBClientManager();
        int failCount = 0;
        File file = new File(filePath);

        for (int i = 0; i < targetList.size(); i++) {
            Target target = targetList.get(i);
            if (target.getProtocol().equals("FTPS")) {
                try {
                    ftpsClient.doSend(
                            target.getIp(),
                            target.getPort(),
                            target.getUser(),
                            target.getPassword(),
                            file,
                            file.getName(),
                            target.getTempDir(),
                            target.getRootDir());
                    logger.info("Success to SEND 'FTPS File' - " + file.getName()
                            + " to " + target.getTitle());
                } catch (IOException e) {
                    if (failCount >= Setting.FILE_SEND_FAIL_LIMIT) {
                        logger.error("Fail to send 'FTPS File' - " + file.getName() + " to "
                                + target.getTitle() + " cased by " + e.toString());
                    } else {
                        i--;
                        failCount++;
                        logger.warn("Retry to send 'FTPS File' : Count " + failCount + " - "
                                + file.getName() + " to " + target.getTitle());
                        continue;
                    }
                }
                failCount = 0;
            } else {
                try {
                    smbClient.doSend(
                            file,
                            target.getTempDir(),
                            target.getRootDir(),
                            file.getName(),
                            target.getUser(),
                            target.getPassword());
                    logger.info("Success to SEND 'SMB File' - " + file.getName()
                            + " to " + target.getTitle());
                } catch (Exception e) {
                    if (failCount >= Setting.FILE_SEND_FAIL_LIMIT) {
                        logger.error("Fail to send 'SMB File' - " + file.getName()
                                + " to " + target.getTitle() + " cased by " + e.getMessage());
                    } else {
                        i--;
                        failCount++;
                        logger.warn("Retry to send 'SMB File' : Count " + failCount
                                + " - " + file.getName() + " to " + target.getTitle());
                        continue;
                    }
                }
                failCount = 0;
            }
        }
    }
}