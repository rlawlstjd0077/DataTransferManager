package manager;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * SMB Client Manager 클래스
 */
public class SMBClientManager {
    public void doSend(final File file,
                       final String tmp,
                       final String root,
                       final String fileName,
                       final String id,
                       final String pw) throws IOException {
        final String copyPath = "smb:" + tmp.replaceAll("\\\\", "/") + "/" + fileName;
        final String movePath = "smb:" + root.replaceAll("\\\\", "/") + "/" + fileName;
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, id, pw);
        SmbFile copyFile = new SmbFile(copyPath, auth);
        SmbFile copyParentFile = new SmbFile(copyFile.getParent(), auth);
        if (!copyParentFile.exists()) {
            copyParentFile.mkdirs();
        }

        SmbFileOutputStream smbfos = new SmbFileOutputStream(copyFile);
        smbfos.write(readFileContent(file).getBytes());
        smbfos.close();

        SmbFile moveFile = new SmbFile(movePath, auth);
        SmbFile moveParentFile = new SmbFile(moveFile.getParent(), auth);
        if (!moveParentFile.exists()) {
            moveParentFile.mkdirs();
        }

        copyFile.renameTo(moveFile);
    }

    private String readFileContent(final File file) throws IOException {
        String result = "";
        String tmp;
        BufferedReader in = new BufferedReader(new FileReader(file));

        while ((tmp = in.readLine()) != null) {
            result += tmp;
        }
        return result;
    }
}
