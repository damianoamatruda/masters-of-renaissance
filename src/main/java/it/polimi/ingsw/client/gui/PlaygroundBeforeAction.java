package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.SButton;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PlaygroundBeforeAction extends PlaygroundController{

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        Gui gui = Gui.getInstance();

        Button left = new SButton();
//        left.setAlignment(Pos.BOTTOM_LEFT);
        left.setText("Market");
        left.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            try {
                gui.setRoot(getClass().getResource("/assets/gui/market.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        canvas.getChildren().add(left);

        Button right = new SButton();
//        right.setAlignment(Pos.CENTER_RIGHT);
        right.setText("Grid");
        right.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            try {
                gui.setRoot(getClass().getResource("/assets/gui/devcardgrid.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        canvas.getChildren().add(right);
        AnchorPane.setRightAnchor(canvas.getChildren().get(3), 0.0);
//        AnchorPane.setBottomAnchor(canvas.getChildren().get(1), this.canvas.getHeight()/2);
//        AnchorPane.setBottomAnchor(canvas.getChildren().get(2), 100.0);
    }

    @Override
    public void on(Gui gui, UpdateAction event) {
        super.on(gui, event);
        try {
            gui.setRoot(getClass().getResource("/assets/gui/playgroundafteraction.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
