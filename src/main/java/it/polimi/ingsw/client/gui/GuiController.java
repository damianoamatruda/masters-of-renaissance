package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.ViewModel.PlayerData;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class GuiController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Gui.getInstance().setController(this);
    }

    public void on(Gui gui, ErrAction event) {
        // TODO: handle
    }

    public void on(Gui gui, ErrActiveLeaderDiscarded event) {
        // TODO: handle
    }

    public void on(Gui gui, ErrBuyDevCard event) {
        // TODO: handle
    }

    public void on(Gui gui, ErrCardRequirements event) {
        // TODO: handle
    }

    public void on(Gui gui, ErrNoSuchEntity event) {
        // TODO: handle
    }

    public void on(Gui gui, ErrInitialChoice event) {
        // TODO: handle
    }

    public void on(Gui gui, ErrNewGame event) {
        // TODO: handle
    }

    public void on(Gui gui, ErrNickname event) {
        // TODO: handle
    }

    public void on(Gui gui, ErrObjectNotOwned event) {
        // TODO: handle
    }

    public void on(Gui gui, ErrReplacedTransRecipe event) {
        // TODO: handle
    }

    public void on(Gui gui, ErrResourceReplacement event) {
        // TODO: handle
    }

    public void on(Gui gui, ErrResourceTransfer event) {
        // TODO: handle
    }

    public void on(Gui gui, ResQuit event) {
        // TODO: handle
    }

    public void on(Gui gui, UpdateAction event) {
        // TODO: handle
    }

    public void on(Gui gui, UpdateActionToken event) {
        // TODO: show (doesn't update the VM)
    }

    public void on(Gui gui, UpdateBookedSeats event) {
        // TODO: show (doesn't update the VM)
    }

    public void on(Gui gui, UpdateCurrentPlayer event) {
        gui.getViewModel().getGameData().setCurrentPlayer(event.getPlayer());
    }

    public void on(Gui gui, UpdateDevCardGrid event) {
        gui.getViewModel().getGameData().setDevCardGrid(event.getCards());
    }

    public void on(Gui gui, UpdateDevCardSlot event) {
        gui.getViewModel().getCurrentPlayerData().setDevSlot(event.getDevSlot(), event.getDevCard());
    }

    public void on(Gui gui, UpdateFaithPoints event) {
        gui.getViewModel().getCurrentPlayerData().setFaithPoints(event.getFaithPoints());
    }

    public void on(Gui gui, UpdateGameEnd event) {
        gui.getViewModel().getGameData().setWinner(event.getWinner());
    }

    public void on(Gui gui, UpdateGame event) {
        gui.getViewModel().getGameData().setActionTokens(event.getActionTokens());
        gui.getViewModel().getGameData().setContainers(event.getResContainers());
        gui.getViewModel().getGameData().setDevelopmentCards(event.getDevelopmentCards());
        gui.getViewModel().getGameData().setLeaderCards(event.getLeaderCards());
        gui.getViewModel().getGameData().setPlayerNicknames(event.getPlayers());
        gui.getViewModel().getGameData().setProductions(event.getProductions());
        gui.getViewModel().getGameData().setFaithTrack(event.getFaithTrack());
        gui.getViewModel().getGameData().setDevCardColors(event.getColors());
        gui.getViewModel().getGameData().setResourceTypes(event.getResourceTypes());

    }

    public void on(Gui gui, UpdateJoinGame event) {
        // TODO: handle
    }

    public void on(Gui gui, UpdateLastRound event) {
        gui.getViewModel().getGameData().setLastRound();
    }

    public void on(Gui gui, UpdateLeader event) {
        if (event.isActive())
            gui.getViewModel().getGameData()
                    .getLeaderCard(event.getLeader())
                    .ifPresent(ReducedLeaderCard::setActive);
        else
            // is a discard move (well, technically never used as such,
            // see constructor references and UpdateLeadersHandCount)
            gui.getViewModel().getCurrentPlayerData().setLeadersCount(
                    gui.getViewModel().getCurrentPlayerData().getLeadersCount() - 1);
    }

    public void on(Gui gui, UpdateLeadersHand event) {
        /* this message arrives last among the starting events:
            joingame
            updategamestart
            currplayer
            market
            devcardgrid
            player
            leadershand -> with GS and player has enough info for leader choice */

        gui.getViewModel().getCurrentPlayerData().setLeadersHand(event.getLeaders());
    }

    public void on(Gui gui, UpdateLeadersHandCount event) {
        gui.getViewModel().getCurrentPlayerData().setLeadersCount(event.getLeadersCount());
    }

    public void on(Gui gui, UpdateMarket event) {
        gui.getViewModel().getGameData().setMarket(event.getMarket());
    }

    public void on(Gui gui, UpdatePlayer event) {
        gui.getViewModel().setPlayerData(event.getPlayer(), new PlayerData(
                event.getBaseProduction(),
                event.getPlayerSetup(),
                event.getStrongbox(),
                event.getWarehouseShelves()));
    }

    public void on(Gui gui, UpdatePlayerStatus event) {
        gui.getViewModel().getCurrentPlayerData().setActive(event.isActive());
    }

    public void on(Gui gui, UpdateResourceContainer event) {
        gui.getViewModel().getGameData().setContainer(event.getResContainer());
    }

    public void on(Gui gui, UpdateSetupDone event) {
        // TODO: handle
    }

    public void on(Gui gui, UpdateVaticanSection event) {
        gui.getViewModel().getGameData().setVaticanSection(event.getVaticanSection());
    }

    public void on(Gui gui, UpdateVictoryPoints event) {
        gui.getViewModel().getCurrentPlayerData().setVictoryPoints(event.getVictoryPoints());
    }
}
