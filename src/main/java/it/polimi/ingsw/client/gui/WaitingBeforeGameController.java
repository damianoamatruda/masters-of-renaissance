package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.common.events.mvevents.UpdateBookedSeats;
import it.polimi.ingsw.common.events.mvevents.UpdateGame;
import it.polimi.ingsw.common.events.vcevents.ReqNewGame;
import javafx.event.ActionEvent;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;

import java.io.IOException;

public class WaitingBeforeGameController extends GuiController {
    public Text bookedSeats;
    public ToggleGroup group;

    @Override
    public void on(Gui gui, UpdateBookedSeats event) {
        super.on(gui, event);
        bookedSeats.setText(Integer.toString(event.getBookedSeats()));
    }

    @Override
    public void on(Gui gui, UpdateGame event) {
        super.on(gui, event);
        System.out.println("Game started.");
        try {
            Gui.setRoot("setupleaders");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleNewGame(ActionEvent actionEvent) {
        // Gui.dispatch(new ReqNewGame((int) group.getSelectedToggle().getProperties().get("text")));
        Gui.dispatch(new ReqNewGame(2));
    }
}
