package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.LeadersHand;
import it.polimi.ingsw.client.cli.components.Menu;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrActiveLeaderDiscarded;
import it.polimi.ingsw.common.events.mvevents.errors.ErrCardRequirements;
import it.polimi.ingsw.common.events.vcevents.ReqLeaderAction;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirementEntry;

import java.util.LinkedHashMap;
import java.util.Map;

public class LeaderActionsState extends CliState {
    private final CliState sourceState;
    private int leaderId;

    public LeaderActionsState(CliState sourceState) {
        this.sourceState = sourceState;
    }

    public void render(Cli cli) {
        cli.getOut().println();
        cli.getOut().print(Cli.center("Leader actions:"));

        cli.getOut().println();
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('A', new Menu.Entry("Activate leader", cli1 -> executeLeaderAction(cli1, true)));
        entries.put('D', new Menu.Entry("Discard leader", cli1 -> executeLeaderAction(cli1, false)));
        new Menu(entries, cli1 -> cli1.setState(sourceState)).render(cli);
    }

    private void executeLeaderAction(Cli cli, boolean isActivate) {
        new LeadersHand(cli.getViewModel().getPlayerLeaderCards(cli.getViewModel().getLocalPlayerNickname())).render(cli);

        cli.getOut().println();
        cli.promptInt("Leader").ifPresentOrElse(leaderId -> {
            this.leaderId = leaderId;
            cli.dispatch(new ReqLeaderAction(leaderId, isActivate));
        }, () -> cli.setState(this));
    }

    @Override
    public void on(Cli cli, ErrActiveLeaderDiscarded event) {
        cli.repeatState(String.format("Active leader %d tried to be discarded.", leaderId)); // TODO: Insert leader ID into event
    }

    @Override
    public void on(Cli cli, ErrCardRequirements event) {
        String msg;
        if (event.getMissingDevCards().isPresent()) {
            msg = String.format("\nPlayer %s does not satisfy the following entries:", cli.getViewModel().getLocalPlayerNickname());

            for (ReducedDevCardRequirementEntry e : event.getMissingDevCards().get())
                msg = msg.concat(String.format("\nColor %s, level %d, missing %s", e.getColor(), e.getLevel(), e.getAmount()));
        } else {
            msg = String.format("\nPlayer %s lacks the following resources by the following amounts:", cli.getViewModel().getLocalPlayerNickname());

            for (Map.Entry<String, Integer> e : event.getMissingResources().get().entrySet())
                msg = msg.concat(String.format("\nResource %s, missing %s", e.getKey(), e.getValue()));
        }

        cli.repeatState(msg);
    }

    @Override
    public void on(Cli cli, UpdateAction event) {
        cli.getOut().println();
        cli.promptPause();
        cli.setState(new TurnBeforeActionState());
    }
}