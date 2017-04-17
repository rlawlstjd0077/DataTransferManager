package sample.data;

import java.util.ArrayList;

/**
 * Created by dsm_025 on 2017-04-16.
 */
public class Config {
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
}
