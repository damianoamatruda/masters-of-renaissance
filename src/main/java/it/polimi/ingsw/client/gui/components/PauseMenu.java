package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/** Gui component representing the pause menu. */
public class PauseMenu extends StackPane {
    @FXML
    private BorderPane main;
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

        Gui.getInstance().setSceneScaling(main);

        setHandlers();
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
        Gui gui = Gui.getInstance();
        PauseOptions pauseOptions = new PauseOptions();
        gui.getRoot().getChildren().remove(this);
        gui.getRoot().getChildren().add(pauseOptions);
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
