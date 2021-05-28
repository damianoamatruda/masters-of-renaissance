package it.polimi.ingsw.client.gui.components;

import java.io.IOException;
import java.util.Objects;

import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Resource extends ImageView {
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
        try {
            Image bg = new Image(Objects.requireNonNull(getClass().getResource(String.format("/assets/gui/resourcetypes/%s.png", resourceName.toLowerCase()))).toExternalForm());
            this.setImage(bg);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("No image file for resource '%s'.", resourceName));
        }
    }

    public static String getMarblePath(String type) {
        return "/assets/gui/market/" + type.toLowerCase() + "marble.png";
    }
}
