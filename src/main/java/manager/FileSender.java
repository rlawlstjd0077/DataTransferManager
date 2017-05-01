package manager;


import data.Config;
import data.Receive;
import data.Target;
import data.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 감지된 폴더명과 일치하는 Target 들에게 File을 send 하는 클래스
 */
public class FileSender implements Runnable{
    private String filePath;
    private String folderName;
    private boolean state;
    private static final Logger logger = LoggerFactory.getLogger(FileSender.class);

    /**
     * File Sender 생성자
     * @param filePath
     * @param folderName
     * @param state : true : transfer, false : receive
     */
    public FileSender(String filePath, String folderName, boolean state){
        this.filePath = filePath;
        this.folderName = folderName;
        this.state = state;
    }
    @Override
    public void run() {
        if(state){
            ArrayList<Transfer> folderList = Config.configFile.getTransfer();
            for(Transfer transfer : folderList){
                if(getFolderNameFromPath(transfer.getSourceDir()).equals(folderName)){
                    doSend(transfer.getTarget());
                }
            }
        }else{
            ArrayList<Receive> folderList  = Config.configFile.getReceive();
            for(Receive receive : folderList){
                if(getFolderNameFromPath(receive.getSourceDir()).equals(folderName)){
                    doSend(receive.getTarget());
                }
            }
        }
    }

    public String getFolderNameFromPath(String path){
        String[] splitList = path.split("/");
        return splitList[splitList.length - 1];
    }

    public void doSend(ArrayList<Target> targetList){
        FTPSClientManager ftpsClient = new FTPSClientManager();
        SMBClientManager smbClient = new SMBClientManager();
        int failCount = 0;
        File file = new File(filePath);

        for(int i = 0; i < targetList.size(); i++){
            Target target = targetList.get(i);
            if(target.getProtocol().equals("FTPS")){
                try {
                    ftpsClient.doSend(target.getIp(), target.getPort(), target.getUser(),
                            target.getPassword(), file, file.getName(), target.getTempDir(), target.getRootDir());
                    logger.debug("FTPS File Send Success To " + target.getTitle());
                } catch (IOException e) {
                    if(failCount >= 3){
                        logger.error("FTPS File Send Failed To " + target.getTitle());
                    }else {
                        i--;
                        failCount++;
                        continue;
                    }
                }
                failCount = 0;
            }else{
                try{
                    smbClient.doSend(target.getIp(), target.getRootDir());
                    logger.debug("SMB File Send Success To " + target.getTitle());
                }catch (IOException e) {
                    if(failCount >= 3){
                        logger.error("SMB File Send Failed To " + target.getTitle());
                    }else {
                        i--;
                        failCount++;
                        continue;
                    }
                }
                failCount = 0;
            }
        }
    }
}