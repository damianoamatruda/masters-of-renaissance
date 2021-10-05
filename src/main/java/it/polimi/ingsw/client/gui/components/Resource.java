package it.polimi.ingsw.client.gui.components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

/** Gui component representing the resource types. */
public class Resource extends ImageView {
    private static final double width = 100;
    private final boolean isBlank;
    private final String name;

    /**
     * Sets the resource type name and displays the correct PNG.
     *
     * @param resourceName      the resource type name
     * @param isZeroReplacement
     */
    public Resource(String resourceName, boolean isZeroReplacement) {
        this.name = resourceName;
        this.isBlank = isZeroReplacement;

        this.setPreserveRatio(true);
        this.setFitWidth(width);

        try {
            Image bg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(String.format("/assets/gui/resourcetypes/%s.png", resourceName.toLowerCase()))));
            this.setImage(bg);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("No image file for resource '%s'.", resourceName), e);
        }
    }

    public Resource(String resourceName) {
        this(resourceName, false);
    }

    /**
     * Gets the path to the correct PNG.
     *
     * @param type the resource type to be represented
     * @return the file path to the PNG
     */
    public static String getMarblePath(String type) {
        return String.format("/assets/gui/market/%smarble.png", type.toLowerCase());
    }

    /**
     * Getter of the resource type name.
     *
     * @return the resource type name
     */
    public String getName() {
        return name;
    }

    public boolean isBlank() {
        return isBlank;
    }
}
