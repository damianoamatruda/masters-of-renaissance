package it.polimi.ingsw.client.gui.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.Objects;

/** Gui component representing the resource types. */
public class Resource extends ImageView {
    private String name;

    /**
     * Class constructor.
     */
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

    /**
     * Sets the resource type name and displays the correct PNG.
     *
     * @param resourceName the resource type name
     */
    public void setResourceType(String resourceName) {
        this.name = resourceName;
        try {
            Image bg = new Image(Objects.requireNonNull(getClass().getResource(String.format("/assets/gui/resourcetypes/%s.png", resourceName.toLowerCase()))).toExternalForm());
            this.setImage(bg);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("No image file for resource '%s'.", resourceName));
        }
    }

    /**
     * Getter of the resource type name.
     *
     * @return the resource type name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the path to the correct PNG.
     *
     * @param type  the resource type to be represented
     * @return  the file path to the PNG
     */
    public static String getMarblePath(String type) {
        return String.format("/assets/gui/market/%smarble.png", type.toLowerCase());
    }
}
