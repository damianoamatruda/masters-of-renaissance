package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateAction.ActionType;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction.ErrActionReason;
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
        try {
            Thread.sleep(200);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        ViewModel vm = cli.getViewModel();
        
        cli.getOut().println("\nChoosing starting leaders hand.");
        cli.getOut().println("Please input leader card IDs from the ones assigned to you.\n");

        List<Integer> leaders = new ArrayList<>();

        int chosen = 0;
        while (chosen < vm.getLocalPlayerData().getSetup().getChosenLeadersCount()) {
            String input = cli.prompt((leadersToChoose - chosen) + " leader cards left to be chosen");
            try {
                int id = Integer.parseInt(input);
                leaders.add(id);
                chosen++;
            } catch (NumberFormatException e) {
                cli.getOut().println("Please input a numerical ID.");
            }
        }

        cli.dispatch(new ReqChooseLeaders(leaders));
    }

    @Override
    public void on(Cli cli, ErrAction event) {
        if (event.getReason() != ErrActionReason.LATE_SETUP_ACTION)
            throw new RuntimeException("Leader setup: ErrAction received with reason not LATE_SETUP_ACTION.");
            
        if (cli.getViewModel().getCurrentPlayer() == cli.getViewModel().getLocalPlayerNickname())
            cli.setState(new TurnBeforeActionState());
        else
            cli.setState(new WaitingAfterTurnState());
    }

    @Override
    public void on(Cli cli, UpdateAction event) {
        if (event.getAction() != ActionType.CHOOSE_LEADERS)
            throw new RuntimeException("Leader setup: UpdateAction received with action type not CHOOSE_LEADERS.");

        int choosable = cli.getViewModel().getLocalPlayerData().getSetup().getInitialResources();

        if (choosable > 0)
            cli.setState(new SetupResourcesState(choosable));
        else if (cli.getViewModel().getCurrentPlayer().equals(cli.getViewModel().getLocalPlayerNickname()))
            cli.setState(new TurnBeforeActionState());
        else
            cli.setState(new WaitingAfterTurnState());
    }
}
