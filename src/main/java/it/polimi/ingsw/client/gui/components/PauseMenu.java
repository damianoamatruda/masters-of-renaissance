package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/** Gui component representing the pause menu. */
public class PauseMenu extends StackPane {
    @FXML
    private VBox window;
    @FXML
    private SButton back;
    @FXML
    private SButton opt;
    @FXML
    private SButton quit;

    public PauseMenu() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/pausemenu.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        setHandlers();

        Gui.getInstance().setSceneScaling(window);
    }

    /**
     * Handles closing the pause menu.
     */
    @FXML
    private void handleBack() {
        ((Pane) this.getParent()).getChildren().remove(this);
    }

    /**
     * Handles accessing the Options screen.
     */
    @FXML
    private void handleOptions() {
        Options options = new Options();
        options.setConfigContainer(false);
        Gui.getInstance().getRoot().getChildren().add(options);
    }

    /**
     *
     */
    @FXML
    private void handleQuit() {
        Gui.getInstance().getUi().dispatch(new ReqQuit());
    }

    /**
     *
     */
    private void setHandlers() {
        back.setOnAction((e) -> handleBack());
        opt.setOnAction((e) -> handleOptions());
        quit.setOnAction((e) -> handleQuit());
    }
}
