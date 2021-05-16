package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.EventEmitter;
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
    public void registerToModelLobby(EventEmitter model) {
        model.register(ErrAction.class, this::on);
        model.register(UpdateBookedSeats.class, this::on);
        model.register(UpdateJoinGame.class, this::on);
        model.register(ResGoodbye.class, this::on);
    }

    @Override
    public void unregisterToModelLobby(EventEmitter model) {
        model.unregister(ErrAction.class, this::on);
        model.unregister(UpdateBookedSeats.class, this::on);
        model.unregister(UpdateJoinGame.class, this::on);
        model.unregister(ResGoodbye.class, this::on);
    }

    @Override
    public void registerToModelGameContext(EventEmitter model) {
        model.register(ErrAction.class, this::on);
        model.register(UpdateSetupDone.class, this::on);

        /* Game */
        model.register(UpdateGameStart.class, this::on);
        model.register(UpdateCurrentPlayer.class, this::on);
        model.register(UpdateGameResume.class, this::on);
        model.register(UpdateLastRound.class, this::on);
        model.register(UpdateGameEnd.class, this::on);

        /* Player */
        model.register(UpdatePlayer.class, this::on);
        model.register(UpdateLeadersHandCount.class, this::on);
        model.register(UpdateFaithPoints.class, this::on);
        model.register(UpdateVictoryPoints.class, this::on);
        model.register(UpdatePlayerStatus.class, this::on);
        model.register(UpdateDevCardSlot.class, this::on);

        /* Card */
        model.register(UpdateLeader.class, this::on);

        /* ResourceContainer */
        model.register(UpdateResourceContainer.class, this::on);

        /* DevCardGrid */
        model.register(UpdateDevCardGrid.class, this::on);

        /* Market */
        model.register(UpdateMarket.class, this::on);

        /* FaithTrack */
        model.register(UpdateVaticanSection.class, this::on);

        /* SoloGame */
        model.register(UpdateActionToken.class, this::on);
    }

    @Override
    public void unregisterToModelGameContext(EventEmitter model) {
        /* Lobby and GameContext */
        model.unregister(ErrAction.class, this::on);

        /* Lobby (only) */
        model.unregister(UpdateBookedSeats.class, this::on);
        model.unregister(UpdateJoinGame.class, this::on);
        model.unregister(ResGoodbye.class, this::on);

        /* GameContext (only) */
        model.unregister(UpdateSetupDone.class, this::on);

        /* Game */
        model.unregister(UpdateGameStart.class, this::on);
        model.unregister(UpdateCurrentPlayer.class, this::on);
        model.unregister(UpdateGameResume.class, this::on);
        model.unregister(UpdateLastRound.class, this::on);
        model.unregister(UpdateGameEnd.class, this::on);

        /* Player */
        model.unregister(UpdatePlayer.class, this::on);
        model.unregister(UpdateLeadersHandCount.class, this::on);
        model.unregister(UpdateFaithPoints.class, this::on);
        model.unregister(UpdateVictoryPoints.class, this::on);
        model.unregister(UpdatePlayerStatus.class, this::on);
        model.unregister(UpdateDevCardSlot.class, this::on);

        /* Card */
        model.unregister(UpdateLeader.class, this::on);

        /* ResourceContainer */
        model.unregister(UpdateResourceContainer.class, this::on);

        /* DevCardGrid */
        model.unregister(UpdateDevCardGrid.class, this::on);

        /* Market */
        model.unregister(UpdateMarket.class, this::on);

        /* FaithTrack */
        model.unregister(UpdateVaticanSection.class, this::on);

        /* SoloGame */
        model.unregister(UpdateActionToken.class, this::on);
    }

    @Override
    public void registerToPrivateModelPlayer(EventEmitter model) {
        model.register(UpdateLeadersHand.class, this::on);
    }

    @Override
    public void unregisterToPrivateModelPlayer(EventEmitter model) {
        model.unregister(UpdateLeadersHand.class, this::on);
    }

    @Override
    public void on(MVEvent event) {
        // TODO: Implement all listeners in CliView and link them to Cli
        System.out.println("Temporary listener.");
    }
}
