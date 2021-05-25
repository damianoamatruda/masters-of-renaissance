package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateAction.ActionType;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction.ErrActionReason;
import it.polimi.ingsw.common.events.vcevents.ReqChooseResources;

import java.util.Map;

public class SetupResourcesState extends CliState {
    private final int choosable;

    public SetupResourcesState(int choosable) {
        this.choosable = choosable;
    }

    @Override
    public void render(Cli cli) {
        cli.getOut().println("\nChoosing starting resources.");
        cli.getOut().println("You can choose " + choosable + " resources. What's your choice?");
        
        Map<Integer, Map<String, Integer>> shelves = cli.promptShelves();

        cli.dispatch(new ReqChooseResources(shelves));
    }

    @Override
    public void on(Cli cli, ErrAction event) {
        if (event.getReason() != ErrActionReason.LATE_SETUP_ACTION)
            throw new RuntimeException("Resources setup: ErrAction received with reason not LATE_SETUP_ACTION.");
            
        if (cli.getCache().getGameData().getCurrentPlayer() == cli.getCache().getUiData().getLocalPlayerNickname())
            cli.setState(new TurnBeforeActionState());
        else
            cli.setState(new WaitingAfterTurnState());
    }

    // ErrObjectNotOwned handled in clistate
    // ErrNoSuchEntity handled in clistate
    // ErrResourceReplacement handled in clistate
    // ErrReplacedTransRecipe handled in clistate
    // ErrInitialChoice handled in clistate
    // ErrResourceTransfer handled in clistate

    @Override
    public void on(Cli cli, UpdateAction event) {
        if (event.getAction() != ActionType.CHOOSE_LEADERS)
            throw new RuntimeException("Resources setup: UpdateAction received with action type not CHOOSE_LEADERS.");

        if (cli.getCache().getGameData().getCurrentPlayer() == cli.getCache().getUiData().getLocalPlayerNickname())
            cli.setState(new TurnBeforeActionState());
        else
            cli.setState(new WaitingAfterTurnState());
    }
}
