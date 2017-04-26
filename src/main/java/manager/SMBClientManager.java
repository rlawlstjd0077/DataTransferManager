package manager;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import java.io.IOException;

/**
 * SMB Client Manager 클래스
 */
public class SMBClientManager {
    public void doSend(String ip, String folderName) throws IOException {
        String path="smb://" + ip +"/"+ folderName +"/test.txt";
        SmbFile smbFile = new SmbFile(path);
        SmbFileOutputStream smbfos = new SmbFileOutputStream(smbFile);
        smbfos.write("testing....and writing to a file".getBytes());
        System.out.println("completed ...nice !");
    }
}
