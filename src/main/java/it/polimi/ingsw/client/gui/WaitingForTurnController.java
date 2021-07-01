package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Gui controller used when local player is not the current player. The current player's leaderboard is shown (without
 * their leader cards and/or other secret components).
 */
public class WaitingForTurnController extends TurnController {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        title.setText(String.format("Watching playerboard of: %s", vm.getCurrentPlayer().get()));

        AnchorPane.setLeftAnchor(title, 400d);
        AnchorPane.setTopAnchor(title, 10.0);

        canvas.getChildren().add(title);

        setLeadersBox(20d, 30d);
    }

    /* Other players' UpdateAction isn't a good indicator
       of the need a state change: it is not fired when a player disconnects.
       Therefore, UpdateCurrentPlayer is necessary. */
    @Override
    public void on(UpdateCurrentPlayer event) {
        super.on(event);

        if (vm.localPlayerIsCurrent())
            setNextState();
    }
}
