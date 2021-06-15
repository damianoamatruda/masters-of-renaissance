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
                    vm.setLocalPlayerNickname(nickname);
                    cli.getUi().dispatch(new ReqJoin(nickname));
                } else
                    valid.set(false);
            }, () -> cli.getUi().dispatch(new ReqQuit()));
        }
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
    public void on(UpdateLeadersHand event) {
        super.on(event);

        /* Client receives UpdateGame, UpdatePlayer and then UpdateLeadersHand.
           Upon UpdateGame reception it will know
           whether the setup is still ongoing or not.
           Upon localplayer's UpdatePlayer reception it will know
           which state to change to.
           If the leaders hand still needs to be chosen,
           the client will need to wait for UpdateLeadersHand. */
        if (vm.getPlayerNicknames().isEmpty())
        cli.promptPause();
        cli.setController(new SetupLeadersState());
    }
}
