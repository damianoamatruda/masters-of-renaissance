package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.IOException;

public class Alert extends StackPane {
    @FXML
    private VBox window;
    @FXML
    private Title titleText;
    @FXML
    private SButton okButton;
    @FXML
    private Text contentText;

    public Alert(String title, String content) {
        this(title, content, null);
    }

    /**
     * @param title
     * @param content
     */
    public Alert(String title, String content, Runnable callback) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/alert.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        Gui.getInstance().setSceneScaling(window);

        titleText.setText(title);

        contentText.setText(content);
        contentText.setWrappingWidth(300);
        contentText.setTextAlignment(TextAlignment.CENTER);

        okButton.setDefaultButton(true);
        okButton.addEventHandler(ActionEvent.ACTION, (ActionEvent actionEvent) -> {
            ((Pane) getParent()).getChildren().remove(this);

            if (callback != null)
                callback.run();
        });
    }
}
