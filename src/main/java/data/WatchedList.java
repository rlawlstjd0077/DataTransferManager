package data;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by dsm_025 on 2017-04-20.
 */
public class WatchedList {
    public static ArrayList<String> transmitFolders = new ArrayList<>(Arrays.asList(
            "Data/Transmit/interface/fd",
            "Data/Transmit/interface/mp"
    ));

    public static ArrayList<String> recieveFolders = new ArrayList<>(Arrays.asList(
            "Data/Receive/interface/es",
                "Data/Receive/interface/ka",
                "Data/Receive/interface/ko",
                "Data/Receive/interface/le",
                "Data/Receive/interface/ov"
    ));

    /**
     * folderName과 일치하는 폴더를 반환하는 메소드
     * @param folderName
     * @param state : true = transmit, false = receive
     * @return
     */
    public static String findEqualFolder(String folderName, boolean state){
        folderName = folderName.split("/")[1].toLowerCase();
        for(String exist : state ? transmitFolders : recieveFolders){
            if(exist.split("/")[3].equals(folderName)){
                return exist;
            }
        }
        return null;
    }

    public static String addToTrasmitFolder(String folderName){
        String newFolderPath  = "Data/Transmit/interface/" + folderName.split("/")[3].toLowerCase();
        transmitFolders.add(newFolderPath);
        return newFolderPath;
    }
    public static String addToReceiveFolder(String folderName){
        String newFolderPath  = "Data/Receive/interface/" + folderName.split("/")[3].toLowerCase();
        transmitFolders.add(newFolderPath);
        return newFolderPath;
    }
}
