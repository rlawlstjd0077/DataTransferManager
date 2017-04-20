package sample.manager;

import org.apache.commons.net.ftp.FTPSClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dsm_025 on 2017-04-19.
 */
public class FTPSClientManager {
    public void doSend(String url, int port, String user, String password, File file, String fileName){
        String remote = fileName;
        FTPSClient client = new FTPSClient();
        try {
            InputStream input = new FileInputStream(file);
            client.connect(url + ":" + port);
            client.login(user, password);
            client.storeFile(remote, input);
            client.logout();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
