package manager;

import com.google.gson.Gson;
import data.Config;

import java.io.*;

/**
 * JSON 관련 처리 기능을 담당하는 클래스
 */
public class JSONManager {
    public static String parseJsonFile() {
        String result = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File("Data/Config/config.txt")));

            String tmp;
            int data = 0;

            while ((tmp = in.readLine()) != null) {
                result += tmp;
            }

            Config.configFile = new Gson().fromJson(result, Config.class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void bindJsonFile(Config config) {
        try {
            //json 파일 초기화
            PrintWriter writer = new PrintWriter(new File("Data/Config/config.txt"));
            writer.print("");
            writer.close();

            String json = new Gson().toJson(config);
            BufferedWriter out = new BufferedWriter(new FileWriter(new File("Data/Config/config.txt")));
            out.write(json);
            out.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
