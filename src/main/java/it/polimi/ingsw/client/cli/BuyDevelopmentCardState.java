package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.DevCardGrid;
import it.polimi.ingsw.client.cli.components.DevSlots;
import it.polimi.ingsw.client.cli.components.LeadersHand;
import it.polimi.ingsw.client.cli.components.ResourceContainers;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrBuyDevCard;
import it.polimi.ingsw.common.events.vcevents.ReqBuyDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardGrid;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard.LeaderType;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.cli.Cli.center;

public class BuyDevelopmentCardState extends CliController {
    private final CliController sourceState;
    private final AtomicBoolean isExitingState;
    private ReducedDevCardGrid grid;
    private String color;
    private int level;
    private Map<String, Integer> cost;
    private Map<Integer, Map<String, Integer>> shelves;
    private int slot;

    public BuyDevelopmentCardState(CliController sourceState) {
        this.sourceState = sourceState;
        isExitingState = new AtomicBoolean(false);
        this.shelves = new HashMap<>();
    }

    @Override
    public void render() {
        cli.getOut().println();
        cli.getOut().println(center("~ Buy a Development Card ~"));

        grid = vm.getDevCardGrid().orElseThrow();

        cli.getOut().println();
        new DevCardGrid(grid).render();
        cli.getOut().println();
        new ResourceContainers(vm.getLocalPlayerNickname(),
                vm.getPlayerWarehouseShelves(vm.getLocalPlayerNickname()),
                vm.getPlayerDepots(vm.getLocalPlayerNickname()),
                vm.getPlayerStrongbox(vm.getLocalPlayerNickname()).orElse(null))
                .render();

        new DevSlots(vm.getPlayerDevelopmentSlots(vm.getLocalPlayerNickname())).render();

        List<ReducedLeaderCard> discountLeaders = vm.getPlayerLeaderCards(vm.getLocalPlayerNickname()).stream()
                .filter(ReducedLeaderCard::isActive)
                .filter(c -> c.getLeaderType() == LeaderType.DISCOUNT).toList();
        new LeadersHand(discountLeaders).render();

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
                isExitingState.set(false);
                if (!allowedColors.contains(color.toLowerCase()))
                    valid.set(false);
                else {
                    this.color = color.substring(0, 1).toUpperCase() + color.substring(1);
                    chooseLevel(cli);
                }
            }, () -> {
                isExitingState.set(true);
                cli.setController(this.sourceState, false);
            });
        }
    }

    private void chooseLevel(Cli cli) {
        Set<Integer> levels = grid.getTopCards().values().stream()
                .flatMap(Collection::stream)
                .flatMap(Optional::stream)
                .map(vm::getDevelopmentCard)
                .map(card -> card.map(ReducedDevCard::getLevel).orElse(-1))
            .filter(level -> level >= 0).collect(Collectors.toUnmodifiableSet());

        AtomicBoolean valid = new AtomicBoolean(false);
        while (!valid.get()) {
            valid.set(true);
            cli.promptInt("Card level").ifPresentOrElse(level -> {
                isExitingState.set(false);
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
            cli.promptInt("Slot (leftmost = 0)").ifPresentOrElse(slot -> {
                isExitingState.set(false);
                if (slot < 1 || slot > vm.getSlotsCount()) {
                    valid.set(false);
                    return;
                }

                this.slot = slot;

                ReducedDevCard card = vm.getDevCardFromGrid(color, level).orElseThrow();

                this.cost = vm.getDevCardDiscountedCost(card.getId());

                if (!this.cost.isEmpty()) {
                    cli.getOut().println();
                    cli.getOut().println(center("Resources need to be paid."));
                    cli.getOut().println(center("Please specify how many resources to take from which container."));
                    cli.getOut().println();

                    chooseShelves(cli);
                }

                if (!isExitingState.get())
                    cli.getUi().dispatch(new ReqBuyDevCard(this.color, this.level, this.slot, this.shelves));
            }, () -> chooseLevel(cli));
        }
    }

    private void chooseShelves(Cli cli) {
        Set<Integer> allowedShelves = vm.getPlayerShelves(vm.getLocalPlayerNickname()).stream()
                .map(ReducedResourceContainer::getId)
                .collect(Collectors.toUnmodifiableSet());

        cli.promptShelves(this.cost, allowedShelves, false).ifPresentOrElse(shelves -> {
            isExitingState.set(false);
            this.shelves = shelves;
        }, () -> chooseSlot(cli));
    }

    @Override
    public void on(ErrBuyDevCard event) {
        if (event.isStackEmpty())
            cli.reloadController(center(String.format(
                    "Cannot buy development card with color %s and level %d: deck is empty.", color, level)));
        else {
            int slotLevel = vm.getPlayerDevelopmentSlots(vm.getLocalPlayerNickname()).get(slot).map(ReducedDevCard::getLevel).orElse(0);
            int maxLevel = slotLevel > level ? slotLevel : level;

            String insMsg = String.format("is insufficient (has to be %d).", maxLevel + 1);
            String errMsg = String.format("Cannot place development card in slot %d: card level %d%s, slot level %d%s.",
                    slot,
                    level, slotLevel >= level ? insMsg : "",
                    slotLevel, slotLevel < level ? insMsg : "");
            
            cli.reloadController(center(errMsg));
        }
    }

    @Override
    public void on(UpdateAction event) {
        cli.setController(new TurnAfterActionState(), true);
    }
}
