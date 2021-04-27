package it.polimi.ingsw.server.controller;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.actiontokens.ActionToken;
import it.polimi.ingsw.server.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.server.model.resourcecontainers.Warehouse;
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

    /** The leader cards. */
    private final Map<Game, List<LeaderCard>> leaderCardLists;

    /** The development cards. */
    private final Map<Game, List<DevelopmentCard>> developmentCardLists;

    /**
     * Instantiates a new Game factory that is able to build Game instances based on parameters parsed from a config
     * file.
     *
     * @param inputStream the input stream to be parsed
     */
    public FileGameFactory(InputStream inputStream) {
        gson = new GsonBuilder()
                .enableComplexMapKeySerialization().setPrettyPrinting()
                .registerTypeHierarchyAdapter(ResourceType.class, new ResourceDeserializer())
                .registerTypeHierarchyAdapter(DevCardColor.class, new ColorDeserializer())
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

        leaderCardLists = new HashMap<>();

        developmentCardLists = new HashMap<>();
    }

    private void checkNumberOfPlayers(List<String> nicknames) throws IllegalArgumentException {
        String baseMsg = "Invalid number of players chosen";
        if (nicknames == null)
            throw new IllegalArgumentException(String.format("%s: %s", baseMsg, "null"));
        else if (nicknames.size() > maxPlayers)
            throw new IllegalArgumentException(
                String.format("%s %d: maximum allowed is %d", baseMsg, nicknames.size(), maxPlayers));
        else if (nicknames.size() == 0)
            throw new IllegalArgumentException(String.format("%s: 0", baseMsg));
    }

    @Override
    public Game getMultiGame(List<String> nicknames) {
        checkNumberOfPlayers(nicknames);

        List<LeaderCard> leaderCards = generateLeaderCards();
        List<DevelopmentCard> developmentCards = generateDevCards();

        // TODO remove and use integer floor division
        if (playerLeadersCount > 0 && leaderCards.size() % playerLeadersCount != 0)
            throw new IllegalArgumentException(
                String.format("Cannot subdivide cleanly %d leadercards between %d players",
                    leaderCards.size(), nicknames.size()));

        if (playerLeadersCount > 0 && nicknames.size() > leaderCards.size() / playerLeadersCount)
            throw new IllegalArgumentException(
                String.format("Cannot assign %d leader cards to %d players: not enough cards (%d available)",
                    playerLeadersCount, nicknames.size(), leaderCards.size()));

        /* Shuffle the leader cards */
        List<LeaderCard> shuffledLeaderCards = new ArrayList<>(leaderCards);
        Collections.shuffle(shuffledLeaderCards);

        /* Shuffle the nicknames */
        List<String> shuffledNicknames = new ArrayList<>(nicknames);
        Collections.shuffle(shuffledNicknames);

        List<Boost> initialResources = gson.fromJson(parserObject.get("initialResources"), new TypeToken<ArrayList<Boost>>() {
        }.getType());

        List<Player> players = new ArrayList<>();
        Production baseProduction = gson.fromJson(parserObject.get("baseProduction"), Production.class);
        for (int i = 0; i < shuffledNicknames.size(); i++)
            players.add(new Player(
                    shuffledNicknames.get(i),
                    i == 0,
                    shuffledLeaderCards.subList(playerLeadersCount * i, playerLeadersCount * (i + 1)),
                    new Warehouse(warehouseShelvesCount),
                    new Strongbox(),
                    baseProduction,
                    slotsCount,
                    chosenLeadersCount,
                    initialResources.get(i).numStorable,
                    initialResources.get(i).faith
            ));

        Game game = new Game(
                players,
                new DevCardGrid(developmentCards, levelsCount, colorsCount),
                generateMarket(),
                generateFaithTrack(),
                maxFaith,
                maxDevCards
        );

        leaderCardLists.put(game, leaderCards);
        developmentCardLists.put(game, developmentCards);

        return game;
    }

    @Override
    public SoloGame getSoloGame(String nickname) {
        if (nickname == null)
            throw new IllegalArgumentException("Nickname not specified");

        List<LeaderCard> leaderCards = generateLeaderCards();
        List<DevelopmentCard> developmentCards = generateDevCards();

        if (leaderCards.size() < playerLeadersCount)
            throw new IllegalArgumentException(
                String.format("Not enough leader cards available to be assigned to the player: %d available, %d needed.", 
                    leaderCards.size(), playerLeadersCount));

        /* Shuffle the leader cards */
        List<LeaderCard> shuffledLeaderCards = new ArrayList<>(leaderCards);
        Collections.shuffle(shuffledLeaderCards);

        List<Boost> initialResources = gson.fromJson(parserObject.get("initialResources"), new TypeToken<ArrayList<Boost>>() {
        }.getType());

        Player player = new Player(
                nickname,
                true,
                shuffledLeaderCards.subList(0, playerLeadersCount),
                new Warehouse(warehouseShelvesCount),
                new Strongbox(),
                gson.fromJson(parserObject.get("baseProduction"), Production.class),
                slotsCount,
                chosenLeadersCount, initialResources.get(0).numStorable,
                initialResources.get(0).faith
        );

        SoloGame game = new SoloGame(
                player,
                new DevCardGrid(developmentCards, levelsCount, colorsCount),
                generateMarket(),
                generateFaithTrack(),
                generateActionTokens(),
                maxFaith,
                maxDevCards
        );

        leaderCardLists.put(game, leaderCards);
        developmentCardLists.put(game, developmentCards);

        return game;
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
     * Getter of leader cards used in a game.
     *
     * @param game the game
     * @return the leader card list
     */
    public Optional<List<LeaderCard>> getLeaderCards(Game game) {
        return Optional.ofNullable(leaderCardLists.get(game));
    }

    /**
     * Getter of development cards used in a game.
     *
     * @param game the game
     * @return the development card list
     */
    public Optional<List<DevelopmentCard>> getDevelopmentCards(Game game) {
        return Optional.ofNullable(developmentCardLists.get(game));
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
            JsonElement subtype = o.get("subtype");
            try {
                resources.add(subtype == null ? customGson.fromJson(o, ResourceType.class) : customGson.fromJson(o, (Class<? extends ResourceType>) Class.forName(subtype.getAsString())));
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
    private List<LeaderCard> generateLeaderCards() {
        Gson customGson = new GsonBuilder()
                .enableComplexMapKeySerialization().setPrettyPrinting()
                .registerTypeHierarchyAdapter(ResourceType.class, new ResourceDeserializer())
                .registerTypeHierarchyAdapter(DevCardColor.class, new ColorDeserializer())
                .registerTypeHierarchyAdapter(CardRequirement.class, new RequirementDeserializer())
                .create();
        List<LeaderCard> leaders = new ArrayList<>();
        List<JsonObject> prototypes = customGson.fromJson(parserObject.get("leaderCards"), new TypeToken<ArrayList<JsonObject>>() {
        }.getType());
        for (JsonObject o : prototypes) {
            try {
                leaders.add(customGson.fromJson(o, (Class<? extends LeaderCard>) Class.forName(o.get("type").getAsString())));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
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
                tokens.add(gson.fromJson(o, (Class<? extends ActionToken>) Class.forName(o.get("type").getAsString())));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return tokens;
    }

    /** Private class representing the early game boost in resources. */
    private static class Boost {
        /** Number of choosable resources obtained at the beginning. */
        private int numStorable;

        /** Starting faith points. */
        private int faith;
    }

    /** Custom deserializer for leader card requirements. */
    private class RequirementDeserializer implements JsonDeserializer<CardRequirement> {

        @Override
        public CardRequirement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Gson customGson = new GsonBuilder()
                    .enableComplexMapKeySerialization().setPrettyPrinting()
                    .registerTypeHierarchyAdapter(ResourceType.class, new ResourceDeserializer())
                    .registerTypeHierarchyAdapter(DevCardColor.class, new ColorDeserializer())
                    .create();
            try {
                return customGson.fromJson(jsonElement, (Class<? extends CardRequirement>) Class.forName(jsonElement.getAsJsonObject().get("type").getAsString()));
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
}