package it.polimi.ingsw.client.gui.components;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class Title extends HBox {
    @FXML
    private Text titleText;

    /**
     *
     */
    public Title() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/title.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     *
     * @return
     */
    public String getText() {
        return textProperty().get();
    }

    /**
     *
     * @param value
     */
    public void setText(String value) {
        textProperty().set(value);
    }

    /**
     *
     * @return
     */
    public StringProperty textProperty() {
        return titleText.textProperty();
    }
}
