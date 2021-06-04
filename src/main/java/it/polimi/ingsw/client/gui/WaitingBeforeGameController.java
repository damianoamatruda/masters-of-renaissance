package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.common.events.mvevents.UpdateBookedSeats;
import it.polimi.ingsw.common.events.mvevents.UpdateGame;
import it.polimi.ingsw.common.events.mvevents.UpdateJoinGame;
import it.polimi.ingsw.common.events.mvevents.UpdateLeadersHand;
import it.polimi.ingsw.common.events.vcevents.ReqNewGame;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class WaitingBeforeGameController extends GuiController {
    @FXML
    private Text bookedSeats;
    @FXML
    private ToggleGroup group;
    @FXML
    private VBox canPrepare;
    private boolean youCanPrepare;

    public WaitingBeforeGameController() {
        youCanPrepare = false;
    }

    public void setBookedSeats(int bookedSeatsValue) {
        bookedSeats.setText(Integer.toString(bookedSeatsValue));
    }

    public void setCanPrepareNewGame(String canPrepareNewGame) {
        if (Gui.getInstance().getViewModel().getLocalPlayerNickname().equals(canPrepareNewGame)) {
            youCanPrepare = true;
            canPrepare.setVisible(true);
        }
    }

    @Override
    public void on(Gui gui, UpdateBookedSeats event) {
        super.on(gui, event);
        setBookedSeats(event.getBookedSeats());
    }

    @Override
    public void on(Gui gui, UpdateGame event) {
        super.on(gui, event);
        System.out.println("Game started.");
    }

    @Override
    public void on(Gui gui, UpdateLeadersHand event) {
        super.on(gui, event);
        gui.setRoot(getClass().getResource("/assets/gui/setupleaders.fxml"));
    }

    @Override
    public void on(Gui gui, UpdateJoinGame event) {
        super.on(gui, event);
        if (youCanPrepare)
            canPrepare.setVisible(false);
    }

    @FXML
    private void handleNewGame() {
        Gui gui = Gui.getInstance();
        RadioButton inputRadio = (RadioButton) group.getSelectedToggle();
        try {
            int count = Integer.parseInt(inputRadio.getText());
            gui.dispatch(new ReqNewGame(count));
        } catch (NumberFormatException e) {
            System.out.println("Not a number");
        }
    }
}
