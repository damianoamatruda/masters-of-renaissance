package it.polimi.ingsw.client.viewmodel;

import it.polimi.ingsw.common.reducedmodel.*;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard.LeaderType;

import java.util.*;
import java.util.stream.Stream;

/** View model of the client of the Masters of Renaissance game. */
public class ViewModel {
    /** The nickname of the local player. */
    private String localPlayer;

    /** The state of finalization of the setup phase. */
    private Boolean isSetupDone;

    /** The list of action tokens present in the game. */
    private List<ReducedActionToken> actionTokens;

    /** The faith points of Lorenzo il Magnifico. */
    private int blackCrossFP;

    /** The list of resource containers present in the game. */
    private List<ReducedResourceContainer> containers;

    /** The current player's nickname. */
    private String currentPlayer;

    /** The list of development card colors present in the game. */
    private List<ReducedColor> devCardColors;

    /** The development card grid state. */
    private ReducedDevCardGrid devCardGrid;

    /** The list of development cards present in the game. */
    private List<ReducedDevCard> developmentCards;

    /** The development slots count. */
    private int devSlotsCount;

    /** The faith track state. */
    private ReducedFaithTrack faithTrack;

    /** The status of finalization of the game. */
    private boolean gameEnded = false;

    /** The status of last round of the game. */
    private boolean lastRound;

    /** The latest action token activated. */
    private ReducedActionToken latestToken;

    /** The list of leader cards present in the game. */
    private List<ReducedLeaderCard> leaderCards;

    /** The game market. */
    private ReducedMarket market;

    /** The list of players, by their nicknames. */
    private List<ReducedPlayer> players;

    /** The list of productions present in the game. */
    private List<ReducedResourceTransactionRecipe> productions;

    /** The list of resource types present in the game. */
    private List<ReducedResourceType> resourceTypes;

    /** The nickname of the winner. */
    private String winnerPlayer;

    /** The list of the available user interface ANSI colors. */
    private final List<String> ansiColors = List.of("\u001B[92m", "\u001B[94m", "\u001B[95m", "\u001B[96m");

    /** The map of unique player string encoded ANSI colors. */
    private final Map<String, String> ansiPlayerColors;

    /** The list of the available user interface HEX colors. */
    private final List<String> hexColors = List.of("#5D99FD", "#961126", "#14A76C", "#ffe933");

    /** The map of unique player string encoded HEX colors. */
    private final Map<String, String> hexPlayerColors;

    // TODO Javadoc
    private String inkwellPlayer;

    /**
     * Class constructor. Initializes empty objects.
     */
    public ViewModel() {
        actionTokens = new ArrayList<>();
        containers = new ArrayList<>();
        devCardColors = new ArrayList<>();
        developmentCards = new ArrayList<>();
        leaderCards = new ArrayList<>();
        players = new ArrayList<>();
        productions = new ArrayList<>();
        resourceTypes = new ArrayList<>();
        lastRound = false;
        ansiPlayerColors = new HashMap<>();
        hexPlayerColors = new HashMap<>();
    }

    /**
     * Retrieves the data regarding one of the players.
     *
     * @param nickname the nickname of the player whose data needs to be retrieved
     * @return the player
     */
    public synchronized Optional<ReducedPlayer> getPlayer(String nickname) {
        return players.stream().filter(player -> player.getNickname().equals(nickname)).findAny();
    }

    /**
     * TODO
     *
     * @param player the player's data
     */
    public synchronized void putPlayer(ReducedPlayer player) {
        players.replaceAll(p -> p.getNickname().equals(player.getNickname()) ? player : p);
    }

    /**
     * TODO
     *
     * @param nickname
     * @return the topmost development cards in the player's slots
     */
    public synchronized Optional<ReducedResourceTransactionRecipe> getPlayerBaseProduction(String nickname) {
        return getPlayer(nickname).map(ReducedPlayer::getBaseProduction).flatMap(this::getProduction);
    }

    /**
     * @param id the ID of the development card the discounted cost of which needs to be computed
     * @return the cost of the development card discounted by the active leader cards of the local player
     */
    public synchronized Map<String, Integer> getDevCardDiscountedCost(int id) {
        Map<String, Integer> discountedCost = new HashMap<>(getDevelopmentCard(id)
                .flatMap(ReducedDevCard::getCost)
                .map(ReducedResourceRequirement::getRequirements)
                .orElse(Map.of()));

        List<ReducedLeaderCard> discountLeaders = getPlayerLeaderCards(localPlayer).stream()
                .filter(ReducedLeaderCard::isActive)
                .filter(c -> c.getLeaderType() == LeaderType.DISCOUNT)
                .toList();

        discountLeaders.forEach(l ->
                discountedCost.computeIfPresent(l.getResourceType(), (r, oldCost) -> oldCost - l.getDiscount() > 0 ? oldCost - l.getDiscount() : null));

        return discountedCost;
    }

    /**
     * Retrieves the development cards owned by a player. Empty slots are represented by empty lists.
     *
     * @param nickname the nickname of the player whose development cards need to be retrieved
     * @return all the development cards in the player's slots
     */
    public synchronized List<List<Optional<ReducedDevCard>>> getPlayerDevelopmentCards(String nickname) {
        List<List<Optional<ReducedDevCard>>> cards = new ArrayList<>();

        while (cards.size() < devSlotsCount)
            cards.add(new ArrayList<>());

        getPlayer(nickname).map(ReducedPlayer::getDevSlots).ifPresent(devSlots -> {
            /* Iterate over each slot */
            for (int i = 0; i < cards.size(); i++) {
                /* Get the IDs of all the cards in the slot */
                List<Integer> slotIDs = i < devSlots.size() ? devSlots.get(i) : new ArrayList<>();
                cards.set(i, slotIDs.stream().map(this::getDevelopmentCard).toList());
            }
        });

        return cards;
    }

    /**
     * Retrieves the topmost development cards in a player's development slots.
     * If a slot is empty the corresponding position in the list contains a null element.
     *
     * @param nickname the nickname of the player whose topmost development cards need to be retrieved
     * @return a list containing, for each slot, the optional topmost development card
     */
    public synchronized List<Optional<ReducedDevCard>> getPlayerDevelopmentSlots(String nickname) {
        Stream<Optional<ReducedDevCard>> stream = getPlayerDevelopmentCards(nickname).stream()
                .map(slot -> slot.isEmpty() ? Optional.empty() : slot.get(0));
        return stream.toList();
    }

    /**
     * Retrieves a player's current faith points.
     *
     * @param nickname the player whose faith points are to be returned
     * @return the faith points of the selected player
     */
    public synchronized int getPlayerFaithPoints(String nickname) {
        return getPlayer(nickname).map(ReducedPlayer::getFaithPoints).orElse(0);
    }

    /**
     * Gets the activity/inactivity status of a player.
     *
     * @param nickname the player whose state to be returned
     * @return the state of the selected player
     */
    public synchronized boolean isPlayerActive(String nickname) {
        return getPlayer(nickname).map(ReducedPlayer::isActive).orElse(false);
    }

    /**
     * Retrieves the leader cards hand of a player.
     *
     * @param nickname the nickname of the player whose leader cards hand needs to be retrieved
     * @return the reduced leader cards owned by the player
     */
    public synchronized List<ReducedLeaderCard> getPlayerLeaderCards(String nickname) {
        if (getPlayer(nickname).isEmpty())
            return new ArrayList<>();
        return getPlayer(nickname).get().getLeadersHand().stream()
                .map(this::getLeaderCard)
                .flatMap(Optional::stream)
                .toList();
    }

    /**
     * Retrieves only the active leader cards hand of a player.
     *
     * @param nickname the nickname of the player whose active leader cards hand needs to be retrieved
     * @return the reduced (active) leader cards owned by the player
     */
    public synchronized List<ReducedLeaderCard> getPlayerActiveLeaderCards(String nickname) {
        return getPlayerLeaderCards(nickname).stream()
                .filter(ReducedLeaderCard::isActive)
                .toList();
    }

    /**
     * Retrieves the count of leader cards of a player.
     *
     * @param nickname the nickname of the player whose number of leader cards needs to be retrieved
     * @return the number of leader cards owned by the player
     */
    public synchronized int getPlayerLeadersCount(String nickname) {
        return getPlayer(nickname).map(ReducedPlayer::getLeadersHandCount).orElse(0);
    }

    /**
     * Retrieves the list of productions available to a player.
     *
     * @param nickname the nickname of the player whose list of available productions needs to be retrieved
     * @return the reduced recipes owned by the player, including:
     *          <ul>
     *              <li>the base production
     *              <li>the visible development cards' productions
     *              <li>the active leader cards' productions
     *          </ul>
     */
    public synchronized List<ReducedResourceTransactionRecipe> getPlayerProductions(String nickname) {
        List<Integer> ids = new ArrayList<>();

        getPlayer(nickname).ifPresent(player -> ids.add(player.getBaseProduction()));
        ids.addAll(getPlayerLeaderCards(nickname).stream().filter(ReducedLeaderCard::isActive).map(ReducedCard::getProduction).toList());
        ids.addAll(getPlayerDevelopmentSlots(nickname).stream().flatMap(Optional::stream).map(ReducedCard::getProduction).toList());

        return ids.stream()
                .map(this::getProduction)
                .flatMap(Optional::stream)
                .toList();
    }

    /**
     * Retrieves the resource containers owned by a player.
     *
     * @param nickname the nickname of the player whose resource containers' data needs to be retrieved
     * @return the player's warehouse shelves and depots
     */
    public synchronized List<ReducedResourceContainer> getPlayerShelves(String nickname) {
        List<ReducedResourceContainer> containers = new ArrayList<>();
        containers.addAll(getPlayerWarehouseShelves(nickname));
        containers.addAll(getPlayerDepots(nickname));
        return containers;
    }

    /**
     * Retrieves exclusively the shelves of the warehouse owned by a player.
     *
     * @param nickname the nickname of the player whose warehouse shelves' data needs to be retrieved
     * @return the player's warehouse shelves
     */
    public synchronized List<ReducedResourceContainer> getPlayerWarehouseShelves(String nickname) {
        if (getPlayer(nickname).isEmpty())
            return new ArrayList<>();
        return getPlayer(nickname).get().getWarehouseShelves().stream()
                .map(this::getContainer)
                .flatMap(Optional::stream)
                .toList();
    }

    /**
     * Retrieves exclusively the leader cards depots accessible by a player.
     *
     * @param nickname the nickname of the player whose leader depots' data needs to be retrieved
     * @return the player's active leaders' depots
     */
    public synchronized List<ReducedResourceContainer> getPlayerDepots(String nickname) {
        if (getPlayer(nickname).isEmpty())
            return new ArrayList<>();
        return getPlayerLeaderCards(nickname).stream()
                .filter(ReducedLeaderCard::isActive)
                .map(ReducedLeaderCard::getContainerId)
                .map(this::getContainer)
                .flatMap(Optional::stream)
                .toList();
    }

    /**
     * Retrieves the strongbox of a player.
     *
     * @param nickname the nickname of the player whose strongbox's data needs to be retrieved
     * @return the player's strongbox
     */
    public synchronized Optional<ReducedResourceContainer> getPlayerStrongbox(String nickname) {
        return getPlayer(nickname).map(ReducedPlayer::getStrongbox).flatMap(this::getContainer);
    }

    /**
     * Retrieves the current victory points of a player.
     *
     * @param nickname the nickname of the player whose victory points amount needs to be retrieved
     * @return the player's victory points
     */
    public synchronized int getPlayerVictoryPoints(String nickname) {
        return getPlayer(nickname).map(ReducedPlayer::getVictoryPoints).orElse(0);
    }

    /**
     * Retrieves an action token by means of its ID.
     *
     * @param id the ID of the token to be returned
     * @return the token associated with the ID
     */
    public synchronized Optional<ReducedActionToken> getActionToken(int id) {
        return actionTokens.stream().filter(t -> t.getId() == id).findAny();
    }

    /**
     * Sets the action tokens in the current order
     *
     * @param actionTokens the actionTokens to set
     */
    public synchronized void setActionTokens(List<ReducedActionToken> actionTokens) {
        if (actionTokens == null)
            return;
        this.actionTokens = new ArrayList<>(actionTokens);
    }

    /**
     * Retrieves Lorenzo's faith points amount
     *
     * @return blackCross' faith points
     */
    public synchronized int getBlackCrossFP() {
        return blackCrossFP;
    }

    /**
     * Sets Lorenzo's faith points amount
     *
     * @param blackCrossFP the blackCross faith points to set
     */
    public synchronized void setBlackCrossFP(int blackCrossFP) {
        this.blackCrossFP = blackCrossFP;
    }

    /**
     * Retrieves a resource container by means of its ID.
     *
     * @param id the id of the container to be returned
     * @return the container corresponding to the id
     */
    public synchronized Optional<ReducedResourceContainer> getContainer(int id) {
        return containers.stream().filter(c -> c.getId() == id).findAny();
    }

    /**
     * Retrieves all resource containers.
     *
     * @return the containers
     */
    public synchronized List<ReducedResourceContainer> getContainers() {
        return containers;
    }

    /**
     * Sets the resource containers used in the game.
     *
     * @param containers the containers to set
     */
    public synchronized void setContainers(List<ReducedResourceContainer> containers) {
        if (containers == null)
            return;
        this.containers = new ArrayList<>(containers);
    }

    /**
     * Sets a specific resource container used in the game.
     *
     * @param container the container to set
     */
    public synchronized void setContainer(ReducedResourceContainer container) {
        if (container == null)
            return;
        containers.replaceAll(c -> c.getId() == container.getId() ? container : c);
    }

    /**
     * Retrieves the nickname of the player now playing a turn.
     *
     * @return the currentPlayer
     */
    public synchronized Optional<String> getCurrentPlayer() {
        return Optional.ofNullable(currentPlayer);
    }

    /**
     * Retrieves whether the local player is now the current player.
     *
     * @return true if the local player is now the current player
     */
    public synchronized boolean isCurrentPlayer() {
        return currentPlayer.equals(localPlayer);
    }

    /**
     * Retrieves all the development card colors being used.
     *
     * @return the devCardColors
     */
    public synchronized List<ReducedColor> getDevCardColors() {
        return devCardColors;
    }

    /**
     * Sets all the development card colors being used.
     *
     * @param devCardColors the devCardColors to set
     */
    public synchronized void setDevCardColors(List<ReducedColor> devCardColors) {
        if (devCardColors == null)
            return;
        this.devCardColors = new ArrayList<>(devCardColors);
    }

    /**
     * Retrieves the current state of the development card grid.
     *
     * @return the devCardGrid
     */
    public synchronized Optional<ReducedDevCardGrid> getDevCardGrid() {
        return Optional.ofNullable(devCardGrid);
    }

    /**
     * Sets the state of the development card grid.
     *
     * @param devCardGrid the devCardGrid to set
     */
    public synchronized void setDevCardGrid(ReducedDevCardGrid devCardGrid) {
        this.devCardGrid = devCardGrid;
    }

    /**
     * Retrieves a development card by means of its ID.
     *
     * @param id the ID of the card to be returned
     * @return the developmentCard matching the ID
     */
    public synchronized Optional<ReducedDevCard> getDevelopmentCard(int id) {
        return developmentCards.stream().filter(c -> c.getId() == id).findAny();
    }

    /**
     * Returns a development card from the top cards of the development card grid,
     * based on color and level.
     *
     * @param color the card's color
     * @param level the card's level
     * @return the card of the specified color and level
     */
    public synchronized Optional<ReducedDevCard> getDevCardFromGrid(String color, int level) {
        if (devCardGrid == null || !devCardGrid.getTopCards().containsKey(color) || level >= devCardGrid.getTopCards().get(color).size())
            return Optional.empty();
        return devCardGrid.getTopCards().get(color).get(level).flatMap(this::getDevelopmentCard);
    }

    /**
     * Sets all the development cards of the game.
     *
     * @param developmentCards the developmentCards to set
     */
    public synchronized void setDevelopmentCards(List<ReducedDevCard> developmentCards) {
        if (developmentCards == null)
            return;
        this.developmentCards = new ArrayList<>(developmentCards);
    }

    /**
     * Retrieves the current state of the faith track.
     *
     * @return the faithTrack
     */
    public synchronized Optional<ReducedFaithTrack> getFaithTrack() {
        return Optional.ofNullable(faithTrack);
    }

    /**
     * Sets the state of the faith track.
     *
     * @param faithTrack the faithTrack to set
     */
    public synchronized void setFaithTrack(ReducedFaithTrack faithTrack) {
        this.faithTrack = faithTrack;
    }

    /**
     * Retrieves whether the game has ended or not.
     *
     * @return whether the game has ended or not
     */
    public synchronized boolean isGameEnded() {
        return gameEnded;
    }

    // TODO Javadoc
    public synchronized void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    /**
     * Retrieves whether the game has reached its last round or not.
     *
     * @return whether it's the last round of the match
     */
    public synchronized boolean isLastRound() {
        return lastRound;
    }

    /**
     * Retrieves the latest action token activated.
     *
     * @return the latest action token activated
     */
    public synchronized Optional<ReducedActionToken> getLatestToken() {
        return Optional.ofNullable(latestToken);
    }

    /**
     * Updates the latest action token activated.
     */
    public synchronized void setLatestToken(ReducedActionToken latestToken) {
        this.latestToken = latestToken;
    }

    /**
     * Retrieves a leader card by means of its ID.
     *
     * @param id the ID of the card to be returned
     * @return the leaderCard matching the ID
     */
    public synchronized Optional<ReducedLeaderCard> getLeaderCard(int id) {
        return leaderCards.stream().filter(c -> c.getId() == id).findAny();
    }

    /**
     * Updates the nickname of the player now playing a turn.
     *
     * @param currentPlayer the currentPlayer to set
     */
    public synchronized void setCurrentPlayer(String currentPlayer) {
        if (currentPlayer == null)
            currentPlayer = ""; // TODO: Do not do anything in this case, instead of setting it to ""
        this.currentPlayer = currentPlayer;
    }

    /**
     * Sets all the leader cards of the game.
     *
     * @param leaderCards the leaderCards to set
     */
    public synchronized void setLeaderCards(List<ReducedLeaderCard> leaderCards) {
        if (leaderCards == null)
            return;
        this.leaderCards = new ArrayList<>(leaderCards);
    }

    /**
     * Retrieves the market state.
     *
     * @return the market
     */
    public synchronized Optional<ReducedMarket> getMarket() {
        return Optional.ofNullable(market);
    }

    /**
     * Sets the market state.
     *
     * @param market the market to set
     */
    public synchronized void setMarket(ReducedMarket market) {
        this.market = market;
    }

    /**
     * Sets last round to true.
     */
    public synchronized void setLastRound(boolean lastRound) {
        this.lastRound = lastRound;
    }

    /**
     * Retrieves all the players that initially joined the game.
     *
     * @return the players
     */
    public synchronized List<ReducedPlayer> getPlayers() {
        return players;
    }

    /**
     * Retrieves a production recipe by means of its ID.
     *
     * @param id the ID of the production to be returned
     * @return the reduced production (transaction recipe)
     */
    public synchronized Optional<ReducedResourceTransactionRecipe> getProduction(int id) {
        return productions.stream().filter(p -> p.getId() == id).findAny();
    }

    /**
     * Sets all the productions in the game.
     *
     * @param productions the productions to set
     */
    public synchronized void setProductions(List<ReducedResourceTransactionRecipe> productions) {
        if (productions == null)
            return;
        this.productions = new ArrayList<>(productions);
    }

    /**
     * Retrieves all resource types of the game.
     *
     * @return the resourceTypes
     */
    public synchronized List<ReducedResourceType> getResourceTypes() {
        return resourceTypes;
    }

    /**
     * Sets all resource types of the game.
     *
     * @param resourceTypes the resourceTypes to set
     */
    public synchronized void setResourceTypes(List<ReducedResourceType> resourceTypes) {
        if (resourceTypes == null)
            return;
        this.resourceTypes = new ArrayList<>(resourceTypes);
    }

    /**
     * Sets all the joined players nicknames.
     *
     * @param players the playerNicknames to set
     */
    public synchronized void setPlayers(List<ReducedPlayer> players) {
        if (this.players != null)
            this.players = new ArrayList<>(players);

        for (int i = 0; i < players.size(); i++) {
            hexPlayerColors.put(players.get(i).getNickname(), hexColors.get(i % hexColors.size()));
            ansiPlayerColors.put(players.get(i).getNickname(), ansiColors.get(i % ansiColors.size()));
        }
    }

    /**
     * Retrieves the max development slots count.
     *
     * @return the max available development slots
     */
    public synchronized int getDevSlotsCount() {
        return devSlotsCount;
    }

    /**
     * Activates the vatican section given by the ID.
     *
     * @param id the ID of the section to activate
     * @param bonusGivenPlayers the players that earned the bonus
     */
    public synchronized void activateVaticanSection(int id, List<String> bonusGivenPlayers) {
        if (faithTrack == null)
            return;
        faithTrack.getVaticanSections().values().stream().filter(vs -> vs.getId() == id).findAny().ifPresent(vs -> {
            vs.setActive();
            vs.setBonusGivenPlayers(bonusGivenPlayers);
        });
    }

    // TODO: Add Javadoc
    public synchronized List<ReducedResourceType> getProductionInputResTypes(ReducedResourceTransactionRecipe production) {
        return resourceTypes.stream()
                .filter(r -> !r.isStorable() && r.isTakeableFromPlayer())
                .filter(r -> !production.getInputBlanksExclusions().contains(r.getName()))
                .toList();
    }

    // TODO: Add Javadoc
    public synchronized List<ReducedResourceType> getProductionOutputResTypes(ReducedResourceTransactionRecipe production) {
        return resourceTypes.stream()
                .filter(r -> r.isStorable() || r.isGiveableToPlayer())
                .filter(r -> !production.getOutputBlanksExclusions().contains(r.getName()))
                .toList();
    }

    /**
     * Sets the max development slots count.
     *
     * @param devSlotsCount the max available development slots
     */
    public synchronized void setDevSlotsCount(int devSlotsCount) {
        this.devSlotsCount = devSlotsCount;
    }

    /**
     * Retrieves the winner of the game.
     *
     * @return the winner
     */
    public synchronized Optional<String> getWinnerPlayer() {
        return Optional.ofNullable(winnerPlayer);
    }

    /**
     * Sets the state of finalization of the setup phase of the game.
     *
     * @param isSetupDone whether the player setup is done
     */
    public synchronized void setSetupDone(boolean isSetupDone) {
        this.isSetupDone = isSetupDone;
    }

    /**
     * Retrieves whether the setup phase of the game has ended.
     *
     * @return whether all the players' setup is done
     */
    public synchronized Optional<Boolean> isSetupDone() {
        return Optional.ofNullable(isSetupDone);
    }

    /**
     * Sets the status of a leader card to activated and adds it to the player
     *
     * @param id the ID of the card to be activated
     */
    public synchronized void activateLeaderCard(int id) {
        leaderCards.replaceAll(l -> l.getId() == id ? l.getActivated() : l);
        getPlayer(currentPlayer).ifPresent(player -> {
            List<Integer> leadersHand = player.getLeadersHand();
            if (!leadersHand.contains(id))
                leadersHand.add(id);
            putPlayer(player.setLeadersHand(leadersHand));
        });
    }

    /**
     * Sets the winner of the game.
     *
     * @param winnerPlayer the winner to set
     */
    public synchronized void setWinnerPlayer(String winnerPlayer) {
        this.winnerPlayer = winnerPlayer;
    }

    /**
     * Retrieves the nickname of the local player's nickname.
     *
     * @return the local player's nickname
     */
    public synchronized Optional<String> getLocalPlayer() {
        return Optional.ofNullable(localPlayer);
    }

    /**
     * Retrieves the uniquely associated ANSI color of a player.
     *
     * @return the string that encodes the ANSI color uniquely associated to a player
     */
    public synchronized Optional<String> getAnsiPlayerColor(String nickname) {
        return Optional.ofNullable(ansiPlayerColors.get(nickname));
    }

    /**
     * Retrieves the uniquely associated HEX color of a player.
     *
     * @return the string that encodes the HEX color uniquely associated to a player
     */
    public synchronized Optional<String> getHexPlayerColor(String nickname) {
        return Optional.ofNullable(hexPlayerColors.get(nickname));
    }

    /**
     * Sets the nickname of the local player's nickname.
     *
     * @param localPlayer the nickname to set
     */
    public synchronized void setLocalPlayer(String localPlayer) {
        this.localPlayer = localPlayer;
    }

    // TODO Javadoc
    public synchronized Optional<String> getInkwellPlayer() {
        return Optional.ofNullable(inkwellPlayer);
    }

    // TODO Javadoc
    public synchronized void setInkwellPlayer(String inkwellPlayer) {
        this.inkwellPlayer = inkwellPlayer;
    }
}
