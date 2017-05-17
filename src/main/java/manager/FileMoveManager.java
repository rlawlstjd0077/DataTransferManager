package manager;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Local 에서 파일을 이동할 때 필요한 기능을 제공하는 클래스
 */
public class FileMoveManager {
    public static final String RECEIVE_DATA_FOLDER = "Data/Receive/Data";
    public static final String TRANSMIT_DATA_FOLDER = "Data/Transmit/Data";
    private static final Logger logger =
            LoggerFactory.getLogger(FileMoveManager.class);

    /**
     * Data dir로 파일을 Move 하는 메소드
     *
     * @param originFilePath : 파일의 원시 주소
     * @param dataFilePath   : 파일의 이동 주소
     */
    public static void moveFileToData(
            final String originFilePath,
            final String dataFilePath) {
        try {
            final Path sourcePath = Paths.get(originFilePath);
            final Path targetPath = Paths.get(dataFilePath);

            final File parentFile = targetPath.toFile().getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }

            Files.move(sourcePath, targetPath);
        } catch (IOException e) {
            logger.error("Fail to Move " + new File(originFilePath).getName()
                    + " File in local caused by " + e.toString());
            return;
        }
        logger.info("Success to Move  "
                + new File(dataFilePath).getName() + " File in local");
    }

    /**
     * Data 폴더에 동일한 파일명이 있는 경우 수정가능한 파일 이름을 찾아주는 메소드
     *
     * @param file
     * @return : 가능한 FilePath
     */
    public static String getValidDuplicateFile(final File file) {
        int count = 1;
        String newName;
        for (File temp : file.getParentFile().listFiles()) {
            if (FilenameUtils.removeExtension(temp.getName()).equals(file.getName())) {
                int num = Integer.parseInt(FilenameUtils.getExtension(temp.getName()));
                num++;
                count = (count < num) ? num : count;
            }
        }
        newName = file.getPath() + "." + String.format("%04d", count);
        return newName;
    }
}
