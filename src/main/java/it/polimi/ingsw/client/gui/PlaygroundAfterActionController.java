package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.SButton;
import it.polimi.ingsw.common.events.mvevents.UpdateActionToken;
import it.polimi.ingsw.common.events.vcevents.ReqEndTurn;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Gui controller used when the local player is current player, and has already chosen a base turn action.
 * Here the client can choose to do leader actions, swap shelves or end the turn.
 */
public class PlaygroundAfterActionController extends PlaygroundController {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        Button endTurn = new SButton("End turn");
        endTurn.setDefaultButton(true);
        endTurn.addEventHandler(ActionEvent.ACTION, actionEvent ->
                gui.getUi().dispatch(new ReqEndTurn()));
        AnchorPane.setBottomAnchor(endTurn, 0.0);
        AnchorPane.setRightAnchor(endTurn, 0.0);
        canvas.getChildren().add(endTurn);

        setLeadersBox(20, 50);

        title.setText("End your turn or do an extra action");
        AnchorPane.setLeftAnchor(title, 10.0);
        AnchorPane.setTopAnchor(title, 5.0);
        canvas.getChildren().add(title);

        warehouse.enableSwapper();
    }

    @Override
    public void on(UpdateActionToken event) {
        super.on(event);
        gui.setScene(getClass().getResource("/assets/gui/triggeractiontoken.fxml"));
    }
}
