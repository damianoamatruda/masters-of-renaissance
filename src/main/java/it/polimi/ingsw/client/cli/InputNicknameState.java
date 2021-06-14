package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.UpdateBookedSeats;
import it.polimi.ingsw.common.events.mvevents.UpdateJoinGame;
import it.polimi.ingsw.common.events.mvevents.UpdateLeadersHand;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNickname;
import it.polimi.ingsw.common.events.vcevents.ReqJoin;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;

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
                    cli.getViewModel().setLocalPlayerNickname(nickname);
                    cli.getUi().dispatch(new ReqJoin(nickname));
                } else
                    valid.set(false);
            }, () -> cli.getUi().dispatch(new ReqQuit()));
        }
    }

    @Override
    public void on(ErrNickname event) {
        // don't call super.on
        cli.reloadController(String.format("Nickname is invalid. Reason: %s.", event.getReason().toString().toLowerCase()));
    }

    @Override
    public void on(UpdateBookedSeats event) {
        if (event.canPrepareNewGame().equals(cli.getViewModel().getLocalPlayerNickname()))
            cli.setController(new InputPlayersCountState());
        else if (!cli.getViewModel().getLocalPlayerNickname().equals(""))
            cli.getOut().printf("%d players waiting for a new game...", event.getBookedSeats());
    }

    @Override
    public void on(UpdateJoinGame event) {
        if (!cli.getUi().isOffline())
            cli.getOut().printf("A new player joined the game! Getting to %d...%n%n", event.getPlayersCount());
    }
    
    @Override
    public void on(UpdateLeadersHand event) {
        super.on(event);

        if (cli.getViewModel().getPlayerNicknames().isEmpty())
        cli.promptPause();
        cli.setController(new SetupLeadersState());
    }
}
