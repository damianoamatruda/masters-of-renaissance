package it.polimi.ingsw.client.gui;

import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Gui controller used when local player is not the current player.
 * The current player's leaderboard is shown (without their leader cards and/or other secret components).
 */
public class WaitingForTurnController extends PlaygroundController {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        topText.setText(String.format("Watching playerboard of: %s", gui.getViewModel().getCurrentPlayer()));

        AnchorPane.setLeftAnchor(topText, 450d);
        AnchorPane.setTopAnchor(topText, 10.0);

        canvas.getChildren().add(topText);
        AnchorPane.setRightAnchor(playerBoard, 0d);

    }
}
