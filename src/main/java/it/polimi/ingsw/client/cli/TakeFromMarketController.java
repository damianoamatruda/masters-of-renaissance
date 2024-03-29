package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.LeadersHand;
import it.polimi.ingsw.client.cli.components.Market;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.vcevents.ReqTakeFromMarket;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard.LeaderType;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.cli.Cli.center;

public class TakeFromMarketController extends CliController {
    private final CliController sourceController;
    private boolean isRow;
    private int index;
    private List<String> resources;
    private Map<String, Integer> replacements;
    private Map<Integer, Map<String, Integer>> shelves;

    public TakeFromMarketController(CliController sourceController) {
        this.sourceController = sourceController;
    }

    @Override
    public void render() {
        cli.getOut().println();
        cli.getOut().println(center("~ Take Market Resources ~"));

        cli.getOut().println();
        new Market(vm.getMarket().orElseThrow()).render();

        cli.getOut().println();
        chooseRowCol();
    }

    private void chooseRowCol() {
        AtomicBoolean valid = new AtomicBoolean(false);
        while (!valid.get()) {
            valid.set(true);
            cli.prompt("[row/col]").ifPresentOrElse(input -> {
                if (input.equalsIgnoreCase("row")) {
                    this.isRow = true;
                    chooseIndex();
                } else if (input.equalsIgnoreCase("col")) {
                    this.isRow = false;
                    chooseIndex();
                } else
                    valid.set(false);
            }, () -> cli.setController(this.sourceController));
        }
    }

    private void chooseIndex() {
        AtomicBoolean valid = new AtomicBoolean(false);
        while (!valid.get()) {
            valid.set(true);
            cli.promptInt("Number").ifPresentOrElse(number -> {
                this.index = number - 1;
                if (this.index >= 0
                        && (this.isRow && this.index < vm.getMarket().orElseThrow().getGrid().size()
                        || !this.isRow && vm.getMarket().orElseThrow().getGrid().size() > 0
                        && this.index < vm.getMarket().orElseThrow().getGrid().get(0).size()))
                    chooseReplacements();
                else
                    valid.set(false);
            }, this::chooseRowCol);
        }
    }

    private void chooseReplacements() {
        /* Get a list with the selected resources */
        this.resources = new ArrayList<>();
        if (this.isRow)
            this.resources = vm.getMarket().orElseThrow().getGrid().get(this.index);
        else {
            for (List<String> row : vm.getMarket().orElseThrow().getGrid())
                this.resources.add(row.get(this.index));
        }
        this.resources = this.resources.stream()
                .map(n -> vm.getResourceTypes().stream().filter(r -> r.getName().equals(n)).findAny())
                .flatMap(Optional::stream)
                .filter(r -> r.isStorable() || vm.getMarket().get().getReplaceableResType().equals(r.getName()))
                .map(ReducedResourceType::getName)
                .toList();

        /* If there is > 0 replaceable, get the active zeroleaders and prompt for replacements */
        int blanksCount = (int) this.resources.stream().filter(r -> r.equals(vm.getMarket().orElseThrow().getReplaceableResType())).count();

        /* Remove the replaceable this.resources from the selected ones */
        this.resources = this.resources.stream().filter(r -> !r.equals(vm.getMarket().orElseThrow().getReplaceableResType())).toList();

        this.replacements = new HashMap<>();

        if (blanksCount > 0) {
            List<ReducedLeaderCard> zeroLeaders = vm.getLocalPlayer().map(vm::getPlayerLeaderCards).orElseThrow().stream()
                    .filter(ReducedLeaderCard::isActive)
                    .filter(c -> c.getLeaderType() == LeaderType.ZERO)
                    .toList();

            if (zeroLeaders.size() > 0) {
                cli.getOut().println();
                cli.getOut().println(center("These are the active leaders you can use to replace blank resources:"));
                cli.getOut().println();
                new LeadersHand(zeroLeaders).render();

                cli.getOut().println();

                AtomicBoolean valid = new AtomicBoolean(false);
                while (!valid.get()) {
                    valid.set(true);
                    cli.promptResources(
                            zeroLeaders.stream().map(ReducedLeaderCard::getResourceType).collect(Collectors.toUnmodifiableSet()), blanksCount
                    ).ifPresentOrElse(replacements -> this.replacements = replacements, this::chooseIndex);
                }
            }
        }

        chooseShelves();
    }

    private void chooseShelves() {
        Map<String, Integer> totalRes = new HashMap<>(this.replacements);
        this.resources.forEach(r -> totalRes.compute(r, (res, c) -> c == null ? 1 : c + 1));

        Set<Integer> allowedShelves = vm.getLocalPlayer().map(vm::getPlayerShelves).orElseThrow().stream()
                .map(ReducedResourceContainer::getId)
                .collect(Collectors.toUnmodifiableSet());

        cli.getOut().println();
        cli.promptShelves(totalRes, allowedShelves, false, true).ifPresentOrElse(shelves -> {
            this.shelves = shelves;
            cli.getUi().dispatch(new ReqTakeFromMarket(this.isRow, this.index, this.replacements, this.shelves));
        }, this::chooseIndex);
    }

    @Override
    public void on(UpdateAction event) {
        cli.promptPause();
        cli.setController(new TurnAfterActionController());
    }
}
