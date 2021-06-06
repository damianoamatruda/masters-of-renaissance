package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.events.vcevents.*;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class NetworkHandler extends EventDispatcher implements Runnable {
    protected final Socket socket;
    protected final NetworkProtocol protocol;
    protected PrintWriter out;
    protected BufferedReader in;
    protected volatile boolean listening;

    public NetworkHandler(Socket socket, NetworkProtocol protocol) {
        this.socket = socket;
        this.protocol = protocol;

        this.out = null;
        this.in = null;
        this.listening = false;
    }

    public void registerOnMV(EventDispatcher view) {
        view.addEventListener(ErrAction.class, this::send);
        view.addEventListener(ErrActiveLeaderDiscarded.class, this::send);
        view.addEventListener(ErrBuyDevCard.class, this::send);
        view.addEventListener(ErrCardRequirements.class, this::send);
        view.addEventListener(ErrInitialChoice.class, this::send);
        view.addEventListener(ErrNewGame.class, this::send);
        view.addEventListener(ErrNickname.class, this::send);
        view.addEventListener(ErrNoSuchEntity.class, this::send);
        view.addEventListener(ErrObjectNotOwned.class, this::send);
        view.addEventListener(ErrReplacedTransRecipe.class, this::send);
        view.addEventListener(ErrResourceReplacement.class, this::send);
        view.addEventListener(ErrResourceTransfer.class, this::send);
        view.addEventListener(ResQuit.class, this::send);
        view.addEventListener(UpdateAction.class, this::send);
        view.addEventListener(UpdateActionToken.class, this::send);
        view.addEventListener(UpdateBookedSeats.class, this::send);
        view.addEventListener(UpdateCurrentPlayer.class, this::send);
        view.addEventListener(UpdateDevCardGrid.class, this::send);
        view.addEventListener(UpdateDevCardSlot.class, this::send);
        view.addEventListener(UpdateFaithPoints.class, this::send);
        view.addEventListener(UpdateGame.class, this::send);
        view.addEventListener(UpdateGameEnd.class, this::send);
        view.addEventListener(UpdateJoinGame.class, this::send);
        view.addEventListener(UpdateLastRound.class, this::send);
        view.addEventListener(UpdateActivateLeader.class, this::send);
        view.addEventListener(UpdateLeadersHand.class, this::send);
        view.addEventListener(UpdateLeadersHandCount.class, this::send);
        view.addEventListener(UpdateMarket.class, this::send);
        view.addEventListener(UpdatePlayer.class, this::send);
        view.addEventListener(UpdatePlayerStatus.class, this::send);
        view.addEventListener(UpdateResourceContainer.class, this::send);
        view.addEventListener(UpdateSetupDone.class, this::send);
        view.addEventListener(UpdateVaticanSection.class, this::send);
        view.addEventListener(UpdateVictoryPoints.class, this::send);
    }

    public void unregisterOnMV(EventDispatcher view) {
        view.removeEventListener(ErrAction.class, this::send);
        view.removeEventListener(ErrActiveLeaderDiscarded.class, this::send);
        view.removeEventListener(ErrBuyDevCard.class, this::send);
        view.removeEventListener(ErrCardRequirements.class, this::send);
        view.removeEventListener(ErrInitialChoice.class, this::send);
        view.removeEventListener(ErrNewGame.class, this::send);
        view.removeEventListener(ErrNickname.class, this::send);
        view.removeEventListener(ErrNoSuchEntity.class, this::send);
        view.removeEventListener(ErrObjectNotOwned.class, this::send);
        view.removeEventListener(ErrReplacedTransRecipe.class, this::send);
        view.removeEventListener(ErrResourceReplacement.class, this::send);
        view.removeEventListener(ErrResourceTransfer.class, this::send);
        view.removeEventListener(ResQuit.class, this::send);
        view.removeEventListener(UpdateAction.class, this::send);
        view.removeEventListener(UpdateActionToken.class, this::send);
        view.removeEventListener(UpdateBookedSeats.class, this::send);
        view.removeEventListener(UpdateCurrentPlayer.class, this::send);
        view.removeEventListener(UpdateDevCardGrid.class, this::send);
        view.removeEventListener(UpdateDevCardSlot.class, this::send);
        view.removeEventListener(UpdateFaithPoints.class, this::send);
        view.removeEventListener(UpdateGame.class, this::send);
        view.removeEventListener(UpdateGameEnd.class, this::send);
        view.removeEventListener(UpdateJoinGame.class, this::send);
        view.removeEventListener(UpdateLastRound.class, this::send);
        view.removeEventListener(UpdateActivateLeader.class, this::send);
        view.removeEventListener(UpdateLeadersHand.class, this::send);
        view.removeEventListener(UpdateLeadersHandCount.class, this::send);
        view.removeEventListener(UpdateMarket.class, this::send);
        view.removeEventListener(UpdatePlayer.class, this::send);
        view.removeEventListener(UpdatePlayerStatus.class, this::send);
        view.removeEventListener(UpdateResourceContainer.class, this::send);
        view.removeEventListener(UpdateSetupDone.class, this::send);
        view.removeEventListener(UpdateVaticanSection.class, this::send);
        view.removeEventListener(UpdateVictoryPoints.class, this::send);
    }

    public void registerOnVC(View view) {
        view.addEventListener(ReqActivateProduction.class, this::send);
        view.addEventListener(ReqBuyDevCard.class, this::send);
        view.addEventListener(ReqChooseLeaders.class, this::send);
        view.addEventListener(ReqChooseResources.class, this::send);
        view.addEventListener(ReqEndTurn.class, this::send);
        view.addEventListener(ReqJoin.class, this::send);
        view.addEventListener(ReqLeaderAction.class, this::send);
        view.addEventListener(ReqNewGame.class, this::send);
        view.addEventListener(ReqQuit.class, this::send);
        view.addEventListener(ReqSwapShelves.class, this::send);
        view.addEventListener(ReqTakeFromMarket.class, this::send);
    }

    public void unregisterOnVC(View view) {
        view.removeEventListener(ReqActivateProduction.class, this::send);
        view.removeEventListener(ReqBuyDevCard.class, this::send);
        view.removeEventListener(ReqChooseLeaders.class, this::send);
        view.removeEventListener(ReqChooseResources.class, this::send);
        view.removeEventListener(ReqEndTurn.class, this::send);
        view.removeEventListener(ReqJoin.class, this::send);
        view.removeEventListener(ReqLeaderAction.class, this::send);
        view.removeEventListener(ReqNewGame.class, this::send);
        view.removeEventListener(ReqQuit.class, this::send);
        view.removeEventListener(ReqSwapShelves.class, this::send);
        view.removeEventListener(ReqTakeFromMarket.class, this::send);
    }

    public abstract void run();

    public void stop() {
        listening = false;
    }

    public void send(Event event) {
        if (out != null)
            out.println(protocol.processOutput(event));
    }
}
