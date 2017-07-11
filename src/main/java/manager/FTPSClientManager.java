package manager;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * FTPS Client Manager 클래스
 */
public class FTPSClientManager {
    public void doSend(final String url,
                       final String port,
                       final String user,
                       final String password,
                       final File file,
                       final String fileName,
                       final String tempDir,
                       final String rootDir) throws IOException {
        String remoteTmp = "/" + tempDir + "/" + fileName;
        String remoteRoot = "/" + rootDir + "/" + fileName;
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
        client.mkd("/" + tempDir);
        client.mkd("/" + rootDir);
        client.storeFile(remoteTmp, input);
        client.rename(remoteTmp, remoteRoot);
        client.logout();
        client.disconnect();
        input.close();
    }
}
