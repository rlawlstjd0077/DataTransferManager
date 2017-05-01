package data;

import java.util.ArrayList;

/**
 * Transfer 데이터 클래스
 */
public class Transfer {
    private String sourceDir;
    private String dataType;
    private ArrayList<Target> target = new ArrayList<>();

    public String getSourceDir() {
        return sourceDir;
    }

    public String getDataType(){return dataType;}

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
