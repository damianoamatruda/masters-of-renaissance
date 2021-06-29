package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.vcevents.ReqJoin;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;

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
                    vm.setLocalPlayerNickname(nickname);
                    cli.getUi().dispatch(new ReqJoin(nickname));
                } else
                    valid.set(false);
            }, () -> cli.getUi().dispatch(new ReqQuit()));
        }
    }

    @Override
    public void on(UpdateBookedSeats event) {
        if (event.canPrepareNewGame().equals(vm.getLocalPlayerNickname()))
            cli.setController(new WaitingBeforeGameController(), false);
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

        new Thread(() -> {
            cli.promptPause();
            setNextState();
        }).start();
    }
}
