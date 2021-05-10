package it.polimi.ingsw.server.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.server.model.actiontokens.ActionToken;
import it.polimi.ingsw.server.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.server.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.server.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.server.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Factory class that builds game instances from file parameters, by acting like an adapter for parsing. */
public class FileGameFactory implements GameFactory {
    /** The parser object corresponding to the config file. */
    private final JsonObject parserObject;

    /** The Gson instance used for parsing. */
    private final Gson gson;

    /** Maximum number of players for each Game. */
    private final int maxPlayers;

    /** Maximum size of a Warehouse shelf. */
    private final int warehouseShelvesCount;

    /** Initial number of leader cards per player. */
    private final int playerLeadersCount;

    /** Number of leader cards per player that must be discarded in the early game. */
    private final int chosenLeadersCount;

    /** The maximum amount of faith points a player can have. */
    private final int maxFaith;

    /** The number of development cards a player can have before triggering the end of a game. */
    private final int maxDevCards;

    /** The maximum level a development card can have. */
    private final int levelsCount;

    /** The number of different card colors. */
    private final int colorsCount;

    /** The number of columns of the market grid. */
    private final int marketColumns;

    /** Number of development card production slots per player. */
    private final int slotsCount;

    /** The development card colors. */
    private final Map<String, DevCardColor> devCardColorMap;

    /** The resource types. */
    private final Map<String, ResourceType> resTypeMap;

    /** The base production shared by all players. */
    private final ResourceTransactionRecipe baseProduction;

    /** The boosts. */
    private final List<Boost> boosts;

    /**
     * Instantiates a new Game factory that is able to build Game instances based on parameters parsed from a config
     * file.
     *
     * @param inputStream the input stream to parse
     */
    public FileGameFactory(InputStream inputStream) {
        gson = new GsonBuilder()
                .enableComplexMapKeySerialization().setPrettyPrinting()
                .registerTypeAdapter(ResourceTransactionRecipe.class, new ProductionDeserializer(null))
                .registerTypeAdapter(ResourceType.class, new ResourceDeserializer())
                .registerTypeAdapter(DevCardColor.class, new ColorDeserializer())
                .create();

        parserObject = gson.fromJson(new InputStreamReader(inputStream), JsonObject.class);

        /* Parses all simple parameters */
        maxPlayers = gson.fromJson(parserObject.get("maxPlayers"), int.class);
        warehouseShelvesCount = gson.fromJson(parserObject.get("warehouseShelvesCount"), int.class);
        playerLeadersCount = gson.fromJson(parserObject.get("playerLeadersCount"), int.class);
        chosenLeadersCount = gson.fromJson(parserObject.get("chosenLeadersCount"), int.class);
        maxFaith = gson.fromJson(parserObject.get("maxFaith"), int.class);
        maxDevCards = gson.fromJson(parserObject.get("maxDevCards"), int.class);
        levelsCount = gson.fromJson(parserObject.get("levelsCount"), int.class);
        marketColumns = gson.fromJson(parserObject.get("marketColumns"), int.class);
        slotsCount = gson.fromJson(parserObject.get("slotsCount"), int.class);

        devCardColorMap = generateDevCardColors().stream()
                .collect(Collectors.toUnmodifiableMap(DevCardColor::getName, Function.identity()));

        colorsCount = devCardColorMap.size();

        resTypeMap = generateResourceTypes().stream()
                .collect(Collectors.toUnmodifiableMap(ResourceType::getName, Function.identity()));

        baseProduction = gson.fromJson(parserObject.get("baseProduction"), ResourceTransactionRecipe.class);
        boosts = gson.fromJson(parserObject.get("boosts"), new TypeToken<ArrayList<Boost>>() {
        }.getType());
    }

    @Override
    public Game getMultiGame(List<String> nicknames) {
        checkNumberOfPlayers(nicknames);

        List<ResourceTransactionRecipe> productions = new ArrayList<>(List.of(baseProduction));
        List<LeaderCard> leaderCards = generateLeaderCards(productions);
        List<DevelopmentCard> developmentCards = generateDevCards();
        List<ResourceContainer> resContainers = getResContainers(leaderCards);
        addDevCardProductions(productions, developmentCards);

        return new Game(
                getPlayers(nicknames, leaderCards, resContainers),
                leaderCards,
                developmentCards,
                resContainers,
                productions,
                new DevCardGrid(developmentCards, levelsCount, colorsCount),
                generateMarket(),
                generateFaithTrack(),
                maxFaith,
                maxDevCards
        );
    }

    @Override
    public SoloGame getSoloGame(String nickname) {
        List<ResourceTransactionRecipe> productions = new ArrayList<>(List.of(baseProduction));
        List<LeaderCard> leaderCards = generateLeaderCards(productions);
        List<DevelopmentCard> developmentCards = generateDevCards();
        List<ResourceContainer> resContainers = getResContainers(leaderCards);
        addDevCardProductions(productions, developmentCards);

        return new SoloGame(
                getPlayers(List.of(nickname), leaderCards, resContainers).get(0),
                leaderCards,
                developmentCards,
                resContainers,
                productions,
                new DevCardGrid(developmentCards, levelsCount, colorsCount),
                generateMarket(),
                generateFaithTrack(),
                generateActionTokens(),
                maxFaith,
                maxDevCards
        );
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
     */
    private void checkNumberOfPlayers(List<String> nicknames) {
        String baseMsg = "Invalid number of players chosen";
        if (nicknames == null)
            throw new IllegalArgumentException(String.format("%s: %s", baseMsg, "null"));
        else if (nicknames.size() > maxPlayers)
            throw new IllegalArgumentException(
                    String.format("%s %d: maximum allowed is %d", baseMsg, nicknames.size(), maxPlayers));
        else if (nicknames.size() == 0)
            throw new IllegalArgumentException(String.format("%s: 0", baseMsg));
    }

    /**
     * Returns a list of players.
     *
     * @param nicknames   the list of nicknames
     * @param leaderCards the list of leader cards
     * @return the players
     */
    private List<Player> getPlayers(List<String> nicknames, List<LeaderCard> leaderCards,
                                    List<ResourceContainer> resContainers) {
        List<Player> players = new ArrayList<>();

        /* Shuffle the nicknames */
        List<String> shuffledNicknames = new ArrayList<>(nicknames);
        Collections.shuffle(shuffledNicknames);

        /* Shuffle the leader cards */
        List<LeaderCard> shuffledLeaderCards = new ArrayList<>(leaderCards);
        Collections.shuffle(shuffledLeaderCards);

        for (int i = 0; i < shuffledNicknames.size(); i++) {
            Warehouse warehouse = new Warehouse(warehouseShelvesCount);
            Strongbox strongbox = new Strongbox();

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
                    chosenLeadersCount,
                    boosts.get(i).initialResources,
                    boosts.get(i).initialFaith,
                    boosts.get(i).initialExcludedResources));
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
                .map(LeaderCard::getDepot)
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
        productions.addAll(developmentCards.stream()
                .map(DevelopmentCard::getProduction).toList());
    }

    /**
     * Returns the set of all the card colors.
     *
     * @return all the cars colors
     */
    private Set<DevCardColor> generateDevCardColors() {
        Gson customGson = new GsonBuilder().create();
        return customGson.fromJson(parserObject.get("cardColors"), new TypeToken<HashSet<DevCardColor>>() {
        }.getType());
    }

    /**
     * Returns the set of all the resource types.
     *
     * @return all the resource types
     */
    private Set<ResourceType> generateResourceTypes() {
        Gson customGson = new GsonBuilder().create();
        Set<JsonObject> protoResources = customGson.fromJson(parserObject.get("resourceTypes"), new TypeToken<HashSet<JsonObject>>() {
        }.getType());
        Set<ResourceType> resources = new HashSet<>();
        for (JsonObject o : protoResources) {
            JsonElement type = o.get("type");
            try {
                resources.add(type == null ? customGson.fromJson(o, ResourceType.class) : customGson.fromJson(o, Class.forName("it.polimi.ingsw.server.model.resourcetypes." + type.getAsString()).asSubclass(ResourceType.class)));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return resources;
    }

    /**
     * Returns a list of all possible leader cards.
     *
     * @return list of leader cards
     */
    private List<LeaderCard> generateLeaderCards(List<ResourceTransactionRecipe> productions) {
        Gson customGson = new GsonBuilder()
                .enableComplexMapKeySerialization().setPrettyPrinting()
                .registerTypeAdapter(ResourceTransactionRecipe.class, new ProductionDeserializer(productions))
                .registerTypeAdapter(ResourceType.class, new ResourceDeserializer())
                .registerTypeAdapter(DevCardColor.class, new ColorDeserializer())
                .registerTypeAdapter(CardRequirement.class, new RequirementDeserializer())
                .create();
        List<LeaderCard> leaders = new ArrayList<>();
        List<JsonObject> prototypes = customGson.fromJson(parserObject.get("leaderCards"), new TypeToken<ArrayList<JsonObject>>() {
        }.getType());
        for (JsonObject o : prototypes) {
            try {
                leaders.add(customGson.fromJson(o, Class.forName("it.polimi.ingsw.server.model.leadercards." + o.get("type").getAsString()).asSubclass(LeaderCard.class)));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

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
    private List<DevelopmentCard> generateDevCards() {
        return gson.fromJson(parserObject.get("developmentCards"), new TypeToken<ArrayList<DevelopmentCard>>() {
        }.getType());
    }

    /**
     * Returns a new Market instance.
     *
     * @return the Market
     */
    private Market generateMarket() {
        JsonElement entries = parserObject.get("marketResources");
        Map<ResourceType, Integer> marketResources = gson.fromJson(entries, new TypeToken<HashMap<ResourceType, Integer>>(){}.getType());
        return new Market(marketResources, marketColumns, resTypeMap.get(parserObject.get("marketReplaceableResource").getAsString()));
    }

    /**
     * Generates the Faith Track.
     *
     * @return new faith track
     */
    private FaithTrack generateFaithTrack() {
        JsonObject track = parserObject.get("faithTrack").getAsJsonObject();
        Set<FaithTrack.YellowTile> tiles = gson.fromJson(track.get("yellowTiles"), new TypeToken<HashSet<FaithTrack.YellowTile>>(){}.getType());
        Set<FaithTrack.VaticanSection> sections = gson.fromJson(track.get("vaticanSections"), new TypeToken<HashSet<FaithTrack.VaticanSection>>(){}.getType());
        return new FaithTrack(sections, tiles);
    }

    /**
     * Returns a list of the action tokens.
     *
     * @return list of action tokens
     */
    private List<ActionToken> generateActionTokens() {
        JsonArray jsonArray = parserObject.get("actionTokens").getAsJsonArray();
        List<ActionToken> tokens = new ArrayList<>();
        List<JsonObject> prototypes = gson.fromJson(jsonArray, new TypeToken<ArrayList<JsonObject>>() {
        }.getType());
        for (JsonObject o : prototypes) {
            try {
                tokens.add(gson.fromJson(o, Class.forName("it.polimi.ingsw.server.model.actiontokens." + o.get("type").getAsString()).asSubclass(ActionToken.class)));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return tokens;
    }

    /** Private class representing the early game boost in resources. */
    private static class Boost {
        /** Number of choosable resources obtained at the beginning. */
        private int initialResources;

        /** Starting faith points. */
        private int initialFaith;

        /** Resources that cannot be chosen. */
        private Set<ResourceType> initialExcludedResources;
    }

    /** Custom deserializer for leader card requirements. */
    private class RequirementDeserializer implements JsonDeserializer<CardRequirement> {

        @Override
        public CardRequirement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Gson customGson = new GsonBuilder()
                    .enableComplexMapKeySerialization().setPrettyPrinting()
                    .registerTypeAdapter(ResourceType.class, new ResourceDeserializer())
                    .registerTypeAdapter(DevCardColor.class, new ColorDeserializer())
                    .create();
            try {
                return customGson.fromJson(jsonElement, Class.forName("it.polimi.ingsw.server.model.cardrequirements." + jsonElement.getAsJsonObject().get("type").getAsString()).asSubclass(CardRequirement.class));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class ColorDeserializer implements JsonDeserializer<DevCardColor> {
        @Override
        public DevCardColor deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return getDevCardColor(jsonElement.getAsString())
                    .orElseThrow(() -> new RuntimeException("The given color does not exist."));
        }
    }

    private class ResourceDeserializer implements JsonDeserializer<ResourceType> {
        @Override
        public ResourceType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return getResourceType(jsonElement.getAsString())
                    .orElseThrow(() -> new RuntimeException("The given resource type does not exist."));
        }
    }

    private class ProductionDeserializer implements JsonDeserializer<ResourceTransactionRecipe> {
        private final List<ResourceTransactionRecipe> productions;

        public ProductionDeserializer(List<ResourceTransactionRecipe> productions) {
            this.productions = productions;
        }

        @Override
        public ResourceTransactionRecipe deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            jsonElement.getAsJsonObject().addProperty("id", ResourceTransactionRecipe.generateId());
            // jsonElement.getAsJsonObject().addProperty("discardableOutput", false);
            Gson customGson = new GsonBuilder()
                    .enableComplexMapKeySerialization().setPrettyPrinting()
                    .registerTypeAdapter(ResourceType.class, new ResourceDeserializer())
                    .create();
            ResourceTransactionRecipe res = customGson.fromJson(jsonElement, ResourceTransactionRecipe.class);
            if (productions != null)
                productions.add(res);
            return res;
        }
    }
}
