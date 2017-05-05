package manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DataType;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;

/**
 * Directory 감시하기 위한 클래스
 */
public class FolderObserver implements Runnable {
    private String dirPath;
    private boolean state;
    private String folderName;
    private String folderPath;
    private static final Logger logger = LoggerFactory.getLogger(FolderObserver.class);
    private final String RECEIVE_DATA_FOLDER = "Data/Receive/Data";
    private final String TRANSMIT_DATA_FOLDER = "Data/Transmit/Data";
    /**
     * 생성자에서 감시할 폴더의 정보를 준다.
     *
     * @param dirPath : 폴더 경로
     * @param state   : true (Transmit), false(Receive)
     */
    public FolderObserver(String dirPath, boolean state) {
        this.dirPath = dirPath;
        this.state = state;
    }

    @Override
    public void run() {
        WatchService myWatchService = null;
        WatchKey watchKey = null;
        try {
            myWatchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(dirPath);
            watchKey = path.register(myWatchService,
                    StandardWatchEventKinds.ENTRY_CREATE);
            folderName = path.getFileName().toString(); // 폴더명만 리턴
            folderPath = new File(String.valueOf(path)).getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!Thread.currentThread().isInterrupted()) {
           for (WatchEvent event : watchKey.pollEvents()) {
                logger.info("Change State at : " + folderPath);
                DataType type = DataType.fromFilename(event.context().toString());
                if (state) {      //transmit 폴더 일 경우
                    new File(TRANSMIT_DATA_FOLDER + "/" + folderName + "/" +  type ).mkdirs();
                    String dataFolderPath = TRANSMIT_DATA_FOLDER + "/" + folderName + "/" +  type + "/" + event.context();
                    moveFileToData(folderPath + "/" + event.context(), dataFolderPath);
                    FileSender transmitSender = new FileSender(dataFolderPath, folderName, true);
                    new Thread(transmitSender).start();
                } else {        //receive 폴더 일 경우
                    new File(RECEIVE_DATA_FOLDER + "/" + folderName + "/" +  type ).mkdirs();
                    String dataFolderPath = RECEIVE_DATA_FOLDER + "/" + folderName + "/" + type + "/" + event.context();
                    moveFileToData(folderPath + "/" + event.context(),
                            dataFolderPath);
                    FileSender receiveSender = new FileSender(dataFolderPath, folderName, false);
                    new Thread(receiveSender).start();
                }
            }
            if (!watchKey.reset()) {
                break;
            }
        }
    }

    /**
     * Data dir로 파일을 Move 하는 메소드
     * @param filePath
     * @param dataDirPath
     */
    public void moveFileToData(String filePath, String dataDirPath) {
        try {
            Files.move(Paths.get(filePath), Paths.get(dataDirPath));
        } catch (IOException e) {
            logger.error("File Move Failed in Local");
            return;
        }
        logger.info("File Move Succeed in Local");
    }
}