package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class GuiController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Gui.getInstance().setController(this);
    }

    public void on(Gui gui, ErrAction event) {
    }

    public void on(Gui gui, ErrActiveLeaderDiscarded event) {
    }

    public void on(Gui gui, ErrBuyDevCard event) {
    }

    public void on(Gui gui, ErrCardRequirements event) {
    }

    public void on(Gui gui, ErrNoSuchEntity event) {
    }

    public void on(Gui gui, ErrInitialChoice event) {
    }

    public void on(Gui gui, ErrNewGame event) {
    }

    public void on(Gui gui, ErrNickname event) {
    }

    public void on(Gui gui, ErrObjectNotOwned event) {
    }

    public void on(Gui gui, ErrReplacedTransRecipe event) {
    }

    public void on(Gui gui, ErrResourceReplacement event) {
    }

    public void on(Gui gui, ErrResourceTransfer event) {
    }

    public void on(Gui gui, ResQuit event) {
    }

    public void on(Gui gui, UpdateAction event) {
    }

    public void on(Gui gui, UpdateActionToken event) {
    }

    public void on(Gui gui, UpdateBookedSeats event) {
    }

    public void on(Gui gui, UpdateCurrentPlayer event) {
    }

    public void on(Gui gui, UpdateDevCardGrid event) {
    }

    public void on(Gui gui, UpdateDevCardSlot event) {
    }

    public void on(Gui gui, UpdateFaithPoints event) {
    }

    public void on(Gui gui, UpdateGameEnd event) {
    }

    public void on(Gui gui, UpdateGame event) {
    }

    public void on(Gui gui, UpdateJoinGame event) {
    }

    public void on(Gui gui, UpdateLastRound event) {
    }

    public void on(Gui gui, UpdateLeader event) {
    }

    public void on(Gui gui, UpdateLeadersHand event) {
    }

    public void on(Gui gui, UpdateLeadersHandCount event) {
    }

    public void on(Gui gui, UpdateMarket event) {
    }

    public void on(Gui gui, UpdatePlayer event) {
    }

    public void on(Gui gui, UpdatePlayerStatus event) {
    }

    public void on(Gui gui, UpdateResourceContainer event) {
    }

    public void on(Gui gui, UpdateSetupDone event) {
    }

    public void on(Gui gui, UpdateVaticanSection event) {
    }

    public void on(Gui gui, UpdateVictoryPoints event) {
    }
}
