package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.*;
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

public class BuyDevelopmentCardController extends CliController {
    private final CliController sourceController;
    private final AtomicBoolean isExitingState;
    private ReducedDevCardGrid grid;
    private String color;
    private int level;
    private Map<String, Integer> cost;
    private Map<Integer, Map<String, Integer>> shelves;
    private int slot;

    public BuyDevelopmentCardController(CliController sourceController) {
        this.sourceController = sourceController;
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

        new ResourceContainers(
                vm.getLocalPlayer().orElseThrow(),
                vm.getLocalPlayer().map(vm::getPlayerWarehouseShelves).orElseThrow(),
                vm.getLocalPlayer().map(vm::getPlayerDepots).orElseThrow(),
                vm.getLocalPlayer().flatMap(vm::getPlayerStrongbox).orElse(null))
                .render();

        new DevSlots(vm.getPlayerDevelopmentSlots(vm.getLocalPlayer().orElseThrow())).render();

        List<ReducedLeaderCard> discountLeaders = vm.getPlayerLeaderCards(vm.getLocalPlayer().orElseThrow()).stream()
                .filter(ReducedLeaderCard::isActive)
                .filter(c -> c.getLeaderType() == LeaderType.DISCOUNT).toList();
        new LeadersHand(discountLeaders).render();

        chooseColor();
    }

    private void chooseColor() {
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
                    chooseLevel();
                }
            }, () -> {
                isExitingState.set(true);
                cli.setController(this.sourceController, false);
            });
        }
    }

    private void chooseLevel() {
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
                    chooseSlot();
                }
            }, this::chooseColor);
        }
    }

    private void chooseSlot() {
        AtomicBoolean valid = new AtomicBoolean(false);
        while (!valid.get()) {
            valid.set(true);
            cli.promptInt("Slot").ifPresentOrElse(slot -> {
                isExitingState.set(false);
                if (slot < 1 || slot > vm.getDevSlotsCount()) {
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

                    chooseShelves();
                }

                if (!isExitingState.get())
                    cli.getUi().dispatch(new ReqBuyDevCard(this.color, this.level, this.slot - 1, this.shelves));
            }, this::chooseLevel);
        }
    }

    private void chooseShelves() {
        Set<Integer> allowedShelves = vm.getLocalPlayer().map(vm::getPlayerShelves).orElseThrow().stream()
                .map(ReducedResourceContainer::getId)
                .collect(Collectors.toSet());

        vm.getLocalPlayer().flatMap(vm::getPlayerStrongbox).ifPresent(s -> {
            cli.getOut().println(center(String.format("%s's strongbox:%n", vm.getLocalPlayer().orElse(""))));
            allowedShelves.add(s.getId());
        });

        vm.getLocalPlayer().flatMap(vm::getPlayerStrongbox).map(s -> new Box(new ResourceContainer(s))).ifPresent(StringComponent::render);
        cli.getOut().println();

        cli.promptShelves(this.cost, allowedShelves, false).ifPresentOrElse(shelves -> {
            isExitingState.set(false);
            this.shelves = shelves;
        }, this::chooseSlot);
    }

    @Override
    public void on(ErrBuyDevCard event) {
        if (event.isStackEmpty())
            cli.reloadController(center(String.format(
                    "You cannot buy the development card with color %s and level %d: deck is empty.", color, level)));
        else {
            int slotLevel = vm.getLocalPlayer().map(vm::getPlayerDevelopmentSlots).orElseThrow().get(slot).map(ReducedDevCard::getLevel).orElse(0);
            int maxLevel = Math.max(slotLevel, level);

            String insMsg = String.format(" is insufficient (it has to be %d)", maxLevel + 1);
            String errMsg = String.format("Cannot place the development card into slot %d: card level %d%s, slot level %d%s.",
                    slot,
                    level, slotLevel >= level ? insMsg : "",
                    slotLevel, slotLevel < level ? insMsg : "");

            cli.reloadController(center(errMsg));
        }
    }

    @Override
    public void on(UpdateAction event) {
        cli.setController(new TurnAfterActionController(), true);
    }
}
