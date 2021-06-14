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

import static it.polimi.ingsw.client.cli.Cli.center;

public class LeaderActionsState extends CliController {
    private final CliController sourceState;
    private int leaderId;

    public LeaderActionsState(CliController sourceState) {
        this.sourceState = sourceState;
    }

    public void render() {
        cli.getOut().println();
        cli.getOut().println(center("~ Leader Actions ~"));

        cli.getOut().println();
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('A', new Menu.Entry("Activate leader", cli1 -> executeLeaderAction(true)));
        entries.put('D', new Menu.Entry("Discard leader", cli1 -> executeLeaderAction(false)));
        new Menu(entries, cli1 -> cli1.setState(sourceState)).render();
    }

    private void executeLeaderAction(boolean isActivate) {
        new LeadersHand(cli.getViewModel().getPlayerLeaderCards(cli.getViewModel().getLocalPlayerNickname())).render();

        cli.getOut().println();
        cli.promptInt("Leader").ifPresentOrElse(leaderId -> {
            this.leaderId = leaderId;
            cli.getUi().dispatch(new ReqLeaderAction(leaderId, isActivate));
        }, () -> cli.setState(this));
    }

    @Override
    public void on(ErrActiveLeaderDiscarded event) {
        cli.repeatState(String.format("Active leader %d tried to be discarded.", leaderId)); // TODO: Insert leader ID into event
    }

    @Override
    public void on(ErrCardRequirements event) {
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
    public void on(UpdateAction event) {
        cli.promptPause();
        cli.setState(sourceState);
    }
}
