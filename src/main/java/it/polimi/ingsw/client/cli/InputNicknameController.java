package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.vcevents.ReqJoin;

import java.util.concurrent.atomic.AtomicBoolean;

import static it.polimi.ingsw.client.cli.Cli.center;

public class InputNicknameController extends CliController {
    private final String title;

    public InputNicknameController(String title) {
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
                    vm.setLocalPlayer(nickname);
                    cli.getUi().dispatch(new ReqJoin(nickname));
                } else
                    valid.set(false);
            }, () -> {
                if (cli.getUi().isOffline())
                    cli.setController(new MainMenuController());
                else {
                    cli.getUi().closeClient();
                    cli.setController(new PlayOnlineController());
                }
            });
        }
    }

    @Override
    public void on(UpdateBookedSeats event) {
        if (vm.getLocalPlayer().isPresent() && event.canPrepareNewGame().equals(vm.getLocalPlayer().get()))
            cli.setController(new WaitingBeforeGameController());
        else if (vm.getLocalPlayer().isPresent()) // if the nickname isn't set don't print updates (do not disturb)
            cli.getOut().printf("%d players waiting for a new game...", event.getBookedSeats());
    }

    @Override
    public void on(UpdateCurrentPlayer event) {
        super.on(event);

        setNextState();
    }

    @Override
    public void on(UpdateJoinGame event) {
        if (!cli.getUi().isOffline())
            cli.alert(String.format("A new player joined the game! Getting to %d...", event.getPlayersCount()));
    }

    @Override
    public void on(UpdateGame event) {
        super.on(event);

        if (event.isMandatoryActionDone() && vm.getLocalPlayer().equals(vm.getCurrentPlayer()))
            cli.setController(new TurnAfterActionController());
        else
            setNextState();
    }
    
    @Override
    public void on(UpdateLeadersHand event) {
        super.on(event);

        setNextState();
    }

    @Override
    public void on(UpdateSetupDone event) {
        super.on(event);

        if (vm.getPlayers().size() > 1) {
            cli.getOut().println();
            cli.getOut().println("All players have finished their setup! Game starting...");
        }
        
        setNextState();
    }
}
