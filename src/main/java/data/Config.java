package data;

import java.util.ArrayList;

/**
 * Config 파일 클래스
 */
public class Config {
    public static Config configFile;
    private String dataDir;
    private ArrayList<Transfer> transfer = new ArrayList<>();
    private ArrayList<Receive> receive = new ArrayList<>();

    public String getDataDir() {
        return dataDir;
    }

    public ArrayList<Transfer> getTransfer() {
        return transfer;
    }

    public ArrayList<Receive> getReceive() {
        return receive;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public void setTransfer(ArrayList<Transfer> transfer) {
        this.transfer = transfer;
    }

    public void setReceive(ArrayList<Receive> receive) {
        this.receive = receive;
    }
}
