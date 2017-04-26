package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 400, 275));
        primaryStage.show();

//        monitoringDir("C:/Users/dsm_025/Downloads");
    }

    private void monitoringDir(String s) {
        WatchService myWatchService = null;
        WatchKey watchKey = null;
        try {
            myWatchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(s); // Get the directory to be monitored
            watchKey = path.register(myWatchService,
                    StandardWatchEventKinds.ENTRY_CREATE); // Register the directory

        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            //변화가 감지되는 경우 이벤트 종류와 파일명을 출력
            for (WatchEvent event : watchKey.pollEvents()) {
                System.out.println(event.kind() + ": " + event.context());
                File file = new File(s + "/" + event.context());
                System.out.println(file.getName());
            }
            if (!watchKey.reset()) {
                break; // 디렉토리등이 삭제되는 경우
            }
        }
    }

    static void copyFile(String inPath, String outPath){
        try{
            FileInputStream inputStream = new FileInputStream(inPath);
            FileOutputStream outputStream = new FileOutputStream(outPath);
            int data = 0;
            while((data = inputStream.read()) != -1){
                System.out.println(data);
                outputStream.write(data);
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        launch(args);
    }
}
