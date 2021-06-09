package it.polimi.ingsw.client.gui;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class WaitingForTurnController extends PlaygroundController {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        topText.setText(String.format("Watching playerboard of: %s", Gui.getInstance().getViewModel().getCurrentPlayer()));

        AnchorPane.setLeftAnchor(topText, 10.0);
        AnchorPane.setTopAnchor(topText, 5.0);

        canvas.getChildren().add(topText);

        setPauseOnEsc();
    }

    @Override
    protected void setPauseOnEsc() {
        this.canvas.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                Gui.getInstance().setRoot(getClass().getResource("/assets/gui/pausemenu.fxml"), (PauseMenuController controller) -> {
                    controller.setLastScene(getClass().getResource("/assets/gui/waitingforturn.fxml"));
                });
            }
        });
    }
}
