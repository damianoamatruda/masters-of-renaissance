package it.polimi.ingsw.common.backend.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.common.backend.model.actiontokens.ActionToken;
import it.polimi.ingsw.common.backend.model.actiontokens.ActionTokenBlackMoveOneShuffle;
import it.polimi.ingsw.common.backend.model.actiontokens.ActionTokenBlackMoveTwo;
import it.polimi.ingsw.common.backend.model.actiontokens.ActionTokenDiscardTwo;
import it.polimi.ingsw.common.backend.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.common.backend.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.common.backend.model.leadercards.*;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Factory class that builds game instances from file parameters, by acting like an adapter for parsing. */
public class FileGameFactory implements GameFactory {
    /** The parser object corresponding to the config file. */
    private final JsonObject rootObject;

    /** Maximum number of players for each Game. */
    private final int maxPlayers;

    /** Maximum size of a Warehouse shelf. */
    private final int warehouseShelvesCount;

    /** Initial number of leader cards per player. */
    private final int playerLeadersCount;

    /** The maximum amount of faith points a player can have. */
    private final int maxFaith;

    /** The number of development cards a player can have before triggering the end of a game. */
    private final int maxDevCards;

    /** The maximum level a development card can have. */
    private final int levelsCount;

    /** Number of development card production slots per player. */
    private final int slotsCount;

    /** The development card colors. */
    private final Map<String, DevCardColor> devCardColorMap;

    /** The resource types. */
    private final Map<String, ResourceType> resTypeMap;

    /** The base production shared by all players. */
    private ResourceTransactionRecipe baseProduction;

    /**
     * Instantiates a new Game factory that is able to build Game instances based on parameters parsed from a config
     * file.
     *
     * @param inputStream the input stream to parse
     */
    public FileGameFactory(InputStream inputStream) {
        Gson gson = new Gson();

        rootObject = gson.fromJson(new InputStreamReader(inputStream), JsonObject.class);

        /* Parses all simple parameters */
        maxPlayers = gson.fromJson(rootObject.get("maxPlayers"), int.class);
        warehouseShelvesCount = gson.fromJson(rootObject.get("warehouseShelvesCount"), int.class);
        playerLeadersCount = gson.fromJson(rootObject.get("playerLeadersCount"), int.class);
        maxFaith = gson.fromJson(rootObject.get("maxFaith"), int.class);
        maxDevCards = gson.fromJson(rootObject.get("maxDevCards"), int.class);
        levelsCount = gson.fromJson(rootObject.get("levelsCount"), int.class);
        slotsCount = gson.fromJson(rootObject.get("slotsCount"), int.class);

        devCardColorMap = buildDevCardColors().stream().collect(Collectors.toUnmodifiableMap(DevCardColor::getName, Function.identity()));
        resTypeMap = buildResourceTypes().stream().collect(Collectors.toUnmodifiableMap(ResourceType::getName, Function.identity()));
    }

    @Override
    public Game getMultiGame(List<String> nicknames) {
        checkNumberOfPlayers(nicknames, true);
        
        baseProduction = buildBaseProduction();
        List<ResourceTransactionRecipe> productions = new ArrayList<>(List.of(baseProduction));
        List<Warehouse> warehouses = new ArrayList<>();
        List<Strongbox> strongboxes = new ArrayList<>();
        buildBaseContainers(warehouses, strongboxes, nicknames.size());
        List<LeaderCard> leaderCards = buildLeaderCards(productions);
        List<DevelopmentCard> developmentCards = buildDevCards();
        List<ResourceContainer> resContainers = getResContainers(leaderCards);
        addDevCardProductions(productions, developmentCards);
        
        return new Game(
                getPlayers(nicknames, warehouses, strongboxes, leaderCards, resContainers), devCardColorMap.values().stream().toList(), resTypeMap.values().stream().toList(),
                leaderCards,
                developmentCards,
                resContainers,
                productions,
                new DevCardGrid(developmentCards, levelsCount, devCardColorMap.size()),
                buildMarket(),
                buildFaithTrack(),
                maxDevCards,
                slotsCount
        );
    }

    @Override
    public SoloGame getSoloGame(String nickname) {
        baseProduction = buildBaseProduction();
        List<ResourceTransactionRecipe> productions = new ArrayList<>(List.of(baseProduction));
        List<Warehouse> warehouses = new ArrayList<>();
        List<Strongbox> strongboxes = new ArrayList<>();
        buildBaseContainers(warehouses, strongboxes, 1);
        List<LeaderCard> leaderCards = buildLeaderCards(productions);
        List<DevelopmentCard> developmentCards = buildDevCards();
        List<ResourceContainer> resContainers = getResContainers(leaderCards);
        addDevCardProductions(productions, developmentCards);
        List<ActionToken> actionTokens = buildActionTokens();
        Collections.shuffle(actionTokens);

        return new SoloGame(
                getPlayers(List.of(nickname), warehouses, strongboxes, leaderCards, resContainers).get(0),
                devCardColorMap.values().stream().toList(), resTypeMap.values().stream().toList(), leaderCards,
                developmentCards,
                resContainers,
                productions,
                actionTokens,
                new DevCardGrid(developmentCards, levelsCount, devCardColorMap.size()),
                buildMarket(),
                buildFaithTrack(),
                maxDevCards,
                slotsCount
        );
    }

    private void buildBaseContainers(List<Warehouse> warehouses, List<Strongbox> strongboxes, int playersCount) {
        for(int i = 0; i < playersCount; i++) {
            warehouses.add(new Warehouse(warehouseShelvesCount));
            strongboxes.add(new Strongbox());
        }
    }

    /**
     * Getter of a development card color in the game by its name.
     *
     * @param name the name of the development card color
     * @return the development card color
     */
    public Optional<DevCardColor> getDevCardColor(String name) {
        return Optional.ofNullable(devCardColorMap.get(name));
    }

    /**
     * Getter of a resource type in the game by its name.
     *
     * @param name the name of the resource type
     * @return the resource type
     */
    public Optional<ResourceType> getResourceType(String name) {
        return Optional.ofNullable(resTypeMap.get(name));
    }

    /**
     * Checks the number of given players.
     *
     * @param nicknames the list of nicknames
     * @param isMultiGame whether the requested game is a multiplayer game
     */
    private void checkNumberOfPlayers(List<String> nicknames, boolean isMultiGame) {
        
        String baseMsg = "Invalid number of players chosen";
        if (nicknames == null)
            throw new IllegalArgumentException(String.format("%s: %s.", baseMsg, "null"));
            else if (nicknames.isEmpty())
                throw new IllegalArgumentException(String.format("%s: 0.", baseMsg));
        else if (nicknames.size() == 1 && isMultiGame)
            throw new IllegalArgumentException("Cannot create MultiGame with only one player.");
        else if (nicknames.size() > maxPlayers)
            throw new IllegalArgumentException(
                    String.format("%s %d: maximum allowed is %d.", baseMsg, nicknames.size(), maxPlayers));
    }

    /**
     * Returns a list of players.
     *
     * @param nicknames   the list of nicknames
     * @param warehouses  the list of allocated warehouses
     * @param strongboxes the list of allocated strongboxes
     * @param leaderCards the list of leader cards
     * @return the players
     */
    private List<Player> getPlayers(List<String> nicknames, List<Warehouse> warehouses,
                                    List<Strongbox> strongboxes, List<LeaderCard> leaderCards,
                                    List<ResourceContainer> resContainers) {
        List<Player> players = new ArrayList<>();

        /* Shuffle the nicknames */
        List<String> shuffledNicknames = new ArrayList<>(nicknames);
        Collections.shuffle(shuffledNicknames);

        /* Shuffle the leader cards */
        List<LeaderCard> shuffledLeaderCards = new ArrayList<>(leaderCards);
        Collections.shuffle(shuffledLeaderCards);

        List<PlayerSetup> playerSetups = buildPlayerSetups();

        for (int i = 0; i < shuffledNicknames.size(); i++) {
            Warehouse warehouse = warehouses.get(i);
            Strongbox strongbox = strongboxes.get(i);

            resContainers.addAll(warehouse.getShelves());
            resContainers.add(strongbox);

            players.add(new Player(
                    shuffledNicknames.get(i),
                    i == 0,
                    shuffledLeaderCards.subList(playerLeadersCount * i, playerLeadersCount * (i + 1)),
                    warehouse,
                    strongbox,
                    baseProduction,
                    slotsCount,
                    playerSetups.get(i)));
        }

        return players;
    }

    /**
     * Returns a list of resource containers based on given leader cards.
     *
     * @param leaderCards the leader cards
     * @return the resource containers
     */
    private List<ResourceContainer> getResContainers(List<LeaderCard> leaderCards) {
        return new ArrayList<>(leaderCards.stream()
                .map(lc -> lc.getDepot(true))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());
    }

    /**
     * Extracts all productions from the development cards and saves them in the production list.
     *
     * @param productions      the existing list of productions
     * @param developmentCards the development cards
     */
    private void addDevCardProductions(List<ResourceTransactionRecipe> productions, List<DevelopmentCard> developmentCards) {
        productions.addAll(developmentCards.stream().map(DevelopmentCard::getProduction).toList());
    }

    /**
     * Returns the set of all the card colors.
     *
     * @return all the cars colors
     */
    private Set<DevCardColor> buildDevCardColors() {
        return new Gson().fromJson(rootObject.get("cardColors"), new TypeToken<Set<DevCardColor>>() {
        }.getType());
    }

    /**
     * Returns the set of all the resource types.
     *
     * @return all the resource types
     */
    private Set<ResourceType> buildResourceTypes() {
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(ResourceType.class, (JsonDeserializer<ResourceType>) (jsonElement, type, jsonDeserializationContext) -> {
                    Gson gson1 = new Gson();
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    JsonElement typeElement = jsonObject.get("type");
                    try {
                        return typeElement == null ? gson1.fromJson(jsonObject, ResourceType.class) : gson1.fromJson(jsonObject, Class.forName("it.polimi.ingsw.common.backend.model.resourcetypes." + typeElement.getAsString()).asSubclass(ResourceType.class));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .create();
        return gson.fromJson(rootObject.get("resourceTypes"), new TypeToken<Set<ResourceType>>() {
        }.getType());
    }

    private ResourceTransactionRecipe buildBaseProduction() {
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(ResourceTransactionRecipe.class, new ProductionDeserializer(new ArrayList<>()))
                .create();
        return gson.fromJson(rootObject.get("baseProduction"), ResourceTransactionRecipe.class);
    }

    private List<PlayerSetup> buildPlayerSetups() {
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(ResourceType.class, new ResourceTypeDeserializer())
                .create();
        return gson.fromJson(rootObject.get("playerSetups"), new TypeToken<List<PlayerSetup>>() {
        }.getType());
    }

    /**
     * Returns a list of all possible leader cards.
     *
     * @return list of leader cards
     */
    private List<LeaderCard> buildLeaderCards(List<ResourceTransactionRecipe> productions) {
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(LeaderCard.class, new LeaderCardDeserializer(productions))
                .create();
        List<LeaderCard> leaders = gson.fromJson(rootObject.get("leaderCards"), new TypeToken<List<LeaderCard>>() {
        }.getType());

        if (leaders.size() < maxPlayers * playerLeadersCount)
            throw new IllegalArgumentException(
                    String.format("Not enough leader cards available to be assigned to %d players: %d available, %d needed.",
                            maxPlayers, leaders.size(), maxPlayers * playerLeadersCount));

        return leaders;
    }

    /**
     * Returns a list of all possible development cards.
     *
     * @return list of development cards
     */
    private List<DevelopmentCard> buildDevCards() {
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(DevelopmentCard.class, new DevelopmentCardDeserializer())
                .create();
        return gson.fromJson(rootObject.get("developmentCards"), new TypeToken<List<DevelopmentCard>>() {
        }.getType());
    }

    /**
     * Returns a new Market instance.
     *
     * @return the Market
     */
    private Market buildMarket() {
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Market.class, (JsonDeserializer<Market>) (jsonElement, type, jsonDeserializationContext) -> {
                    Gson gson1 = new GsonBuilder()
                            .registerTypeHierarchyAdapter(ResourceType.class, new ResourceTypeDeserializer())
                            .create();
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    Map<ResourceType, Integer> resources = gson1.fromJson(jsonObject.get("resources"), new TypeToken<Map<ResourceType, Integer>>() {
                    }.getType());
                    int columns = gson1.fromJson(jsonObject.get("colsCount"), int.class);
                    ResourceType replaceableResource = gson1.fromJson(jsonObject.get("replaceableResource"), ResourceType.class);

                    return new Market(resources, columns, replaceableResource);
                })
                .create();
        return gson.fromJson(rootObject.get("market"), Market.class);
    }

    /**
     * builds the Faith Track.
     *
     * @return new faith track
     */
    private FaithTrack buildFaithTrack() {
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(FaithTrack.class, (JsonDeserializer<FaithTrack>) (jsonElement, type, jsonDeserializationContext) -> {
                    Gson gson1 = new GsonBuilder()
                            .registerTypeHierarchyAdapter(ResourceType.class, new ResourceTypeDeserializer())
                            .registerTypeHierarchyAdapter(FaithTrack.VaticanSection.class, new VaticanSectionDeserializer())
                            .create();
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    Set<FaithTrack.YellowTile> tiles = gson1.fromJson(jsonObject.get("yellowTiles"), new TypeToken<Set<FaithTrack.YellowTile>>() {
                    }.getType());
                    Set<FaithTrack.VaticanSection> sections = gson1.fromJson(jsonObject.get("vaticanSections"), new TypeToken<Set<FaithTrack.VaticanSection>>() {
                    }.getType());
                    return new FaithTrack(sections, tiles, maxFaith);
                })
                .create();
        return gson.fromJson(rootObject.get("faithTrack"), FaithTrack.class);
    }

    /**
     * Returns a list of the action tokens.
     *
     * @return list of action tokens
     */
    private List<ActionToken> buildActionTokens() {
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(ActionToken.class, new ActionTokenDeserializer())
                .create();
        return gson.fromJson(rootObject.get("actionTokens"), new TypeToken<List<ActionToken>>() {
        }.getType());
    }

    private class RequirementDeserializer implements JsonDeserializer<CardRequirement> {
        @Override
        public CardRequirement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Gson gson = new GsonBuilder()
                    .registerTypeHierarchyAdapter(DevCardColor.class, new DevCardColorDeserializer())
                    .registerTypeHierarchyAdapter(ResourceType.class, new ResourceTypeDeserializer())
                    .create();
            try {
                return gson.fromJson(jsonElement, Class.forName("it.polimi.ingsw.common.backend.model.cardrequirements." + jsonElement.getAsJsonObject().get("type").getAsString()).asSubclass(CardRequirement.class));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class DevCardColorDeserializer implements JsonDeserializer<DevCardColor> {
        @Override
        public DevCardColor deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return getDevCardColor(jsonElement.getAsString()).orElseThrow(() -> new RuntimeException("The given color does not exist."));
        }
    }

    private class ResourceTypeDeserializer implements JsonDeserializer<ResourceType> {
        @Override
        public ResourceType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return getResourceType(jsonElement.getAsString()).orElseThrow(() -> new RuntimeException("The given resource type does not exist."));
        }
    }

    private class VaticanSectionDeserializer implements JsonDeserializer<FaithTrack.VaticanSection> {
        @Override
        public FaithTrack.VaticanSection deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Gson gson = new GsonBuilder()
                    .registerTypeHierarchyAdapter(DevCardColor.class, new DevCardColorDeserializer())
                    .registerTypeHierarchyAdapter(ResourceType.class, new ResourceTypeDeserializer())
                    .registerTypeHierarchyAdapter(ResourceTransactionRecipe.class, new ProductionDeserializer(new ArrayList<>()))
                    .create();
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            int id = gson.fromJson(jsonObject.get("id"), int.class);
            int faithPointsBeginning = gson.fromJson(jsonObject.get("faithPointsBeginning"), int.class);
            int faithPointsEnd = gson.fromJson(jsonObject.get("faithPointsEnd"), int.class);
            int victoryPoints = gson.fromJson(jsonObject.get("victoryPoints"), int.class);

            return new FaithTrack.VaticanSection(id, faithPointsBeginning, faithPointsEnd, victoryPoints);
        }
    }

    private class ActionTokenDeserializer implements JsonDeserializer<ActionToken> {
        @Override
        public ActionToken deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            try {
                Gson gson = new GsonBuilder()
                        .registerTypeHierarchyAdapter(DevCardColor.class, new DevCardColorDeserializer())
                        .registerTypeAdapter(ActionTokenBlackMoveOneShuffle.class, (JsonDeserializer<ActionTokenBlackMoveOneShuffle>) (jsonElement1, type1, jsonDeserializationContext1) -> {
                            Gson gson1 = new Gson();
                            JsonObject jsonObject1 = jsonElement1.getAsJsonObject();

                            int id = gson1.fromJson(jsonObject1.get("id"), int.class);

                            return new ActionTokenBlackMoveOneShuffle(id);
                        })
                        .registerTypeAdapter(ActionTokenBlackMoveTwo.class, (JsonDeserializer<ActionTokenBlackMoveTwo>) (jsonElement1, type1, jsonDeserializationContext1) -> {
                            Gson gson1 = new Gson();
                            JsonObject jsonObject1 = jsonElement1.getAsJsonObject();

                            int id = gson1.fromJson(jsonObject1.get("id"), int.class);

                            return new ActionTokenBlackMoveTwo(id);
                        })
                        .registerTypeAdapter(ActionTokenDiscardTwo.class, (JsonDeserializer<ActionTokenDiscardTwo>) (jsonElement1, type1, jsonDeserializationContext1) -> {
                            Gson gson1 = new GsonBuilder()
                                    .registerTypeHierarchyAdapter(DevCardColor.class, new DevCardColorDeserializer())
                                    .create();
                            JsonObject jsonObject1 = jsonElement1.getAsJsonObject();

                            int id = gson1.fromJson(jsonObject1.get("id"), int.class);
                            DevCardColor discardedColor = gson1.fromJson(jsonObject1.get("discardedColor"), DevCardColor.class);

                            return new ActionTokenDiscardTwo(id, discardedColor);
                        })
                        .create();
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonElement typeElement = jsonObject.get("type");
                return gson.fromJson(jsonObject, Class.forName("it.polimi.ingsw.common.backend.model.actiontokens." + typeElement.getAsString()).asSubclass(ActionToken.class));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class DevelopmentCardDeserializer implements JsonDeserializer<DevelopmentCard> {
        @Override
        public DevelopmentCard deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Gson gson = new GsonBuilder()
                    .registerTypeHierarchyAdapter(DevCardColor.class, new DevCardColorDeserializer())
                    .registerTypeHierarchyAdapter(ResourceType.class, new ResourceTypeDeserializer())
                    .registerTypeHierarchyAdapter(ResourceTransactionRecipe.class, new ProductionDeserializer(new ArrayList<>()))
                    .create();
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            DevCardColor color = gson.fromJson(jsonObject.get("color"), DevCardColor.class);
            int level = gson.fromJson(jsonObject.get("level"), int.class);
            ResourceRequirement cost = gson.fromJson(jsonObject.get("cost"), ResourceRequirement.class);
            ResourceTransactionRecipe production = gson.fromJson(jsonObject.get("production"), ResourceTransactionRecipe.class);
            int victoryPoints = gson.fromJson(jsonObject.get("victoryPoints"), int.class);
            int id = gson.fromJson(jsonObject.get("id"), int.class);

            return new DevelopmentCard(color, level, cost, production, victoryPoints, id);
        }
    }

    private class LeaderCardDeserializer implements JsonDeserializer<LeaderCard> {
        private final List<ResourceTransactionRecipe> productions;

        public LeaderCardDeserializer(List<ResourceTransactionRecipe> productions) {
            this.productions = productions;
        }

        @Override
        public LeaderCard deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(DepotLeader.class, (JsonDeserializer<DepotLeader>) (jsonElement1, type1, jsonDeserializationContext1) -> {
                        Gson gson1 = new GsonBuilder()
                                .registerTypeHierarchyAdapter(ResourceTransactionRecipe.class, new ProductionDeserializer(productions))
                                .registerTypeHierarchyAdapter(ResourceType.class, new ResourceTypeDeserializer())
                                .registerTypeHierarchyAdapter(DevCardColor.class, new DevCardColorDeserializer())
                                .registerTypeHierarchyAdapter(CardRequirement.class, new RequirementDeserializer())
                                .create();
                        JsonObject jsonObject1 = jsonElement1.getAsJsonObject();

                        int shelfSize = gson1.fromJson(jsonObject1.get("shelfSize"), int.class);
                        ResourceType resource = gson1.fromJson(jsonObject1.get("resource"), ResourceType.class);
                        CardRequirement requirement = gson1.fromJson(jsonObject1.get("requirement"), CardRequirement.class);
                        int victoryPoints = gson1.fromJson(jsonObject1.get("victoryPoints"), int.class);
                        int id = gson1.fromJson(jsonObject1.get("id"), int.class);

                        return new DepotLeader(shelfSize, resource, requirement, victoryPoints, id);
                    })
                    .registerTypeAdapter(DiscountLeader.class, (JsonDeserializer<DiscountLeader>) (jsonElement1, type1, jsonDeserializationContext1) -> {
                        Gson gson1 = new GsonBuilder()
                                .registerTypeHierarchyAdapter(ResourceTransactionRecipe.class, new ProductionDeserializer(productions))
                                .registerTypeHierarchyAdapter(ResourceType.class, new ResourceTypeDeserializer())
                                .registerTypeHierarchyAdapter(DevCardColor.class, new DevCardColorDeserializer())
                                .registerTypeHierarchyAdapter(CardRequirement.class, new RequirementDeserializer())
                                .create();
                        JsonObject jsonObject1 = jsonElement1.getAsJsonObject();

                        int discount = gson1.fromJson(jsonObject1.get("discount"), int.class);
                        ResourceType resource = gson1.fromJson(jsonObject1.get("resource"), ResourceType.class);
                        CardRequirement requirement = gson1.fromJson(jsonObject1.get("requirement"), CardRequirement.class);
                        int victoryPoints = gson1.fromJson(jsonObject1.get("victoryPoints"), int.class);
                        int id = gson1.fromJson(jsonObject1.get("id"), int.class);

                        return new DiscountLeader(discount, resource, requirement, victoryPoints, id);
                    })
                    .registerTypeAdapter(ProductionLeader.class, (JsonDeserializer<ProductionLeader>) (jsonElement1, type1, jsonDeserializationContext1) -> {
                        Gson gson1 = new GsonBuilder()
                                .registerTypeHierarchyAdapter(ResourceTransactionRecipe.class, new ProductionDeserializer(productions))
                                .registerTypeHierarchyAdapter(ResourceType.class, new ResourceTypeDeserializer())
                                .registerTypeHierarchyAdapter(DevCardColor.class, new DevCardColorDeserializer())
                                .registerTypeHierarchyAdapter(CardRequirement.class, new RequirementDeserializer())
                                .create();
                        JsonObject jsonObject1 = jsonElement1.getAsJsonObject();

                        ResourceTransactionRecipe production = gson1.fromJson(jsonObject1.get("production"), ResourceTransactionRecipe.class);
                        ResourceType resource = gson1.fromJson(jsonObject1.get("resource"), ResourceType.class);
                        CardRequirement requirement = gson1.fromJson(jsonObject1.get("requirement"), CardRequirement.class);
                        int victoryPoints = gson1.fromJson(jsonObject1.get("victoryPoints"), int.class);
                        int id = gson1.fromJson(jsonObject1.get("id"), int.class);

                        return new ProductionLeader(production, resource, requirement, victoryPoints, id);
                    })
                    .registerTypeAdapter(ZeroLeader.class, (JsonDeserializer<ZeroLeader>) (jsonElement1, type1, jsonDeserializationContext1) -> {
                        Gson gson1 = new GsonBuilder()
                                .registerTypeHierarchyAdapter(ResourceTransactionRecipe.class, new ProductionDeserializer(productions))
                                .registerTypeHierarchyAdapter(ResourceType.class, new ResourceTypeDeserializer())
                                .registerTypeHierarchyAdapter(DevCardColor.class, new DevCardColorDeserializer())
                                .registerTypeHierarchyAdapter(CardRequirement.class, new RequirementDeserializer())
                                .create();
                        JsonObject jsonObject1 = jsonElement1.getAsJsonObject();

                        ResourceType resource = gson1.fromJson(jsonObject1.get("resource"), ResourceType.class);
                        CardRequirement requirement = gson1.fromJson(jsonObject1.get("requirement"), CardRequirement.class);
                        int victoryPoints = gson1.fromJson(jsonObject1.get("victoryPoints"), int.class);
                        int id = gson1.fromJson(jsonObject1.get("id"), int.class);

                        return new ZeroLeader(resource, requirement, victoryPoints, id);
                    })
                    .create();
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonElement typeElement = jsonObject.get("type");
            try {
                return gson.fromJson(jsonElement, Class.forName("it.polimi.ingsw.common.backend.model.leadercards." + typeElement.getAsString()).asSubclass(LeaderCard.class));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class ProductionDeserializer implements JsonDeserializer<ResourceTransactionRecipe> {
        private final List<ResourceTransactionRecipe> productions;

        public ProductionDeserializer(List<ResourceTransactionRecipe> productions) {
            this.productions = productions;
        }

        @Override
        public ResourceTransactionRecipe deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Gson gson = new GsonBuilder()
                    .registerTypeHierarchyAdapter(ResourceType.class, new ResourceTypeDeserializer())
                    .create();
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            Map<ResourceType, Integer> input = gson.fromJson(jsonObject.get("input"), new TypeToken<Map<ResourceType, Integer>>() {
            }.getType());
            int inputBlanks = gson.fromJson(jsonObject.get("inputBlanks"), int.class);
            Set<ResourceType> inputBlanksExclusions = gson.fromJson(jsonObject.get("inputBlanksExclusions"), new TypeToken<Set<ResourceType>>() {
            }.getType());
            Map<ResourceType, Integer> output = gson.fromJson(jsonObject.get("output"), new TypeToken<Map<ResourceType, Integer>>() {
            }.getType());
            int outputBlanks = gson.fromJson(jsonObject.get("outputBlanks"), int.class);
            Set<ResourceType> outputBlanksExclusions = gson.fromJson(jsonObject.get("outputBlanksExclusions"), new TypeToken<Set<ResourceType>>() {
            }.getType());

            ResourceTransactionRecipe recipe = new ResourceTransactionRecipe(input, inputBlanks, inputBlanksExclusions, output, outputBlanks, outputBlanksExclusions, false);
            if (productions != null)
                productions.add(recipe);
            return recipe;
        }
    }
}
