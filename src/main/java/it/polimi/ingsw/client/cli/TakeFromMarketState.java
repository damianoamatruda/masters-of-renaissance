package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.LeadersHand;
import it.polimi.ingsw.client.cli.components.Market;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.backend.model.leadercards.ZeroLeader;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.vcevents.ReqTakeFromMarket;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.cli.Cli.center;

public class TakeFromMarketState extends CliState {
    private final CliState sourceState;
    private boolean isRow;
    private int index;
    private List<String> resources;
    private Map<String, Integer> replacements;
    private Map<Integer, Map<String, Integer>> shelves;

    public TakeFromMarketState(CliState sourceState) {
        this.sourceState = sourceState;
    }

    @Override
    public void render(Cli cli) {
        cli.getOut().println();
        cli.getOut().println(center("~ Take Market Resources ~"));

        ViewModel vm = cli.getViewModel();

        cli.getOut().println();
        new Market(vm.getMarket().orElseThrow()).render(cli);

        cli.getOut().println();
        cli.showShelves(vm.getLocalPlayerNickname());

        chooseRowCol(cli);
    }

    private void chooseRowCol(Cli cli) {
        AtomicBoolean valid = new AtomicBoolean(false);
        while (!valid.get()) {
            valid.set(true);
            cli.prompt("[row/col]").ifPresentOrElse(input -> {
                if (input.equalsIgnoreCase("row")) {
                    this.isRow = true;
                    chooseIndex(cli);
                } else if (input.equalsIgnoreCase("col")) {
                    this.isRow = false;
                    chooseIndex(cli);
                } else
                    valid.set(false);
            }, () -> cli.setState(this.sourceState));
        }
    }

    private void chooseIndex(Cli cli) {
        AtomicBoolean valid = new AtomicBoolean(false);
        while (!valid.get()) {
            valid.set(true);
            cli.promptInt("Number").ifPresentOrElse(number -> {
                this.index = number - 1;
                if (this.index >= 0
                        && (this.isRow && this.index < cli.getViewModel().getMarket().orElseThrow().getGrid().size()
                        || !this.isRow && cli.getViewModel().getMarket().orElseThrow().getGrid().size() > 0
                        && this.index < cli.getViewModel().getMarket().orElseThrow().getGrid().get(0).size()))
                    chooseReplacements(cli);
                else
                    valid.set(false);
            }, () -> chooseRowCol(cli));
        }
    }

    private void chooseReplacements(Cli cli) {
        ViewModel vm = cli.getViewModel();

        // get a list with the selected resources
        this.resources = new ArrayList<>();
        if (this.isRow)
            this.resources = vm.getMarket().orElseThrow().getGrid().get(this.index);
        else {
            for (List<String> row : vm.getMarket().orElseThrow().getGrid())
                this.resources.add(row.get(this.index));
        }
        this.resources = this.resources.stream()
                .map(n -> vm.getResourceTypes().stream().filter(r -> r.getName().equals(n)).findAny())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(ReducedResourceType::isStorable)
                .map(ReducedResourceType::getName)
                .toList();

        // if there's > 0 replaceable, get the active zeroleaders and prompt for replacements
        int blanksCount = (int) this.resources.stream().filter(r -> r.equals(vm.getMarket().orElseThrow().getReplaceableResType())).count();

        // remove the replaceable this.resources from the selected ones
        this.resources = this.resources.stream().filter(r -> !r.equals(vm.getMarket().orElseThrow().getReplaceableResType())).toList();

        this.replacements = new HashMap<>();

        // for (String res : chosenResources)
        //     this.replacements.compute(res, (k, v) -> v == null ? 1 : v++);

        if (blanksCount > 0) {
            List<ReducedLeaderCard> zeroLeaders = vm.getPlayerLeaderCards(vm.getLocalPlayerNickname()).stream()
                    .filter(c -> c.isActive() &&
                            c.getLeaderType().equals(ZeroLeader.class.getSimpleName())).toList();

            // TODO: Refactor logic of this
            if (zeroLeaders.size() > 0) {
                new LeadersHand(zeroLeaders).render(cli);
                cli.getOut().println("These are the active leaders you can use to replace blank resources.");

                AtomicBoolean valid = new AtomicBoolean(false);
                while (!valid.get()) {
                    valid.set(true);
                    cli.prompt("Replace resources? [y/n]").ifPresentOrElse(input -> {
                        valid.set(input.equalsIgnoreCase("y") || input.equalsIgnoreCase("n"));
                        if (input.equalsIgnoreCase("y")) {
                            cli.promptResources(
                                    zeroLeaders.stream().map(ReducedLeaderCard::getResourceType).collect(Collectors.toUnmodifiableSet()), blanksCount
                            ).ifPresentOrElse(replacements -> {
                                this.replacements = replacements;
                            }, () -> chooseIndex(cli)); // TODO: Refactor logic of this
                        }
                    }, () -> chooseIndex(cli));
                }
            }
        }

        chooseShelves(cli);
    }

    private void chooseShelves(Cli cli) {
        ViewModel vm = cli.getViewModel();

        Map<String, Integer> totalRes = new HashMap<>(this.replacements);
        this.resources.forEach(r -> totalRes.compute(r, (res, c) -> c == null ? 1 : c + 1));

        Set<Integer> allowedShelves = vm.getPlayerShelves(vm.getLocalPlayerNickname()).stream()
                .map(ReducedResourceContainer::getId)
                .collect(Collectors.toUnmodifiableSet());

        cli.getOut().println();
        cli.promptShelves(totalRes, allowedShelves, true).ifPresentOrElse(shelves -> {
            this.shelves = shelves;
            cli.dispatch(new ReqTakeFromMarket(this.isRow, this.index, this.replacements, this.shelves));
        }, () -> chooseIndex(cli)); // TODO: Check this
    }

    // ErrObjectNotOwned handled in CliState
    // ErrNoSuchEntity handled in CliState
    // ErrResourceReplacement handled in CliState
    // ErrReplacedTransRecipe handled in CliState
    // ErrResourceTransfer handled in CliState

    @Override
    public void on(Cli cli, UpdateAction event) {
        cli.promptPause();
        cli.setState(new TurnAfterActionState());
    }
}
