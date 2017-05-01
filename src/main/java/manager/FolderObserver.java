package manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DataType;

import java.io.File;
import java.io.IOException;
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
            Path path = Paths.get(dirPath); // Get the directory to be monitored
            watchKey = path.register(myWatchService,
                    StandardWatchEventKinds.ENTRY_CREATE); // Register the directory
            folderName = path.getFileName().toString(); // 폴더명만 리턴
            folderPath = new File(String.valueOf(path)).getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!Thread.currentThread().isInterrupted()) {
            for (WatchEvent event : watchKey.pollEvents()) {
                DataType type = DataType.fromFilename(event.context().toString());
                new File(RECEIVE_DATA_FOLDER + "/" + folderName + "/" +  type ).mkdir();
                new File(TRANSMIT_DATA_FOLDER + "/" + folderName + "/" +  type ).mkdir();
                if (state) {      //receive 폴더 일 경우
                    FileSender transmitSender = new FileSender(folderName + "/" + event.context(), folderName, true);
                    new Thread(transmitSender).start();
                    moveFileToData(folderPath + "/" + event.context(),
                            RECEIVE_DATA_FOLDER + "/" + folderName + "/" +  type + "/" + event.context());
                } else {
                    FileSender receiveSender = new FileSender(folderName + "/" + event.context(), folderName, true);
                    new Thread(receiveSender).start();
                    moveFileToData(folderPath + "/" + event.context(),
                            RECEIVE_DATA_FOLDER + "/" + folderName + "/" + type + "/" + event.context());
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
        Path file = Paths.get(filePath);
        Path movePath = Paths.get(dataDirPath);
        try {
            Files.move(file, movePath);
        } catch (IOException e) {
            logger.error("File Move Failed");
        }
    }
}