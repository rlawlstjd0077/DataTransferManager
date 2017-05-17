package manager;

import data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 감지된 폴더명과 일치하는 Target 들에게 File을 send 하는 클래스
 */
public class FileSender implements Runnable {
    private String filePath;
    private String folderName;
    private boolean state;
    private static final Logger logger =
            LoggerFactory.getLogger(FileSender.class);

    /**
     * File Sender 생성자
     *
     * @param filePath : file Path
     * @param folderName : 폴더 명
     * @param state      : true (transfer), false : (receive)
     */
    public FileSender(String filePath, String folderName, boolean state) {
        this.filePath = filePath;
        this.folderName = folderName;
        this.state = state;
    }

    @Override
    public void run() {
        if (state) {          //transfer 일 경우
            ArrayList<Transfer> folderList =
                    Config.getConfigFile().getTransfer();
            for (Transfer transfer : folderList) {
                if (getFolderNameFromPath(transfer.getSourceDir()).equals(folderName)) {
                    doSend(transfer.getTarget());
                }
            }
        } else {              //receive 일 경우
            ArrayList<Receive> folderList =
                    Config.getConfigFile().getReceive();
            for (Receive receive : folderList) {
                if (getFolderNameFromPath(receive.getSourceDir()).equals(folderName)) {
                    doSend(receive.getTarget());
                }
            }
        }
    }

    /**
     * Path로 부터 Folder 명을 가져오는 메소드
     * @param path
     * @return
     */
    public String getFolderNameFromPath(String path) {
        String[] splitList = path.split("/");
        return splitList[splitList.length - 1].toLowerCase();
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
                } catch (IOException e) {
                    if (failCount >= Setting.FILE_SEND_FAIL_LIMIT) {
                        logger.error("Fail to send 'SMB File' - " + file.getName()
                                + " to " + target.getTitle() + " cased by " + e.toString());
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