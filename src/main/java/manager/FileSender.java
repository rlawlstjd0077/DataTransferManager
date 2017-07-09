package manager;

import data.*;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DataType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

/**
 * 감지된 폴더명과 일치하는 Target 들에게 File을 send 하는 클래스
 */
public class FileSender implements Runnable {
    private File file;
    private String folderPath;
    private String folderName;
    private String fileName;
    private boolean state;
    private static final Logger logger =
            LoggerFactory.getLogger(FileSender.class);

    /**
     * File Sender 생성자
     *
     * @param folderPath   : file Path
     * @param folderName : 폴더 명
     * @param state      : true (transfer), false : (receive)
     */
    public FileSender(String folderPath, String folderName, String fileName, boolean state) {
        this.folderPath = folderPath;
        this.file = new File(folderPath);
        this.folderName = folderName;
        this.fileName = fileName;
        this.state = state;
    }

    @Override
    public void run() {
        moveFiles();
        sendFiles();
        try {
            Files.delete(Paths.get(folderPath + "/" + fileName));
        } catch (IOException e) {
        }
    }

    private void sendFiles() {
        if (state) {          //transfer 일 경우
            ArrayList<Transfer> folderList =
                    Config.getConfigFile().getTransfer();
            for (Transfer transfer : folderList) {
                if (FilenameUtils.getBaseName(transfer.getSourceDir()).equals(folderName)) {
                    doSend(transfer.getTarget());
                }
            }
        } else {              //receive 일 경우
            ArrayList<Receive> folderList =
                    Config.getConfigFile().getReceive();
            for (Receive receive : folderList) {
                if (FilenameUtils.getBaseName(receive.getSourceDir()).equals(folderName)) {
                    doSend(receive.getTarget());
                }
            }
        }
    }

    private void moveFiles() {
        DataType type = DataType.fromFilename(file.getName());
        File dataFolder;
        if (state) {
            dataFolder = new File(FileMoveManager.TRANSMIT_DATA_FOLDER + "/"
                    + folderName + "/" + type);
        } else {
            dataFolder = new File(FileMoveManager.RECEIVE_DATA_FOLDER + "/"
                    + folderName + "/" + type);
        }

        dataFolder.mkdirs();
        String dataFilePath = dataFolder.getPath() + "/" + fileName;

        if (new File(dataFilePath).exists()) {        //동일한 파일이 있는 경우
            new File(dataFilePath).renameTo(new File(FileMoveManager.getValidDuplicateFile(new File(dataFilePath))));
        }
        FileMoveManager.moveFileToData(folderPath + "/" + fileName, dataFilePath);
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
        File file = new File(folderPath);

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