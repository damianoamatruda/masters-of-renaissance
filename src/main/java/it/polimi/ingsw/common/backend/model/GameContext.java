package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.EventDispatcher;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.model.cardrequirements.CardRequirementsNotMetException;
import it.polimi.ingsw.common.backend.model.leadercards.LeaderCard;
import it.polimi.ingsw.common.backend.model.resourcecontainers.IllegalResourceTransferException;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Shelf;
import it.polimi.ingsw.common.backend.model.resourcetransactions.IllegalResourceTransactionContainersException;
import it.polimi.ingsw.common.backend.model.resourcetransactions.IllegalResourceTransactionReplacementsException;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ProductionRequest;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransaction;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateAction.ActionType;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction.ErrActionReason;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNoSuchEntity.IDType;
import it.polimi.ingsw.common.reducedmodel.ReducedProductionRequest;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class manages the states and actions of a game.
 */
public class GameContext extends EventDispatcher {
    private final Object lock;
    /** The game. */
    private final Game game;

    /** The game factory. */
    private final GameFactory gameFactory;

    /** This represents the state of a game during the turn of a player that has not made the mandatory move yet. */
    private boolean mandatoryActionDone;

    /**
     * Initializes a context for the given game.
     *
     * @param game the game
     */
    public GameContext(Game game, GameFactory gameFactory) {
        this.lock = new Object();
        this.game = game;
        this.gameFactory = gameFactory;
        this.mandatoryActionDone = false;
    }

    public void registerViewToModel(View view, String nickname) {
        synchronized(lock) {
            view.registerOnModelGame(game);
            view.registerOnModelPlayer(getPlayerByNickname(nickname));
        }
    }

    public void unregisterViewToModel(View view, String nickname) {
        synchronized(lock) {
            view.unregisterOnModelGame(game);
            view.unregisterOnModelPlayer(getPlayerByNickname(nickname));
        }
    }

    public void start() {
        synchronized(lock) {
            // TODO: Add logger
            // System.out.println("context.start, players:");
            // for (String n : game.getPlayers().stream().map(Player::getNickname).toList())
            //     System.out.println(n);
            // System.out.println("context.start, players: end of list");
    
            game.dispatchState();
            game.getMarket().dispatchInitialState();
            game.getDevCardGrid().dispatchInitialState();
            game.getPlayers().forEach(Player::dispatchState);
            game.getPlayers().forEach(player -> player.getSetup().giveInitialFaithPoints(game, player));
        }
    }

    public void resume(View view) {
        synchronized(lock) {
            game.dispatchState(view);
            game.getPlayers().forEach(p -> p.dispatchState(view));
        }
    }

    /**
     * Choose leaders from the hand of a player.
     *
     * @param view      the view the action originates from. Used to on back errors
     * @param nickname  the player
     * @param leaderIds the leader cards to choose
     */
    public void chooseLeaders(View view, String nickname, List<Integer> leaderIds) {
        synchronized(lock) {
            Player player = getPlayerByNickname(nickname);

            if (player.getSetup().isDone()) {
                dispatch(new ErrAction(view, ErrActionReason.LATE_SETUP_ACTION));
                return;
            }

            // TODO: Refactor this
            Optional<Integer> missing = leaderIds.stream().filter(l -> player.getLeaderById(l).isEmpty()).findAny();
            if (missing.isPresent()) {
                dispatch(new ErrObjectNotOwned(view, missing.get(), "LeaderCard"));
                return;
            }

            int distinctSize = leaderIds.stream().distinct().toList().size();
            if (distinctSize < leaderIds.size()) {
                dispatch(new ErrInitialChoice(view, true, leaderIds.size() - distinctSize));
                return;
            }


            List<LeaderCard> leaders = leaderIds.stream().map(player::getLeaderById).filter(Optional::isPresent).map(Optional::get).toList();

            try {
                player.getSetup().chooseLeaders(game, player, leaders);
                dispatch(new UpdateAction(nickname, ActionType.CHOOSE_LEADERS));
            } catch (CannotChooseException e) {
                dispatch(new ErrInitialChoice(view, true, e.getMissingLeadersCount()));
            }
        }
    }

    /**
     * Chooses the initial resources to take as a player.
     *
     * @param view              the view the action originates from. Used to on back errors
     * @param nickname       the player
     * @param reducedShelves the destination shelves
     */
    public void chooseResources(View view, String nickname, Map<Integer, Map<String, Integer>> reducedShelves) {
        synchronized(lock) {
            Player player = getPlayerByNickname(nickname);
    
            if (player.getSetup().isDone()) {
                dispatch(new ErrAction(view, ErrActionReason.LATE_SETUP_ACTION));
                return;
            }
    
            Map<Shelf, Map<ResourceType, Integer>> shelves = new HashMap<>();
            for (Map.Entry<Integer, Map<String, Integer>> shelf : reducedShelves.entrySet()) {
                Optional<Shelf> s = player.getShelfById(shelf.getKey());
    
                if (s.isEmpty()) {
                    dispatch(new ErrObjectNotOwned(view, shelf.getKey(), "Shelf"));
                    return;
                }
    
                try {
                    shelves.put(s.get(), translateResMap(shelf.getValue()));
                } catch (NoSuchElementException e) {
                    dispatch(new ErrNoSuchEntity(view, IDType.RESOURCE, -1, e.getMessage()));
                }
            }
    
            try {
                player.getSetup().chooseResources(game, player, shelves);
                dispatch(new UpdateAction(nickname, ActionType.CHOOSE_RESOURCES));
            } catch (IllegalResourceTransactionReplacementsException e) {
                // illegal replaced resources
                dispatch(new ErrResourceReplacement(view, e.isInput(), e.isNonStorable(), e.isExcluded(), e.getReplacedCount(), e.getBlanks()));
            } catch (IllegalResourceTransactionContainersException e) {
                // amount of resources in replaced map is different from shelves mapping
                dispatch(new ErrReplacedTransRecipe(view, e.getResType(), e.getReplacedCount(), e.getShelvesChoiceResCount(), e.isIllegalDiscardedOut()));
            } catch (CannotChooseException e1) {
                // resources already chosen
                dispatch(new ErrInitialChoice(view, false, 0));
            } catch (IllegalResourceTransferException e) {
                dispatch(new ErrResourceTransfer(view, e.getResource().getName(), e.isAdded(), e.getKind().name()));
            }
        }
    }

    /**
     * Swaps the content of two shelves of a player.
     *
     * @param view        the view the action originates from. Used to on back errors
     * @param nickname the player
     * @param shelfId1 the first shelf
     * @param shelfId2 the second shelf
     */
    public void swapShelves(View view, String nickname, Integer shelfId1, Integer shelfId2) {
        synchronized(lock) {
            Player player = getPlayerByNickname(nickname);
    
            if (!preliminaryChecks(view, player))
                return;
    
            if (!checkCurrentPlayer(view, player))
                return;
    
            Shelf shelf1, shelf2;
    
            try {
                shelf1 = player.getShelfById(shelfId1).get();
            } catch (NoSuchElementException e) {
                dispatch(new ErrObjectNotOwned(view, shelfId1, "Shelf"));
                return;
            }
            try {
                shelf2 = player.getShelfById(shelfId2).get();
            } catch (NoSuchElementException e) {
                dispatch(new ErrObjectNotOwned(view, shelfId2, "Shelf"));
                return;
            }
    
            try {
                ResourceContainer.swap(shelf1, shelf2);
                dispatch(new UpdateAction(nickname, ActionType.SWAP_SHELVES));
            } catch (IllegalResourceTransferException e) {
                dispatch(new ErrResourceTransfer(view, e.getResource().getName(), e.isAdded(), e.getKind().name()));
            }
        }
    }

    /**
     * Makes a player activate a leader card.
     *
     * @param view        the view the action originates from. Used to on back errors
     * @param nickname the player
     * @param leaderId the leader card to activate
     */
    public void activateLeader(View view, String nickname, Integer leaderId) {
        synchronized(lock) {
            Player player = getPlayerByNickname(nickname);
    
            if (!preliminaryChecks(view, player))
                return;
    
            if (!checkCurrentPlayer(view, player))
                return;
    
            LeaderCard leader;
    
            try {
                leader = player.getLeaderById(leaderId).orElseThrow(() -> new Exception());
            } catch (Exception e) {
                dispatch(new ErrObjectNotOwned(view, leaderId, "LeaderCard"));
                return;
            }
    
            try {
                leader.activate(player);
                dispatch(new UpdateAction(nickname, ActionType.ACTIVATE_LEADER));
            } catch (IllegalArgumentException e) {
                dispatch(new ErrObjectNotOwned(view, leaderId, "LeaderCard"));
            } catch (CardRequirementsNotMetException e) {
                dispatch(new ErrCardRequirements(view, e.getMessage()));
            }
        }
    }

    /**
     * Makes a player discard a leader card.
     *
     * @param view        the view the action originates from. Used to on back errors
     * @param nickname the player
     * @param leaderId the leader card to discard
     */
    public void discardLeader(View view, String nickname, Integer leaderId) {
        synchronized(lock) {
            Player player = getPlayerByNickname(nickname);
    
            if (!preliminaryChecks(view, player))
                return;
    
            if (!checkCurrentPlayer(view, player))
                return;
    
            LeaderCard leader;
            
            try {
                leader = player.getLeaderById(leaderId).orElseThrow(() -> new Exception());
            } catch (Exception e) {
                dispatch(new ErrObjectNotOwned(view, leaderId, "LeaderCard"));
                return;
            }
    
            try {
                player.discardLeader(game, leader);
                dispatch(new UpdateAction(nickname, ActionType.DISCARD_LEADER));
            } catch (IllegalArgumentException e) {
                dispatch(new ErrObjectNotOwned(view, leaderId, "LeaderCard"));
            } catch (ActiveLeaderDiscardException e) {
                dispatch(new ErrActiveLeaderDiscarded(view));
            }
        }
    }

    /**
     * Takes resources from the market as a player.
     *
     * @param view              the view the action originates from. Used to on back errors
     * @param nickname       the player
     * @param isRow          <code>true</code> if a row is selected; <code>false</code> if a column is selected.
     * @param index          index of the selected row or column
     * @param replacements   a map of the chosen resources to take, if choices are applicable
     * @param reducedShelves a map of the shelves where to add the taken resources, if possible
     */
    public void takeMarketResources(View view, String nickname, boolean isRow, int index,
                                    Map<String, Integer> replacements,
                                    Map<Integer, Map<String, Integer>> reducedShelves) {
        
        synchronized(lock) {
            Player player = getPlayerByNickname(nickname);

            if (!preliminaryChecks(view, player))
                return;

            if (!checkCurrentPlayer(view, player))
                return;

            if (!checkMandatoryActionNotDone(view))
                return;

            Map<Shelf, Map<ResourceType, Integer>> shelves = new HashMap<>();
            for (Map.Entry<Integer, Map<String, Integer>> shelf : reducedShelves.entrySet()) {
                Optional<Shelf> s = player.getShelfById(shelf.getKey());

                if (s.isEmpty()) {
                    dispatch(new ErrObjectNotOwned(view, shelf.getKey(), "Shelf"));
                    return;
                }

                try {
                    shelves.put(s.get(), translateResMap(shelf.getValue()));
                } catch (NoSuchElementException e) {
                    dispatch(new ErrNoSuchEntity(view, IDType.RESOURCE, -1, e.getMessage()));
                }
            }

            try {
                game.getMarket().takeResources(game, player, isRow, index, translateResMap(replacements), shelves);
            } catch (IllegalResourceTransactionReplacementsException e) {
                // illegal replaced resources
                dispatch(new ErrResourceReplacement(view, e.isInput(), e.isNonStorable(), e.isExcluded(), e.getReplacedCount(), e.getBlanks()));
                return;
            } catch (IllegalResourceTransactionContainersException e) {
                // amount of resources in replaced map is different from shelves mapping
                dispatch(new ErrReplacedTransRecipe(view, e.getResType(), e.getReplacedCount(), e.getShelvesChoiceResCount(), e.isIllegalDiscardedOut()));
                return;
            } catch (IllegalResourceTransferException e) {
                dispatch(new ErrResourceTransfer(view, e.getResource().getName(), e.isAdded(), e.getKind().name()));
                return;
            } catch (IllegalArgumentException e) {
                dispatch(new ErrNoSuchEntity(view, IDType.MARKET_INDEX, index, null));
                return;
            } catch (NoSuchElementException e) {
                dispatch(new ErrNoSuchEntity(view, IDType.RESOURCE, -1, e.getMessage()));
                return;
            }

            mandatoryActionDone = true;
            
            dispatch(new UpdateAction(nickname, ActionType.TAKE_MARKET_RESOURCES));
        }
    }

    /**
     * Makes a player buy a development card from the development card grid.
     *
     * @param view                    the view the action originates from. Used to on back errors
     * @param nickname             the player
     * @param color                the color of the card to be bought
     * @param level                the level of the card to be bought
     * @param slotIndex            the index of the deview slot where to put the development card
     * @param reducedResContainers a map of the resource containers where to take the storable resources
     */
    public void buyDevCard(View view, String nickname, String color, int level, int slotIndex,
                           Map<Integer, Map<String, Integer>> reducedResContainers) {
        synchronized(lock) {
            Player player = getPlayerByNickname(nickname);
            if (!preliminaryChecks(view, player))
                return;

            if (!checkCurrentPlayer(view, player))
                return;

            if (!checkMandatoryActionNotDone(view))
                return;

            Map<ResourceContainer, Map<ResourceType, Integer>> resContainers = new HashMap<>();
            for (Map.Entry<Integer, Map<String, Integer>> container : reducedResContainers.entrySet()) {
                Optional<ResourceContainer> c = player.getResourceContainerById(container.getKey());

                if (c.isEmpty()) {
                    dispatch(new ErrObjectNotOwned(view, container.getKey(), "ResourceContainer"));
                    return;
                }

                try {
                    resContainers.put(c.get(), translateResMap(container.getValue()));
                } catch (NoSuchElementException e) {
                    dispatch(new ErrNoSuchEntity(view, IDType.RESOURCE, -1, e.getMessage()));
                }
            }

            try {
                game.getDevCardGrid().buyDevCard(game, player, gameFactory.getDevCardColor(color).orElseThrow(), level, slotIndex, resContainers);
            } catch (EmptyStackException e) {
                dispatch(new ErrBuyDevCard(view, true));
                return;
            } catch (CardRequirementsNotMetException e) {
                dispatch(new ErrCardRequirements(view, e.getMessage()));
                return;
            } catch (IllegalCardDepositException e) {
                dispatch(new ErrBuyDevCard(view, false));
                return;
            } catch (IllegalResourceTransactionReplacementsException e) {
                // illegal replaced resources
                dispatch(new ErrResourceReplacement(view, e.isInput(), e.isNonStorable(), e.isExcluded(), e.getReplacedCount(), e.getBlanks()));
                return;
            } catch (IllegalResourceTransactionContainersException e) {
                // amount of resources in replaced map is different from shelves mapping
                dispatch(new ErrReplacedTransRecipe(view, e.getResType(), e.getReplacedCount(), e.getShelvesChoiceResCount(), e.isIllegalDiscardedOut()));
                return;
            } catch (IllegalResourceTransferException e) {
                dispatch(new ErrResourceTransfer(view, e.getResource().getName(), e.isAdded(), e.getKind().name()));
                return;
            }

            mandatoryActionDone = true;
     
            dispatch(new UpdateAction(nickname, ActionType.BUY_DEVELOPMENT_CARD));
        }
    }

    /**
     * Makes a player activate a group of productions.
     *
     * @param view                   the view the action originates from. Used to on back errors
     * @param nickname            the player
     * @param reducedProdRequests the group of requested contemporary productions
     */
    public void activateProductionRequests(View view, String nickname, List<ReducedProductionRequest> reducedProdRequests) {
        synchronized(lock) {
            Player player = getPlayerByNickname(nickname);

            if (!preliminaryChecks(view, player))
                return;

            if (!checkCurrentPlayer(view, player))
                return;

            if (!checkMandatoryActionNotDone(view))
                return;

            Optional<Integer> missing = reducedProdRequests.stream().map(ReducedProductionRequest::getProduction).filter(p -> player.getProductionById(p).isEmpty()).findAny();
            if (missing.isPresent()) {
                dispatch(new ErrObjectNotOwned(view, missing.get(), "ReducedProductionRequest"));
                return;
            }

            List<ProductionRequest> prodRequests = new ArrayList<>();

            for (ReducedProductionRequest r : reducedProdRequests) {
                Map<ResourceContainer, Map<ResourceType, Integer>> inputContainers = new HashMap<>();
                for (Map.Entry<Integer, Map<String, Integer>> container : r.getInputContainers().entrySet()) {
                    Optional<ResourceContainer> c = player.getResourceContainerById(container.getKey());
        
                    if (c.isEmpty()) {
                        dispatch(new ErrObjectNotOwned(view, container.getKey(), "ResourceContainer"));
                        return;
                    }
        
                    try {
                        inputContainers.put(c.get(), translateResMap(container.getValue()));
                    } catch (NoSuchElementException e) {
                        dispatch(new ErrNoSuchEntity(view, IDType.RESOURCE, -1, e.getMessage()));
                        return;
                    }
                }

                try {
                    prodRequests.add(
                            new ProductionRequest(
                                    player.getProductionById(r.getProduction()).orElseThrow(),
                                    translateResMap(r.getInputBlanksRep()),
                                    translateResMap(r.getOutputBlanksRep()),
                                    inputContainers, player.getStrongbox()));
                } catch (IllegalResourceTransactionReplacementsException e) {
                    // illegal replaced resources
                    dispatch(new ErrResourceReplacement(view, e.isInput(), e.isNonStorable(), e.isExcluded(), e.getReplacedCount(), e.getBlanks()));
                    return;
                } catch (IllegalResourceTransactionContainersException e) {
                    // amount of resources in replaced map is different from shelves mapping
                    dispatch(new ErrReplacedTransRecipe(view, e.getResType(), e.getReplacedCount(), e.getShelvesChoiceResCount(), e.isIllegalDiscardedOut()));
                    return;
                } catch (NoSuchElementException e) {
                    dispatch(new ErrNoSuchEntity(view, IDType.RESOURCE, -1, e.getMessage()));
                    return;
                }
            }

            ResourceTransaction transaction = new ResourceTransaction(List.copyOf(prodRequests));
            try {
                transaction.activate(game, player);
            } catch (IllegalResourceTransferException e) {
                dispatch(new ErrResourceTransfer(view, e.getResource().getName(), e.isAdded(), e.getKind().name()));
                return;
            }

            mandatoryActionDone = true;
         
            dispatch(new UpdateAction(nickname, ActionType.ACTIVATE_PRODUCTION));
        }
    }

    /**
     * Makes a player end his turn.
     *
     * @param view        the view the action originates from. Used to on back errors
     * @param nickname the player
     */
    public void endTurn(View view, String nickname) {
        synchronized(lock) {
            Player player = getPlayerByNickname(nickname);
    
            if (!preliminaryChecks(view, player))
                return;
    
            if (!checkCurrentPlayer(view, player))
                return;
    
            if (!mandatoryActionDone) {
                dispatch(new ErrAction(view, ErrActionReason.EARLY_TURN_END));
                return;
            }
    
            try {
                game.onTurnEnd();
            } catch (NoActivePlayersException e) {
                throw new RuntimeException("No active players"); // TODO: Handle this
            }
    
            mandatoryActionDone = false;
        
            dispatch(new UpdateAction(nickname, ActionType.END_TURN));
        }
    }

    public void setActive(String nickname, boolean active) throws NoActivePlayersException {
        synchronized(lock) {       
            Player player = getPlayerByNickname(nickname);
            player.setActive(active);
            if (!active && player.equals(game.getCurrentPlayer()))
            game.onTurnEnd();
        }
    }

    /**
     * Ensures that the setup still isn't finished and the game has not ended yet. Action methods can use these checks
     * to confirm their legitimacy of execution, before starting.
     *
     * @param view   the view to use to on back the error, if the checks don't on
     * @return whether the checks passed
     */
    private boolean preliminaryChecks(View view, Player player) {
        if (!player.getSetup().isDone() || game.hasEnded()) {
            dispatch(new ErrAction(view, !player.getSetup().isDone() ? ErrActionReason.EARLY_MANDATORY_ACTION : ErrActionReason.GAME_ENDED));
            return false;
        }
        return true;
    }

    /**
     * Ensures that the given player is the current player in the turn.
     *
     * @param view      the view to on the error massage back with
     * @param player the player to check
     * @return whether the check passed
     */
    private boolean checkCurrentPlayer(View view, Player player) {
        if (!player.equals(game.getCurrentPlayer())) {
            dispatch(new ErrAction(view, ErrActionReason.NOT_CURRENT_PLAYER));
            return false;
        }
        return true;
    }

    /**
     * Ensures that the current player has not done a mandatory action yet.
     *
     * @param view      the view to on the error massage back with
     * @return whether the check passed
     */
    private boolean checkMandatoryActionNotDone(View view) {
        if (mandatoryActionDone) {
            dispatch(new ErrAction(view, ErrActionReason.LATE_MANDATORY_ACTION));
            return false;
        }
        return true;
    }

    private Player getPlayerByNickname(String nickname) throws NoSuchElementException {
        return game.getPlayers().stream().filter(p -> p.getNickname().equals(nickname)).findAny().orElseThrow();
    }

    private Map<ResourceType, Integer> translateResMap(Map<String, Integer> r) {
        return r.entrySet().stream().collect(Collectors.toMap(e -> {
            String name = e.getKey().substring(0, 1).toUpperCase() + e.getKey().substring(1);
            return gameFactory.getResourceType(name).orElseThrow(() -> new NoSuchElementException(name));
        }, e -> e.getValue()
        ));
    }
}
