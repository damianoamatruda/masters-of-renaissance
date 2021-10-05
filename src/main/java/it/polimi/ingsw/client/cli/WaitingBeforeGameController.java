package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNewGame;
import it.polimi.ingsw.common.events.vcevents.ReqNewGame;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;

import static it.polimi.ingsw.client.cli.Cli.center;

public class WaitingBeforeGameController extends CliController {
    @Override
    public void render() {
        if (cli.getUi().isOffline()) {
            cli.getUi().dispatch(new ReqNewGame(1));
            return;
        }

        cli.getOut().println();
        cli.getOut().println(center("~ Play Online ~"));

        cli.getOut().println();
        cli.getOut().println(center("You can prepare a new game."));

        cli.getOut().println();

        boolean valid = false;
        while (!valid) {
            valid = true;
            try {
                cli.promptInt("Players count").ifPresentOrElse(count -> {
                    if (count <= 0)
                        throw new NumberFormatException();
                    cli.getUi().dispatch(new ReqNewGame(count));
                }, () -> cli.getUi().dispatch(new ReqQuit()));
            } catch (NumberFormatException e) {
                cli.getOut().println(center("Please input an integer greater than 0."));
                valid = false;
            }
        }
    }

    @Override
    public void on(ErrNewGame event) {
        if (event.isInvalidPlayersCount())
            cli.reloadController("Invalid players count.");
        else {
            cli.getOut().println(center("You cannot choose the players' count for this game."));
            setNextState();
        }
    }

    @Override
    public void on(UpdateBookedSeats event) {
        cli.getOut().println();
        cli.getOut().println(center("%d players waiting for a new game...", event.getBookedSeats()));
    }

    @Override
    public void on(UpdateJoinGame event) {
        if (event.getPlayersCount() > 1) {
            cli.getOut().println();
            cli.getOut().println(center(String.format("A game has been prepared for %d players!", event.getPlayersCount())));
        }
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
    public void on(UpdateCurrentPlayer event) {
        super.on(event);

        setNextState();
    }

    @Override
    public void on(UpdateLeadersHand event) {
        super.on(event);

        setNextState();
    }
}
