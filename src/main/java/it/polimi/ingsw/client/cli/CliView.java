package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.EventDispatcher;
import it.polimi.ingsw.common.EventPasser;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.*;

public class CliView extends View {
    /** The event passer of the view. */
    private EventPasser eventPasser;

    /**
     * Class constructor.
     */
    public CliView() {
        super();
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
    public void registerToModelLobby(EventDispatcher model) {
        model.addEventListener(ErrAction.class, this::on);
        model.addEventListener(UpdateBookedSeats.class, this::on);
        model.addEventListener(UpdateJoinGame.class, this::on);
        model.addEventListener(ResGoodbye.class, this::on);
    }

    @Override
    public void unregisterToModelLobby(EventDispatcher model) {
        model.removeEventListener(ErrAction.class, this::on);
        model.removeEventListener(UpdateBookedSeats.class, this::on);
        model.removeEventListener(UpdateJoinGame.class, this::on);
        model.removeEventListener(ResGoodbye.class, this::on);
    }

    @Override
    public void registerToModelGameContext(EventDispatcher model) {
        model.addEventListener(ErrAction.class, this::on);
        model.addEventListener(UpdateSetupDone.class, this::on);

        /* Game */
        model.addEventListener(UpdateGameStart.class, this::on);
        model.addEventListener(UpdateCurrentPlayer.class, this::on);
        model.addEventListener(UpdateGameResume.class, this::on);
        model.addEventListener(UpdateLastRound.class, this::on);
        model.addEventListener(UpdateGameEnd.class, this::on);

        /* Player */
        model.addEventListener(UpdatePlayer.class, this::on);
        model.addEventListener(UpdateLeadersHandCount.class, this::on);
        model.addEventListener(UpdateFaithPoints.class, this::on);
        model.addEventListener(UpdateVictoryPoints.class, this::on);
        model.addEventListener(UpdatePlayerStatus.class, this::on);
        model.addEventListener(UpdateDevCardSlot.class, this::on);

        /* Card */
        model.addEventListener(UpdateLeader.class, this::on);

        /* ResourceContainer */
        model.addEventListener(UpdateResourceContainer.class, this::on);

        /* DevCardGrid */
        model.addEventListener(UpdateDevCardGrid.class, this::on);

        /* Market */
        model.addEventListener(UpdateMarket.class, this::on);

        /* FaithTrack */
        model.addEventListener(UpdateVaticanSection.class, this::on);

        /* SoloGame */
        model.addEventListener(UpdateActionToken.class, this::on);
    }

    @Override
    public void unregisterToModelGameContext(EventDispatcher model) {
        /* Lobby and GameContext */
        model.removeEventListener(ErrAction.class, this::on);

        /* Lobby (only) */
        model.removeEventListener(UpdateBookedSeats.class, this::on);
        model.removeEventListener(UpdateJoinGame.class, this::on);
        model.removeEventListener(ResGoodbye.class, this::on);

        /* GameContext (only) */
        model.removeEventListener(UpdateSetupDone.class, this::on);

        /* Game */
        model.removeEventListener(UpdateGameStart.class, this::on);
        model.removeEventListener(UpdateCurrentPlayer.class, this::on);
        model.removeEventListener(UpdateGameResume.class, this::on);
        model.removeEventListener(UpdateLastRound.class, this::on);
        model.removeEventListener(UpdateGameEnd.class, this::on);

        /* Player */
        model.removeEventListener(UpdatePlayer.class, this::on);
        model.removeEventListener(UpdateLeadersHandCount.class, this::on);
        model.removeEventListener(UpdateFaithPoints.class, this::on);
        model.removeEventListener(UpdateVictoryPoints.class, this::on);
        model.removeEventListener(UpdatePlayerStatus.class, this::on);
        model.removeEventListener(UpdateDevCardSlot.class, this::on);

        /* Card */
        model.removeEventListener(UpdateLeader.class, this::on);

        /* ResourceContainer */
        model.removeEventListener(UpdateResourceContainer.class, this::on);

        /* DevCardGrid */
        model.removeEventListener(UpdateDevCardGrid.class, this::on);

        /* Market */
        model.removeEventListener(UpdateMarket.class, this::on);

        /* FaithTrack */
        model.removeEventListener(UpdateVaticanSection.class, this::on);

        /* SoloGame */
        model.removeEventListener(UpdateActionToken.class, this::on);
    }

    @Override
    public void registerToPrivateModelPlayer(EventDispatcher model) {
        model.addEventListener(UpdateLeadersHand.class, this::on);
    }

    @Override
    public void unregisterToPrivateModelPlayer(EventDispatcher model) {
        model.removeEventListener(UpdateLeadersHand.class, this::on);
    }

    @Override
    public void on(MVEvent event) {
        // TODO: Implement all listeners in CliView and link them to Cli
        System.out.println("Temporary listener.");
    }
}
