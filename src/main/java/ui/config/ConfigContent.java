package ui.config;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by dsm_025 on 2017-04-24.
 */
class ConfigContent {
    private final SimpleStringProperty tag;
    private final SimpleStringProperty name;
    private final SimpleObjectProperty<ConfigController.Role> role;

    public ConfigContent(String tag, String name, ConfigController.Role role) {
        this.tag = new SimpleStringProperty(tag);
        this.name = new SimpleStringProperty(name);
        this.role = new SimpleObjectProperty<ConfigController.Role>(role);
    }

    public String getTag() {
        return tag.get();
    }

    public void setTag(String tag) {
        this.tag.set(tag);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public ConfigController.Role getRole() {
        return role.get();
    }

    public void setRole(ConfigController.Role role) {
        this.role.set(role);
    }
}
