package manager;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import java.io.*;

/**
 * SMB Client Manager 클래스
 */
public class SMBClientManager {
    public void doSend(String ip, File file, String tmp, String root, String fileName) throws IOException {
        String copyPath ="smb://" + ip +"/" + tmp + "/" + fileName;
        String movePath ="smb://" + ip +"/" + root + "/" + fileName;
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, null, null);     //everyone으로 접속
        SmbFile copyFile = new SmbFile(copyPath, auth);
        SmbFileOutputStream smbfos = new SmbFileOutputStream(copyFile);
        smbfos.write(readFileContent(file).getBytes());

        SmbFile moveFile = new SmbFile(movePath, auth);
        copyFile.renameTo(moveFile);
    }

    private String readFileContent(File file) throws IOException {
        String result = "";
        String tmp;
        BufferedReader in = new BufferedReader(new FileReader(file));

        int data = 0;

        while ((tmp = in.readLine()) != null) {
            result += tmp;
        }
        return result;
    }
}
