package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.SButton;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Gui controller used when the local player is current player, and hasn't chosen a base turn action. Here the client
 * can choose to produce, buy a card or visit the market, as well as doing leader actions and swap shelves.
 */
public class TurnBeforeActionController extends TurnController {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allowProductions = true;

        super.initialize(url, resourceBundle);

        Button marketButton = new SButton("Market");
        marketButton.addEventHandler(ActionEvent.ACTION, actionEvent ->
                gui.setScene(getClass().getResource("/assets/gui/takefrommarket.fxml")));
        AnchorPane.setLeftAnchor(marketButton, 10d);
        AnchorPane.setTopAnchor(marketButton, 10d);
        canvas.getChildren().add(marketButton);

        Button gridButton = new SButton("Grid");
        gridButton.addEventHandler(ActionEvent.ACTION, actionEvent ->
                gui.setScene(getClass().getResource("/assets/gui/buydevelopmentcard.fxml")));
        AnchorPane.setRightAnchor(gridButton, 10d);
        AnchorPane.setTopAnchor(gridButton, 10d);
        canvas.getChildren().add(gridButton);

        setLeadersBox(54.0, 30.0);

        title.setText("Your turn");
        AnchorPane.setLeftAnchor(title, 500d);
        AnchorPane.setTopAnchor(title, 10.0);
        canvas.getChildren().add(title);

        warehouse.enableSwapper();

        addProduceButtons();
    }

    @Override
    public void on(UpdateAction event) {
        super.on(event);
        if (event.getAction() == UpdateAction.ActionType.BUY_DEVELOPMENT_CARD ||
                event.getAction() == UpdateAction.ActionType.TAKE_MARKET_RESOURCES ||
                event.getAction() == UpdateAction.ActionType.ACTIVATE_PRODUCTION)
            gui.setScene(getClass().getResource("/assets/gui/turnafteraction.fxml"));
    }
}
