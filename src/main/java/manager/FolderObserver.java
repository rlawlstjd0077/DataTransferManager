package manager;

import java.io.IOException;
import java.nio.file.*;

/**
 * Directory 감시하기 위한 Thread 클래스
 */
public class FolderObserver implements Runnable {
    private String dirPath;
    private boolean state;
    private String folderName;

    /**
     * 생성자에서 감시할 폴더의 정보를 준다.
     * @param dirPath : 폴더 경로
     * @param state : true (Transmit), false(Receive)
     */
    public FolderObserver(String dirPath, boolean state){
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
            folderName = path.getFileName().toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            for (WatchEvent event : watchKey.pollEvents()) {
                System.out.println(event.kind() + ": " + event.context());
                //TODO event Context가 무슨 값인지 확인 필요
                if(state){      //receive 폴더 일 경우
                    FileSender transmitSender = new FileSender(folderName + "/" + event.context(), folderName, true);
                    new Thread(transmitSender).start();
                }else{
                    FileSender receiveSender = new FileSender(folderName + "/" + event.context(), folderName, true);
                    new Thread(receiveSender).start();
                }
            }
            if (!watchKey.reset()) {
                break;
            }
        }
    }
}