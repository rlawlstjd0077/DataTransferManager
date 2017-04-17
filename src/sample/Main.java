package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        JSONParser parser = new JSONParser();
        parser.jsonToConfig();
//        monitoringDir("C:/Users/dsm_025/Downloads");

    }

    static void monitoringDir(String dir) throws IOException, InterruptedException {
        WatchService myWatchService = FileSystems.getDefault().newWatchService();

        //모니터링을 원하는 디렉토리 Path를 얻는다.
        Path path = Paths.get(dir); // Get the directory to be monitored


        //모니터링 서비스를 할 path에 의해 파일로케이션을 등록
        WatchKey watchKey = path.register(myWatchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE); // Register the directory

        while (true) {
            //변화가 감지되는 경우 이벤트 종류와 파일명을 출력
            for (WatchEvent event : watchKey.pollEvents()) {
                System.out.println(event.kind() + ": " + event.context());
                copyFile("C:/Users/dsm_025/Downloads/" + event.context(), "C:/Users/dsm_025/Desktop/temp/" + event.context());
            }

            if (!watchKey.reset()) {
                break; // 디렉토리등이 삭제되는 경우
            }
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        launch(args);
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
}
