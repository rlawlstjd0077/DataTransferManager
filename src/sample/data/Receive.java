package sample.data;

import java.util.ArrayList;

/**
 * Created by dsm_025 on 2017-04-16.
 */
public class Receive {
    private String sourceDir;
    private ArrayList<Target> target = new ArrayList<>();

    public String getSourceDir() {
        return sourceDir;
    }

    public ArrayList<Target> getTarget() {
        return target;
    }
}
