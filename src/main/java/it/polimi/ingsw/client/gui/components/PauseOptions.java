package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class PauseOptions extends StackPane {
    @FXML
    private Options window;

    public PauseOptions() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/pauseoptions.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        Gui.getInstance().setSceneScaling(window);

        window.setOnBack(e -> {
            Gui.getInstance().getRoot().getChildren().remove(this);
            Gui.getInstance().getRoot().getChildren().add(new PauseMenu());
        });
    }
}