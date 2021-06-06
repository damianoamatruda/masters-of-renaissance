package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.LeadersHand;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateAction.ActionType;
import it.polimi.ingsw.common.events.mvevents.UpdateSetupDone;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction.ErrActionReason;
import it.polimi.ingsw.common.events.mvevents.errors.ErrInitialChoice;
import it.polimi.ingsw.common.events.vcevents.ReqChooseLeaders;

import java.util.ArrayList;
import java.util.List;

public class SetupLeadersState extends CliState {
    private final int leadersToChoose;

    public SetupLeadersState(int leadersToChoose) {
        this.leadersToChoose = leadersToChoose;
    }

    @Override
    public void render(Cli cli) {
        ViewModel vm = cli.getViewModel();

        if (vm.getLocalPlayerData().getLeadersHand() != null)
            new LeadersHand(vm.getPlayerLeaderCards(vm.getLocalPlayerNickname())).render(cli);

        cli.getOut().println("\nChoosing starting leaders hand.");
        cli.getOut().println("Please input leader card IDs from the ones assigned to you.\n");

        List<Integer> leaders = new ArrayList<>();

        int chosen = 0;
        while (chosen < vm.getLocalPlayerData().getSetup().getChosenLeadersCount()) {
            int id = cli.promptInt((leadersToChoose - chosen) + " leader cards left to be chosen");
            leaders.add(id);
            chosen++;
        }

        cli.dispatch(new ReqChooseLeaders(leaders));
    }

    @Override
    public void on(Cli cli, ErrAction event) {
        if (event.getReason() != ErrActionReason.LATE_SETUP_ACTION)
            throw new RuntimeException("Leader setup: ErrAction received with reason not LATE_SETUP_ACTION.");

        if (cli.getViewModel().getCurrentPlayer().equals(cli.getViewModel().getLocalPlayerNickname()))
            cli.setState(new TurnBeforeActionState());
        else
            cli.setState(new WaitingAfterTurnState());
    }

    @Override
    public void on(Cli cli, ErrInitialChoice event) {
        // TODO: Share method with SetupResourcesState

        // repeats either SetupLeadersState or SetupResourcesState
        // if it doesn't, that's really bad
        cli.repeatState(event.isLeadersChoice() ? // if the error is from the initial leaders choice
                event.getMissingLeadersCount() == 0 ?
                        "Leaders already chosen" :        // if the count is zero it means the leaders were already chosen
                        String.format("Not enough leaders chosen: %d missing.", event.getMissingLeadersCount()) :
                "Resources already chosen");          // else it's from the resources choice
    }

    @Override
    public void on(Cli cli, UpdateAction event) {
        if (event.getAction() != ActionType.CHOOSE_LEADERS)
            throw new RuntimeException("Leader setup: UpdateAction received with action type not CHOOSE_LEADERS.");
        if (!event.getPlayer().equals(cli.getViewModel().getLocalPlayerNickname()))
            return;

        int choosable = cli.getViewModel().getLocalPlayerData().getSetup().getInitialResources();

        if (choosable > 0)
            cli.setState(new SetupResourcesState(choosable));
        else if (cli.getViewModel().getCurrentPlayer().equals(cli.getViewModel().getLocalPlayerNickname()))
            cli.setState(new TurnBeforeActionState());
        else
            cli.setState(new WaitingAfterTurnState());
    }

    @Override
    public void on(Cli cli, UpdateSetupDone event) {
        super.on(cli, event);

        if (cli.getViewModel().getCurrentPlayer().equals(cli.getViewModel().getLocalPlayerNickname()))
            cli.setState(new TurnBeforeActionState());
        else
            cli.setState(new WaitingAfterTurnState());
    }
}
