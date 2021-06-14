package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;

public class WaitingAfterTurnState extends CliState {
    private final Cli cli = Cli.getInstance();

    @Override
    public void render(Cli cli) {
        if (!cli.getViewModel().isLastRound())
            cli.getOut().println("Please wait for other players to end their turn...");
        else
            cli.getOut().println("You have played your last turn. Waiting for others to finish...");
    }

    @Override
    public void on(UpdateCurrentPlayer event) {
        super.on(event);
        if (event.getPlayer().equals(cli.getViewModel().getLocalPlayerNickname())) {
            cli.getOut().println();
            cli.getOut().println("It's your turn.");
            cli.promptPause();
            cli.setState(new TurnBeforeActionState());
        }
    }
}
