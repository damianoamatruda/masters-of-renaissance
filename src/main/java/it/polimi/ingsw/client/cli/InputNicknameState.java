package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.viewmodel.PlayerData;
import it.polimi.ingsw.common.events.mvevents.UpdateBookedSeats;
import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;
import it.polimi.ingsw.common.events.mvevents.UpdateGame;
import it.polimi.ingsw.common.events.mvevents.UpdateJoinGame;
import it.polimi.ingsw.common.events.mvevents.UpdateLeadersHand;
import it.polimi.ingsw.common.events.mvevents.UpdatePlayer;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNickname;
import it.polimi.ingsw.common.events.vcevents.ReqJoin;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;
import it.polimi.ingsw.common.reducedmodel.ReducedPlayerSetup;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static it.polimi.ingsw.client.cli.Cli.center;

public class InputNicknameState extends CliController {
    private final String title;

    public InputNicknameState(String title) {
        this.title = title;
    }

    @Override
    public void render() {
        cli.getOut().println();
        cli.getOut().println(center(String.format("~ %s ~", title)));

        cli.getOut().println();
        AtomicBoolean valid = new AtomicBoolean(false);
        while (!valid.get()) {
            valid.set(true);
            cli.prompt("Nickname").ifPresentOrElse(nickname -> {
                if (!nickname.isBlank()) {
                    vm.setLocalPlayerNickname(nickname);
                    cli.getUi().dispatch(new ReqJoin(nickname));
                } else
                    valid.set(false);
            }, () -> cli.getUi().dispatch(new ReqQuit()));
        }

        /* Upon UpdateGame reception the client will know
           whether the setup is still ongoing or not.
           Upon localplayer's UpdatePlayer reception it will know
           which state to change to.
           If the leaders hand still needs to be chosen,
           the client will need to wait for UpdateLeadersHand.
           If the setup is done, the client needs to wait until
           UpdateCurrentPlayer to know which state to change to. */
    }

    @Override
    public void on(ErrNickname event) {
        cli.reloadController(String.format("Nickname is invalid. Reason: %s.", event.getReason().toString().toLowerCase()));
    }

    @Override
    public void on(UpdateBookedSeats event) {
        if (event.canPrepareNewGame().equals(vm.getLocalPlayerNickname()))
            cli.setController(new InputPlayersCountState());
        else if (!vm.getLocalPlayerNickname().equals("")) // if the nickname isn't set don't print updates (do not disturb)
            cli.getOut().printf("%d players waiting for a new game...", event.getBookedSeats());
    }

    @Override
    public void on(UpdateJoinGame event) {
        if (!cli.getUi().isOffline())
            cli.getOut().printf("A new player joined the game! Getting to %d...%n%n", event.getPlayersCount());
    }

    @Override
    public void on(UpdateGame event) {
        super.on(event);

        setNextState();
    }

    @Override
    public void on(UpdatePlayer event) {
        super.on(event);

        setNextState();
    }

    @Override
    public void on(UpdateCurrentPlayer event) {
        super.on(event);

        setNextState();
    }
    
    @Override
    public void on(UpdateLeadersHand event) {
        super.on(event);

        setNextState();
    }

    private void setNextState() {
        vm.isSetupDone().ifPresent(isSetupDone -> { // received UpdateGame
            if (isSetupDone) // setup is done
                vm.getCurrentPlayer().ifPresent(nick -> { // received UpdateCurrentPlayer
                    if (nick.equals(vm.getLocalPlayerNickname()))
                        cli.setController(new TurnBeforeActionState());
                    
                    cli.setController(new WaitingAfterTurnState());
                });
            else // setup not done
                vm.getPlayerData(vm.getLocalPlayerNickname()).ifPresent(pd -> {
                    pd.getSetup().ifPresent(setup -> { // received local player's setup
                        if (!setup.hasChosenLeaders())
                            cli.setController(new SetupLeadersState());
                        if (!setup.hasChosenResources())
                            cli.setController(new SetupResourcesState());
                    });
                });
        });
    }
}
