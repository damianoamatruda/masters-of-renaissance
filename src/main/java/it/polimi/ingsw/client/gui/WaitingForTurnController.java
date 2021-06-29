package it.polimi.ingsw.client.gui;

import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;
import it.polimi.ingsw.common.events.mvevents.UpdateAction.ActionType;

/**
 * Gui controller used when local player is not the current player. The current player's leaderboard is shown (without
 * their leader cards and/or other secret components).
 */
public class WaitingForTurnController extends TurnController {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        title.setText(String.format("Watching playerboard of: %s", gui.getViewModel().getCurrentPlayer()));

        AnchorPane.setLeftAnchor(title, 400d);
        AnchorPane.setTopAnchor(title, 10.0);

        canvas.getChildren().add(title);

        setLeadersBox(20d, 30d);
    }

    @Override
    public void on(UpdateAction event) {
        super.on(event);

        if (event.getAction() == ActionType.END_TURN &&
            !event.getPlayer().equals(vm.getLocalPlayerNickname()) &&
            vm.getCurrentPlayer().equals(vm.getLocalPlayerNickname()))
            setNextState();
    }
}
