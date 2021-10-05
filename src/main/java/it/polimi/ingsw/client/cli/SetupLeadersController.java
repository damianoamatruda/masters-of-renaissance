package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.LeadersHand;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateAction.ActionType;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction.ErrActionReason;
import it.polimi.ingsw.common.events.vcevents.ReqChooseLeaders;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static it.polimi.ingsw.client.cli.Cli.center;

public class SetupLeadersController extends SetupController {
    @Override
    public void render() {
        cli.getOut().println();
        cli.getOut().println(center(String.format("~ Choose %s leader cards ~", vm.getLocalPlayer().flatMap(vm::getPlayer).orElseThrow().getSetup().getChosenLeadersCount())));

        int leadersToChoose = vm
                .getLocalPlayer()
                .flatMap(vm::getPlayer).orElseThrow()
                .getSetup()
                .getChosenLeadersCount();

        List<ReducedLeaderCard> lCards = vm.getLocalPlayer().map(vm::getPlayerLeaderCards).orElseThrow();
        if (lCards.isEmpty()) {
            cli.alert("No leader cards to choose from. Setup cannot continue.");
            cli.setController(new MainMenuController());
            return;
        }

        cli.getOut().println();
        new LeadersHand(lCards).render();

        cli.getOut().println();

        List<Integer> leaders = new ArrayList<>();

        AtomicBoolean done = new AtomicBoolean(false);
        AtomicInteger chosen = new AtomicInteger();
        while (!done.get()) {
            cli.promptInt((leadersToChoose - chosen.get()) + " leader cards left to be chosen").ifPresentOrElse(id -> {
                leaders.add(id);
                chosen.getAndIncrement();
                done.set(chosen.get() >= vm.getLocalPlayer().flatMap(vm::getPlayer).orElseThrow().getSetup().getChosenLeadersCount());
            }, () -> {
                leaders.clear();
                done.set(true);
            });
        }

        if (!leaders.isEmpty())
            cli.getUi().dispatch(new ReqChooseLeaders(leaders));
        else
            cli.getUi().dispatch(new ReqQuit());
    }

    @Override
    public void on(ErrAction event) {
        if (event.getReason() != ErrActionReason.LATE_SETUP_ACTION)
            throw new RuntimeException("Leader setup: ErrAction received with reason not LATE_SETUP_ACTION.");

        super.on(event);
    }

    @Override
    public void on(UpdateAction event) {
        if (event.getAction() != ActionType.CHOOSE_LEADERS && vm.getLocalPlayer().isPresent() && event.getPlayer().equals(vm.getLocalPlayer().get()))
            throw new RuntimeException("Leader setup: UpdateAction received with action type not CHOOSE_LEADERS.");
        
        vm.getLocalPlayer().ifPresent(p -> {
            if (p.equals(event.getPlayer()) && isResourceSetupAvailable())
                setNextState();
        });
    }
}
