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
import it.polimi.ingsw.common.events.vcevents.ReqQuit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SetupLeadersState extends CliState {
    @Override
    public void render(Cli cli) {
        ViewModel vm = cli.getViewModel();

        int leadersToChoose = cli.getViewModel()
                .getLocalPlayerData().orElseThrow()
                .getSetup().orElseThrow()
                .getChosenLeadersCount();

        if (vm.getLocalPlayerData().orElseThrow().getLeadersHand() != null)
            new LeadersHand(vm.getPlayerLeaderCards(vm.getLocalPlayerNickname())).render(cli);

        cli.getOut().println();
        cli.getOut().print(Cli.center("Choosing starting leaders hand."));

        List<Integer> leaders = new ArrayList<>();

        AtomicBoolean done = new AtomicBoolean(false);
        AtomicInteger chosen = new AtomicInteger();
        while (!done.get()) {
            cli.promptInt((leadersToChoose - chosen.get()) + " leader cards left to be chosen").ifPresentOrElse(id -> {
                leaders.add(id);
                chosen.getAndIncrement();
                done.set(chosen.get() >= vm.getLocalPlayerData().orElseThrow().getSetup().orElseThrow().getChosenLeadersCount());
            }, () -> {
                leaders.clear();
                done.set(true);
            });
        }

        if (!leaders.isEmpty())
            cli.dispatch(new ReqChooseLeaders(leaders));
        else
            cli.dispatch(new ReqQuit());
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
        if (event.getAction() != ActionType.CHOOSE_LEADERS && event.getPlayer().equals(cli.getViewModel().getLocalPlayerNickname()))
            throw new RuntimeException("Leader setup: UpdateAction received with action type not CHOOSE_LEADERS.");
        if (!event.getPlayer().equals(cli.getViewModel().getLocalPlayerNickname()))
            return;

        if (cli.getViewModel().getLocalPlayerData().orElseThrow().getSetup().orElseThrow().getInitialResources() > 0)
            cli.setState(new SetupResourcesState());
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
