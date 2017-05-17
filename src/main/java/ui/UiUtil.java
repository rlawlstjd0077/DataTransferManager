package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * Ui에 공통적으로 사용되는 기능을 제공
 */
public class UiUtil {

    public static FXMLLoader getFxmlLoader(Class<?> clazz) {
        FXMLLoader loader = new FXMLLoader();
        final String fxmlName = clazz.getSimpleName().replace("Controller", "") + ".fxml";
        loader.setLocation(clazz.getResource(fxmlName));
        return loader;
    }

    public static FXMLLoader getFxmlLoader(Class<?> clazz, String name) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(clazz.getResource(name));
        return loader;
    }


    /**
     * FXML을  이름을 기반으로 추출하여 읽고 등록.
     *
     * @param controller  인스턴스.
     * @throws IOException 파일이 없을 때 발생.
     */
    public static void loadFxml(Parent controller) throws IOException {
        final FXMLLoader loader = getFxmlLoader(controller.getClass());
        loader.setRoot(controller);
        loader.setController(controller);
        loader.load();
    }
}
