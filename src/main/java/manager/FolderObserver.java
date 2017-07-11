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
                    StandardWatchEventKinds.ENTRY_MODIFY);
            folderName = path.getFileName().toString(); // 폴더명만 리턴
        } catch (IOException e) {
        }

        while (!Thread.currentThread().isInterrupted()) {
            for (WatchEvent event : watchKey.pollEvents()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }

                if(!Files.isReadable(Paths.get(dirPath + "/" + event.context()))){
                    continue;
                }
                logger.debug("Create " + event.context()
                        + " file in " + dirPath);
                if (state) {      //transmit 폴더 일 경우
                    FileSender transmitSender =
                            new FileSender(dirPath, folderName, event.context().toString(), true);
                    new Thread(transmitSender).start();
                } else {        //receive 폴더 일 경우
                    FileSender receiveSender =
                            new FileSender(dirPath, folderName, event.context().toString(), false);
                    new Thread(receiveSender).start();
                }
            }
            if (!watchKey.reset()) {
                break;
            }
        }
    }
}