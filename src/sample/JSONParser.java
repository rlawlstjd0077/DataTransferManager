package sample;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import sample.data.Config;
import sample.data.TestJSONData;
import sample.data.Transfer;

/**
 * Created by dsm_025 on 2017-04-16.
 */
public class JSONParser {
    public void jsonToConfig(){
        Gson gson = new Gson();
        Config config = new Gson().fromJson(TestJSONData.JSON_DATA, Config.class);
    }
}
