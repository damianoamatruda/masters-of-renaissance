package it.polimi.ingsw.server;

import it.polimi.ingsw.common.EventDispatcher;
import it.polimi.ingsw.common.EventPasser;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.*;

/**
 * This class represents a virtual view.
 */
public class VirtualView extends View {
    /** The event passer of the view. */
    private EventPasser eventPasser;

    /**
     * Class constructor.
     */
    public VirtualView() {
        this.eventPasser = null;
    }

    /**
     * Sets the event passer.
     *
     * @param eventPasser the event passer
     */
    public void setEventPasser(EventPasser eventPasser) {
        this.eventPasser = eventPasser;
    }

    @Override
    public void registerToModelGame(EventDispatcher game) {
        /* Game */
        game.addEventListener(UpdateGameStart.class, this::on);
        game.addEventListener(UpdateCurrentPlayer.class, this::on);
        game.addEventListener(UpdateSetupDone.class, this::on);
        game.addEventListener(UpdateGameResume.class, this::on);
        game.addEventListener(UpdateLastRound.class, this::on);
        game.addEventListener(UpdateGameEnd.class, this::on);

        /* Player */
        game.addEventListener(UpdatePlayer.class, this::on);
        game.addEventListener(UpdateLeadersHandCount.class, this::on);
        game.addEventListener(UpdateFaithPoints.class, this::on);
        game.addEventListener(UpdateVictoryPoints.class, this::on);
        game.addEventListener(UpdatePlayerStatus.class, this::on);
        game.addEventListener(UpdateDevCardSlot.class, this::on);

        /* Card */
        game.addEventListener(UpdateLeader.class, this::on);

        /* ResourceContainer */
        game.addEventListener(UpdateResourceContainer.class, this::on);

        /* DevCardGrid */
        game.addEventListener(UpdateDevCardGrid.class, this::on);

        /* Market */
        game.addEventListener(UpdateMarket.class, this::on);

        /* FaithTrack */
        game.addEventListener(UpdateVaticanSection.class, this::on);

        /* SoloGame */
        game.addEventListener(UpdateActionToken.class, this::on);
    }

    @Override
    public void unregisterToModelGame(EventDispatcher game) {
        /* Game */
        game.removeEventListener(UpdateGameStart.class, this::on);
        game.removeEventListener(UpdateCurrentPlayer.class, this::on);
        game.removeEventListener(UpdateSetupDone.class, this::on);
        game.removeEventListener(UpdateGameResume.class, this::on);
        game.removeEventListener(UpdateLastRound.class, this::on);
        game.removeEventListener(UpdateGameEnd.class, this::on);

        /* Player */
        game.removeEventListener(UpdatePlayer.class, this::on);
        game.removeEventListener(UpdateLeadersHandCount.class, this::on);
        game.removeEventListener(UpdateFaithPoints.class, this::on);
        game.removeEventListener(UpdateVictoryPoints.class, this::on);
        game.removeEventListener(UpdatePlayerStatus.class, this::on);
        game.removeEventListener(UpdateDevCardSlot.class, this::on);

        /* Card */
        game.removeEventListener(UpdateLeader.class, this::on);

        /* ResourceContainer */
        game.removeEventListener(UpdateResourceContainer.class, this::on);

        /* DevCardGrid */
        game.removeEventListener(UpdateDevCardGrid.class, this::on);

        /* Market */
        game.removeEventListener(UpdateMarket.class, this::on);

        /* FaithTrack */
        game.removeEventListener(UpdateVaticanSection.class, this::on);

        /* SoloGame */
        game.removeEventListener(UpdateActionToken.class, this::on);
    }

    @Override
    public void registerToModelPlayer(EventDispatcher player) {
        player.addEventListener(UpdateLeadersHand.class, this::on);
    }

    @Override
    public void unregisterToModelPlayer(EventDispatcher player) {
        player.removeEventListener(UpdateLeadersHand.class, this::on);
    }

    @Override
    public void on(MVEvent event) {
        if (eventPasser == null)
            throw new RuntimeException("Cannot send MVEvent: no passer available.");
        eventPasser.on(event);
    }
}
