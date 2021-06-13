package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.UpdateBookedSeats;
import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;
import it.polimi.ingsw.common.events.mvevents.UpdateJoinGame;
import it.polimi.ingsw.common.events.mvevents.UpdateLeadersHand;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNickname;
import it.polimi.ingsw.common.events.vcevents.ReqJoin;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;

import java.util.concurrent.atomic.AtomicBoolean;

import static it.polimi.ingsw.client.cli.Cli.center;

public class InputNicknameState extends CliState {
    private final String title;

    public InputNicknameState(String title) {
        this.title = title;
    }

    @Override
    public void render(Cli cli) {
        cli.getOut().println();
        cli.getOut().println(center(String.format("~ %s ~", title)));

        cli.getOut().println();
        AtomicBoolean valid = new AtomicBoolean(false);
        while (!valid.get()) {
            valid.set(true);
            cli.prompt("Nickname").ifPresentOrElse(nickname -> {
                if (!nickname.isBlank()) {
                    cli.getViewModel().setLocalPlayerNickname(nickname);
                    cli.dispatch(new ReqJoin(nickname));
                } else
                    valid.set(false);
            }, () -> cli.dispatch(new ReqQuit()));
        }
    }

    @Override
    public void on(Cli cli, ErrNickname event) {
        // don't call super.on
        cli.repeatState(String.format("Nickname is invalid. Reason: %s.", event.getReason().toString().toLowerCase()));
    }

    @Override
    public void on(Cli cli, UpdateBookedSeats event) {
        if (event.canPrepareNewGame().equals(cli.getViewModel().getLocalPlayerNickname()))
            cli.setState(new InputPlayersCountState());
        else if (!cli.getViewModel().getLocalPlayerNickname().equals(""))
            cli.getOut().printf("%d players waiting for a new game...", event.getBookedSeats());
    }

    @Override
    public void on(Cli cli, UpdateJoinGame event) {
        if (!cli.isOffline())
            cli.getOut().printf("A new player joined the game! Getting to %d...%n%n", event.getPlayersCount());
    }

    @Override
    public void on(Cli cli, UpdateCurrentPlayer event) {
        // having this overriding may prove necessary:
        // if the setState isn't fast enough, the next event after UpdateGame is CurrentPlayer and
        // this means it might not get handled in WaitingAfterTurnState
        super.on(cli, event);
        
        if (cli.getViewModel().isResumedGame()) {
            if (event.getPlayer().equals(cli.getViewModel().getLocalPlayerNickname()))
                cli.setState(new TurnBeforeActionState());
            else
                cli.setState(new WaitingAfterTurnState());
        }
    }
    
    @Override
    public void on(Cli cli, UpdateLeadersHand event) {
        super.on(cli, event);

        if (cli.getViewModel().isResumedGame())
            throw new RuntimeException("UpdateLeadersHand after resumed game.");

        cli.promptPause();
        cli.setState(new SetupLeadersState());
    }
}
