package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.SButton;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class PlaygroundBeforeAction extends PlaygroundController{

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Gui gui = Gui.getInstance();

        Button left = new SButton();
//        left.setAlignment(Pos.BOTTOM_LEFT);
        left.setText("Market");
        left.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent ->
                gui.setRoot(getClass().getResource("/assets/gui/market.fxml")));
        canvas.getChildren().add(left);

        Button right = new SButton();
//        right.setAlignment(Pos.CENTER_RIGHT);
        right.setText("Grid");
        right.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent ->
                gui.setRoot(getClass().getResource("/assets/gui/devcardgrid.fxml")));
        canvas.getChildren().add(right);

        AnchorPane.setLeftAnchor(left, 0d);
        AnchorPane.setTopAnchor(left, 0d);
        AnchorPane.setRightAnchor(right, 0d);
        AnchorPane.setTopAnchor(right, 0d);

        setLeadersBox();

        topText.setText("Your turn");

        AnchorPane.setLeftAnchor(topText, 10.0);
        AnchorPane.setTopAnchor(topText, 50.0);

        canvas.getChildren().add(topText);

        warehouse.enableSwapper();
    }

    @Override
    public void on(Gui gui, UpdateAction event) {
        super.on(gui, event);
        if(event.getAction() == UpdateAction.ActionType.BUY_DEVELOPMENT_CARD ||
                event.getAction() == UpdateAction.ActionType.TAKE_MARKET_RESOURCES ||
                event.getAction() == UpdateAction.ActionType.ACTIVATE_PRODUCTION)
        gui.setRoot(getClass().getResource("/assets/gui/playgroundafteraction.fxml"));
    }
}
