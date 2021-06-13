package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.Options;
import it.polimi.ingsw.client.gui.components.SButton;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class PauseMenu extends StackPane {
    public BorderPane options;
    @FXML private StackPane backStackPane;
    @FXML private BorderPane bpane;
    @FXML private SButton back;
    @FXML private SButton opt;
    @FXML private SButton quit;

    private NumberBinding maxScale;

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

        maxScale = Bindings.min(backStackPane.widthProperty().divide(Gui.realWidth),
                backStackPane.heightProperty().divide(Gui.realHeight));
        bpane.scaleXProperty().bind(maxScale);
        bpane.scaleYProperty().bind(maxScale);
    }

    @FXML
    private void handleBack() {
        ((Pane) this.getParent()).getChildren().remove(this);
    }

    @FXML
    private void handleOptions() {
        Options options = new Options(maxScale);
        options.setConfigContainer(false);
        backStackPane.getChildren().add(options);
    }

    @FXML
    private void handleQuit() {
        Gui.getInstance().dispatch(new ReqQuit());
    }

    private void setHandlers() {
        back.setOnAction((e) -> handleBack());
        opt.setOnAction((e) -> handleOptions());
        quit.setOnAction((e) -> handleQuit());
    }
}
