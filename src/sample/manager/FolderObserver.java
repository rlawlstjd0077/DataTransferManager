package sample.manager;

import java.io.IOException;
import java.nio.file.*;

/**
 * Directory를 감시하기 위한 Thread 클래스
 */
public class FolderObserver implements Runnable {
    private String dirPath;

    /**
     * 생성자에서 감시할 폴더의 정보를 준다.
     * @param dirPath : 폴더 경로
     * @param state : true (Recieve), false(Transmit)
     */
    public FolderObserver(String dirPath, boolean state){
        this.dirPath = dirPath;
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

        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            //변화가 감지되는 경우 이벤트 종류와 파일명을 출력
            for (WatchEvent event : watchKey.pollEvents()) {
                System.out.println(event.kind() + ": " + event.context());
            }
            if (!watchKey.reset()) {
                break; // 디렉토리등이 삭제되는 경우
            }
        }
    }
}
