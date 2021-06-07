package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.viewmodel.PlayerData;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class GuiController implements Initializable {
    public GuiController() {
        Gui.getInstance().setController(this);
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
        gui.stopNetwork();
        gui.setRoot(getClass().getResource("/assets/gui/mainmenu.fxml"));
    }

    public void on(Gui gui, UpdateAction event) {
        // TODO: handle
    }

    public void on(Gui gui, UpdateActionToken event) {
        // TODO: show
        gui.getViewModel().setLatestToken(gui.getViewModel().getActionToken(event.getActionToken()).orElseThrow());
    }

    public void on(Gui gui, UpdateBookedSeats event) {
        // TODO: handle
    }

    public void on(Gui gui, UpdateCurrentPlayer event) {
        gui.getViewModel().setCurrentPlayer(event.getPlayer());
    }

    public void on(Gui gui, UpdateDevCardGrid event) {
        gui.getViewModel().setDevCardGrid(event.getCards());
    }

    public void on(Gui gui, UpdateDevCardSlot event) {
        gui.getViewModel().getCurrentPlayerData().orElseThrow().setDevSlot(event.getDevSlot(), event.getDevCard());
    }

    public void on(Gui gui, UpdateFaithPoints event) {
        if (event.isBlackCross())
            gui.getViewModel().setBlackCrossFP(event.getFaithPoints());
        else
            gui.getViewModel().getPlayerData(event.getPlayer()).orElseThrow().setFaithPoints(event.getFaithPoints());
    }

    public void on(Gui gui, UpdateGameEnd event) {
        gui.getViewModel().setWinner(event.getWinner());
    }

    public void on(Gui gui, UpdateGame event) {
        gui.getViewModel().setActionTokens(event.getActionTokens());
        gui.getViewModel().setContainers(event.getResContainers());
        gui.getViewModel().setDevelopmentCards(event.getDevelopmentCards());
        gui.getViewModel().setLeaderCards(event.getLeaderCards());
        gui.getViewModel().setPlayerNicknames(event.getPlayers());
        gui.getViewModel().setProductions(event.getProductions());
        gui.getViewModel().setFaithTrack(event.getFaithTrack());
        gui.getViewModel().setDevCardColors(event.getColors());
        gui.getViewModel().setResourceTypes(event.getResourceTypes());
        gui.getViewModel().setSlotsCount(event.getSlotsCount());

        gui.getViewModel().setResumedGame(event.isResumed());
    }

    public void on(Gui gui, UpdateJoinGame event) {
        // TODO: handle
    }

    public void on(Gui gui, UpdateLastRound event) {
        gui.getViewModel().setLastRound();
    }

    public void on(Gui gui, UpdateActivateLeader event) {
        gui.getViewModel().activateLeaderCard(event.getLeader());
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

        gui.getViewModel().getPlayerData(event.getPlayer()).orElseThrow().setLeadersHand(event.getLeaders());
    }

    public void on(Gui gui, UpdateLeadersHandCount event) {
        gui.getViewModel().getPlayerData(event.getPlayer()).orElseThrow().setLeadersCount(event.getLeadersCount());
    }

    public void on(Gui gui, UpdateMarket event) {
        gui.getViewModel().setMarket(event.getMarket());
    }

    public void on(Gui gui, UpdatePlayer event) {
        gui.getViewModel().setPlayerData(event.getPlayer(), new PlayerData(
                event.getBaseProduction(),
                event.getPlayerSetup(),
                event.getStrongbox(),
                event.getWarehouseShelves()));
    }

    public void on(Gui gui, UpdatePlayerStatus event) {
        gui.getViewModel().getPlayerData(event.getPlayer()).orElseThrow().setActive(event.isActive());
    }

    public void on(Gui gui, UpdateResourceContainer event) {
        gui.getViewModel().setContainer(event.getResContainer());
    }

    public void on(Gui gui, UpdateSetupDone event) {
        gui.getViewModel().setSetupDone(true);
    }

    public void on(Gui gui, UpdateVaticanSection event) {
        gui.getViewModel().setVaticanSection(event.getVaticanSection());
    }

    public void on(Gui gui, UpdateVictoryPoints event) {
        gui.getViewModel().getPlayerData(event.getPlayer()).orElseThrow().setVictoryPoints(event.getVictoryPoints());
    }
}
