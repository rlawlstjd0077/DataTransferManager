package manager;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;

/**
 * FTPS Client Manager 클래스
 */
public class FTPSClientManager {
    public void doSend(String url, String port, String user, String password, File file, String fileName, String folderName) throws IOException {
        String remote = fileName;
        FTPSClient client = new FTPSClient();
        client.setAuthValue("TLS");
        client.setRemoteVerificationEnabled(false);

        InputStream input = new FileInputStream(file);
        client.connect(url, Integer.parseInt(port));
        client.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());

        client.login(user, password);
        client.setBufferSize(1000);
        client.enterLocalPassiveMode();
        client.setFileType(FTP.BINARY_FILE_TYPE);
        client.execPBSZ(0);
        client.execPROT("P");
        client.enterLocalPassiveMode();
        client.storeFile(remote, input);
        client.logout();
    }
}
