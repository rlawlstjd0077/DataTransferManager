package data;

import manager.JSONManager;
import org.junit.Test;
import util.DataType;

/**
 * Created by ykpark on 2017-06-03.
 */
public class ConfigGenerator {
    @Test
    public void generateConfig() {
//        JSONManager.parseJsonFile();
//        Config parsedConfig = Config.getConfigFile();
        Config config = new Config();

        Transfer transfer1 = new Transfer(); {
            transfer1.setSourceDir("c:/transmit/interface/GK2A_FDS");
            DataType dataType1 = DataType.ECEF_EPHMEREDES;
            transfer1.setDataType(dataType1.toString());
            transfer1.getTarget().add(generateTarget(
                    "ggo5-server-ftp",
                    "FTPS",
                    "ggo5",
                    "21",
                    "GK2A_ECEF_Ephemerides",
                    "c:/SOC/interface",
                    "ftp_nmsc",
                    "ftp_nmscpw"
            ));
        }
        config.getTransfer().add(transfer1);
        JSONManager.bindJsonFile(config);
    }
    private Target generateTarget(String title,
                                  String protocol,
                                  String ip,
                                  String port,
                                  String tempDir,
                                  String rootDir,
                                  String user,
                                  String password) {
        Target target = new Target();
        target.setTitle(title);
        target.setProtocol(protocol);
        target.setIp(ip);
        target.setPort(port);
        target.setTempDir(tempDir);
        target.setRootDir(rootDir);
        target.setUser(user);
        target.setPassword(password);
        return target;
    }
}
