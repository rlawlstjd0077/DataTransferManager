package data;

import java.util.ArrayList;

/**
 * Created by dsm_025 on 2017-04-16.
 */
public class Receive {
    private String sourceDir;
    private String dataType;
    private ArrayList<Target> target = new ArrayList<>();

    public String getSourceDir() {
        return sourceDir;
    }

    public String getDataType() {
        return dataType;
    }

    public ArrayList<Target> getTarget() {
        return target;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void setTarget(ArrayList<Target> target) {
        this.target = target;
    }
}
