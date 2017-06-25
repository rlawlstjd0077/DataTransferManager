package manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DataType;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

/**
 * Directory 감시하는 기능을 수행하는 클래스
 */
public class FolderObserver implements Runnable {
    private String dirPath;
    private boolean state;
    private String folderName;
    private static final Logger logger =
            LoggerFactory.getLogger(FolderObserver.class);

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
        WatchService myWatchService;
        WatchKey watchKey = null;
        try {
            myWatchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(dirPath);
            watchKey = path.register(myWatchService,
                    StandardWatchEventKinds.ENTRY_CREATE);
            folderName = path.getFileName().toString(); // 폴더명만 리턴
        } catch (IOException e) {
        }

        while (!Thread.currentThread().isInterrupted()) {
            for (WatchEvent event : watchKey.pollEvents()) {
                logger.debug("Create " + event.context()
                        + " file in " + dirPath);
                DataType type = DataType.fromFilename(event.context().toString());
                if (state) {      //transmit 폴더 일 경우
                    File dataFolder = new File(FileMoveManager.TRANSMIT_DATA_FOLDER + "/"
                            + folderName + "/" + type);
                    dataFolder.mkdirs();
                    String dataFilePath = dataFolder.getPath() + "/" + event.context();

                    if (new File(dataFilePath).exists()) {        //동일한 파일이 있는 경우
                        new File(dataFilePath).renameTo(new File(FileMoveManager.getValidDuplicateFile(new File(dataFilePath))));
                    }

                    FileMoveManager.moveFileToData(dirPath
                            + "/" + event.context(), dataFilePath);
                    FileSender transmitSender =
                            new FileSender(dataFilePath, folderName, true);
                    new Thread(transmitSender).start();
                } else {        //receive 폴더 일 경우
                    File dataFolder = new File(FileMoveManager.RECEIVE_DATA_FOLDER + "/"
                            + folderName + "/" + type);
                    dataFolder.mkdirs();
                    String dataFilePath = dataFolder.getPath() + "/" +  event.context();

                    if (new File(dataFilePath).exists()) {        //동일한 파일이 있는 경우
                        new File(dataFilePath).renameTo(new File(FileMoveManager.getValidDuplicateFile(new File(dataFilePath))));
                    }

                    FileMoveManager.moveFileToData(dirPath + "/"
                            + event.context(), dataFilePath);
                    FileSender receiveSender =
                            new FileSender(dataFilePath, folderName, false);
                    new Thread(receiveSender).start();
                }
            }
            if (!watchKey.reset()) {
                break;
            }
        }
    }
}