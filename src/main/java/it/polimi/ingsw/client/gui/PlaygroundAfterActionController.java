package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.SButton;
import it.polimi.ingsw.common.events.mvevents.UpdateActionToken;
import it.polimi.ingsw.common.events.vcevents.ReqEndTurn;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class PlaygroundAfterActionController extends PlaygroundController {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        Gui gui = Gui.getInstance();

        Button endTurn = new SButton();
        endTurn.setText("End turn");
        endTurn.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            gui.getUi().dispatch(new ReqEndTurn());
        });
        canvas.getChildren().add(endTurn);
        AnchorPane.setBottomAnchor(endTurn, 0.0);
        AnchorPane.setRightAnchor(endTurn, 0.0);

        setLeadersBox();

        topText.setText("End your turn or do an extra action");

        AnchorPane.setLeftAnchor(topText, 10.0);
        AnchorPane.setTopAnchor(topText, 5.0);

        canvas.getChildren().add(topText);

        warehouse.enableSwapper();

    }

    @Override
    public void on(Gui gui, UpdateActionToken event) {
        super.on(gui, event);
        gui.setRoot(getClass().getResource("/assets/gui/triggeractiontoken.fxml"));
    }
}
