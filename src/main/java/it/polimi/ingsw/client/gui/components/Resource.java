package it.polimi.ingsw.client.gui.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.Objects;

public class Resource extends ImageView {
    private String name;

    public Resource() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/resource.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setResourceType(String resourceName) {
        this.name = resourceName;
        try {
            Image bg = new Image(Objects.requireNonNull(getClass().getResource(String.format("/assets/gui/resourcetypes/%s.png", resourceName.toLowerCase()))).toExternalForm());
            this.setImage(bg);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("No image file for resource '%s'.", resourceName));
        }
    }

    public String getName() {
        return name;
    }

    public static String getMarblePath(String type) {
        return String.format("/assets/gui/market/%smarble.png", type.toLowerCase());
    }
}
