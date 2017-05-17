package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import data.Config;

import java.io.*;

/**
 * JSON 관련 처리 기능을 담당하는 클래스
 */
public class JSONManager {
    /**
     * JsonFile txt 파일을 읽어와 Config 객체로 저장하는 클래스
     * @return
     */
    public static String parseJsonFile() {
        String result = "";
        try {
            BufferedReader in =
                    new BufferedReader(new FileReader(new File("Data/Config/config.txt")));

            String tmp;
            while ((tmp = in.readLine()) != null) {
                result += tmp;
            }

            Config.setConfigFile(new Gson().fromJson(result, Config.class));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Config 객체로 부터 Json 파일로 변환하여 저장하는 메소드
     * @param config
     */
    public static void bindJsonFile(Config config) {
        try {
            //json 파일 초기화
            PrintWriter writer = new PrintWriter(new File("Data/Config/config.txt"));
            writer.print("");
            writer.close();

            final GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            final Gson gson = builder.create();
            String json = gson.toJson(config);
            BufferedWriter out =
                    new BufferedWriter(new FileWriter(new File("Data/Config/config.txt")));
            out.write(json);
            out.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
