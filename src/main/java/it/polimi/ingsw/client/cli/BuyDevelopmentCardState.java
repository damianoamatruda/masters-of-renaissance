package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.DevCardGrid;
import it.polimi.ingsw.client.cli.components.DevSlots;
import it.polimi.ingsw.client.cli.components.ResourceContainers;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrBuyDevCard;
import it.polimi.ingsw.common.events.mvevents.errors.ErrCardRequirements;
import it.polimi.ingsw.common.events.vcevents.ReqBuyDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardGrid;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirementEntry;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.cli.Cli.center;

public class BuyDevelopmentCardState extends CliState {
    private final CliState sourceState;
    private ViewModel vm;
    private ReducedDevCardGrid grid;
    private String color;
    private int level;
    private Map<String, Integer> cost;
    private Map<Integer, Map<String, Integer>> shelves;
    private int slot;

    public BuyDevelopmentCardState(CliState sourceState) {
        this.sourceState = sourceState;

        this.shelves = new HashMap<>();
    }

    @Override
    public void render(Cli cli) {
        vm = cli.getViewModel();
        
        cli.getOut().println();
        cli.getOut().println(center("~ Buy a Development Card ~"));

        grid = vm.getDevCardGrid().orElseThrow();

        cli.getOut().println();
        new DevCardGrid(grid).render(cli);
        cli.getOut().println();
        new ResourceContainers(vm.getLocalPlayerNickname(),
                vm.getPlayerWarehouseShelves(vm.getLocalPlayerNickname()),
                vm.getPlayerDepots(vm.getLocalPlayerNickname()),
                vm.getPlayerStrongbox(vm.getLocalPlayerNickname()).orElse(null))
                .render(cli);

        new DevSlots(vm.getPlayerDevelopmentSlots(vm.getLocalPlayerNickname())).render(cli);

        chooseColor(cli);
    }

    private void chooseColor(Cli cli) {
        Set<String> allowedColors = grid.getTopCards().keySet().stream()
            .map(String::toLowerCase)
            .collect(Collectors.toUnmodifiableSet());

        AtomicBoolean valid = new AtomicBoolean(false);
        while (!valid.get()) {
            valid.set(true);
            cli.prompt("Card color").ifPresentOrElse(color -> {
                if (!allowedColors.contains(color.toLowerCase()))
                    valid.set(false);
                else {
                    this.color = color.substring(0, 1).toUpperCase() + color.substring(1);
                    chooseLevel(cli);
                }
            }, () -> cli.setState(this.sourceState));
        }
    }

    private void chooseLevel(Cli cli) {
        Set<Integer> levels = grid.getTopCards().values().stream()
            .flatMap(Collection::stream)
            .filter(Optional::isPresent)
            .map(id -> vm.getDevelopmentCard(id.get()))
            .map(card -> card.isPresent() ? card.get().getLevel() : -1).collect(Collectors.toUnmodifiableSet());

        AtomicBoolean valid = new AtomicBoolean(false);
        while (!valid.get()) {
            valid.set(true);
            cli.promptInt("Card level").ifPresentOrElse(level -> {
                if (!levels.contains(level))
                    valid.set(false);
                else {    
                    this.level = level;
                    chooseSlot(cli);
                }
            }, () -> chooseColor(cli));
        }
    }

    private void chooseSlot(Cli cli) {
        AtomicBoolean valid = new AtomicBoolean(false);
        while (!valid.get()) {
            valid.set(true);
            cli.promptInt("Player board slot index to assign the card to").ifPresentOrElse(slot -> {
                if (slot < 1 || slot > vm.getSlotsCount()) {
                    valid.set(false);
                    return;
                }

                this.slot = slot;

                ReducedDevCard card = vm.getDevelopmentCard(color, level).orElseThrow();

                this.cost = card.getCost().isPresent() ? new HashMap<>(card.getCost().get().getRequirements()) : new HashMap<>();

                if (!this.cost.isEmpty()) {
                    cli.getOut().println("Resources need to be paid.");
                    cli.getOut().println("Please specify how many resources to take from which container.");

                    cli.getOut().println();
                    new ResourceContainers(vm.getLocalPlayerNickname(),
                            vm.getPlayerWarehouseShelves(vm.getLocalPlayerNickname()),
                            vm.getPlayerDepots(vm.getLocalPlayerNickname()),
                            vm.getPlayerStrongbox(vm.getLocalPlayerNickname()).orElse(null))
                            .render(cli);

                    chooseShelves(cli);
                }

                cli.dispatch(new ReqBuyDevCard(this.color, this.level, this.slot, this.shelves));
            }, () -> chooseLevel(cli));
        }
    }

    private void chooseShelves(Cli cli) {
        Set<Integer> allowedShelves = vm.getPlayerShelves(vm.getLocalPlayerNickname()).stream()
                .map(ReducedResourceContainer::getId)
                .collect(Collectors.toUnmodifiableSet());

        cli.promptShelves(this.cost, allowedShelves, false).ifPresentOrElse(shelves -> {
            this.shelves = shelves;
        }, () -> chooseSlot(cli));
    }

    @Override
    public void on(Cli cli, ErrBuyDevCard event) {
        cli.repeatState(event.isStackEmpty() ?
                "Cannot buy development card. Deck is empty." :
                "Cannot place devcard in slot, level mismatch.");
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
        cli.promptPause();
        cli.setState(new TurnAfterActionState());
    }
}
