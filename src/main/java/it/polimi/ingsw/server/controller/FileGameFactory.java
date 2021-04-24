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

    /** The development card colors. */
    private final Map<String, DevCardColor> devCardColorMap;

    /** The resource types. */
    private final Map<String, ResourceType> resTypeMap;

    /** Maximum number of players for each Game. */
    private final int maxPlayers;

    /** Initial number of leader cards per player. */
    private final int playerLeadersCount;

    /** Number of leader cards per player that must be discarded in the early game. */
    private final int numDiscardedLeaders;

    /** The maximum amount of faith points a player can have. */
    private final int maxFaith;

    /** The number of development cards a player can have before triggering the end of a game. */
    private final int maxDevCards;

    /** The maximum level a development card can have. */
    private final int maxLevel;

    /** The number of different card colors. */
    private final int numCardColors;

    /** The number of columns of the market grid. */
    private final int marketColumns;

    /** Maximum size of a Warehouse shelf. */
    private final int maxShelfSize;

    /** Number of development card production slots per player. */
    private final int slotsCount;

    /**
     * Instantiates a new Game factory that is able to build Game instances based on parameters parsed from a config
     * file.
     *
     * @param inputStream the input stream to be parsed
     */
    public FileGameFactory(InputStream inputStream) {
        gson = new GsonBuilder()
                .enableComplexMapKeySerialization().setPrettyPrinting()
                .create();

        parserObject = (JsonObject) new JsonParser().parse(new InputStreamReader(inputStream));

        //Parses all simple parameters
        maxPlayers = gson.fromJson(parserObject.get("max-players"), int.class);
        playerLeadersCount = gson.fromJson(parserObject.get("num-leader-cards"), int.class);
        numDiscardedLeaders = gson.fromJson(parserObject.get("num-discarded-leader-cars"), int.class);
        maxFaith = gson.fromJson(parserObject.get("max-faith"), int.class);
        maxDevCards = gson.fromJson(parserObject.get("max-development-cards"), int.class);
        maxLevel = gson.fromJson(parserObject.get("max-level"), int.class);
        numCardColors = gson.fromJson(parserObject.get("num-colors"), int.class);
        marketColumns = gson.fromJson(parserObject.get("market-columns"), int.class);
        maxShelfSize = gson.fromJson(parserObject.get("max-shelf-size"), int.class);
        slotsCount = gson.fromJson(parserObject.get("slots-count"), int.class);

        devCardColorMap = generateDevCardColors().stream()
                .collect(Collectors.toUnmodifiableMap(DevCardColor::getName, Function.identity()));


        resTypeMap = generateResourceTypes().stream()
                .collect(Collectors.toUnmodifiableMap(ResourceType::getName, Function.identity()));
    }

    @Override
    public Game getMultiGame(List<String> nicknames) {

        if (nicknames == null || nicknames.size() > maxPlayers || nicknames.size() == 0)
            throw new IllegalArgumentException();

        List<LeaderCard> leaderCards;

        /* Shuffle the leader cards */
        leaderCards = generateLeaderCards();
        Collections.shuffle(leaderCards);
        if (playerLeadersCount > 0 && leaderCards.size() % playerLeadersCount != 0)
            throw new IllegalArgumentException();

        /* Shuffle the nicknames */
        List<String> shuffledNicknames = new ArrayList<>(nicknames);
        Collections.shuffle(shuffledNicknames);
        if (playerLeadersCount > 0 && shuffledNicknames.size() > leaderCards.size() / playerLeadersCount)
            throw new IllegalArgumentException();

        Map<Integer, Boost> boost = gson.fromJson(parserObject.get("initial-resources"), new TypeToken<HashMap<Integer, Boost>>(){}.getType());

        List<Player> players = new ArrayList<>();
        Production production = gson.fromJson(parserObject.get("base-production"), Production.class);
        for (int i = 0; i < shuffledNicknames.size(); i++) {
            Player player = new Player(
                    shuffledNicknames.get(i),
                    i == 0,
                    leaderCards.subList(playerLeadersCount * i, playerLeadersCount * (i + 1)),
                    new Warehouse(maxShelfSize),
                    new Strongbox(),
                    production, slotsCount,
                    boost.get(i+1).numStorable,
                    boost.get(i+1).faith
            );
            players.add(player);
        }

        return new Game(
                players,
                new DevCardGrid(generateDevCards(), maxLevel, numCardColors),
                generateMarket(),
                generateFaithTrack(),
                maxFaith,
                maxDevCards
        );

    }

    @Override
    public SoloGame getSoloGame(String nickname) {

        if (nickname == null) throw new IllegalArgumentException();
        List<LeaderCard> shuffledLeaderCards;
        shuffledLeaderCards = new ArrayList<>(generateLeaderCards());
        Collections.shuffle(shuffledLeaderCards);

        Map<Integer, Boost> boost = gson.fromJson(parserObject.get("initial-resources"), new TypeToken<HashMap<Integer, Boost>>(){}.getType());

        Player player = new Player(
                nickname,
                true,
                shuffledLeaderCards.subList(0, playerLeadersCount),
                new Warehouse(maxShelfSize),
                new Strongbox(),
                gson.fromJson(parserObject.get("base-production"), Production.class), slotsCount,
                boost.get(1).numStorable,
                boost.get(1).faith
        );

        return new SoloGame(
                player,
                new DevCardGrid(generateDevCards(), maxLevel, numCardColors),
                generateMarket(),
                generateFaithTrack(),
                generateActionTokens(),
                maxFaith,
                maxDevCards
        );
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
     * Getter of a development card color in the game by its name.
     *
     * @param name the name of the development card color
     * @return the development card color
     */
    public Optional<DevCardColor> getDevCardColor(String name) {
        return Optional.ofNullable(devCardColorMap.get(name));
    }

    /**
     * Returns a list of all possible development cards.
     *
     * @return list of development cards
     */
    private List<DevelopmentCard> generateDevCards() {
        return gson.fromJson(parserObject.get("development-cards"), new TypeToken<ArrayList<DevelopmentCard>>(){}.getType());
    }

    /**
     * Returns a list of all possible leader cards.
     *
     * @return list of leader cards
     */
    private List<LeaderCard> generateLeaderCards() {
        Gson otherGson = new GsonBuilder()
                .enableComplexMapKeySerialization().setPrettyPrinting()
                .registerTypeHierarchyAdapter(CardRequirement.class, new RequirementDeserializer())
                .create();
        List<LeaderCard> leaders = new ArrayList<>();
        List<JsonObject> prototypes = otherGson.fromJson(parserObject.get("leader-cards"), new TypeToken<ArrayList<JsonObject>>() {
        }.getType());
        for (JsonObject o : prototypes) {
            try {
                leaders.add(otherGson.fromJson(o, (Class<? extends LeaderCard>) Class.forName(o.get("type").getAsString())));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException();
            }
        }
        return leaders;
    }

    /**
     * Returns a new Market instance.
     *
     * @return the Market
     */
    private Market generateMarket() {
        return gson.fromJson(parserObject.get("market"), Market.class);
    }

    /**
     * Generates the Faith Track.
     *
     * @return new faith track
     */
    private FaithTrack generateFaithTrack() {
        JsonObject track = parserObject.get("faith-track").getAsJsonObject();
        Set<FaithTrack.YellowTile> tiles = gson.fromJson(track.get("yellow-tiles"), new TypeToken<HashSet<FaithTrack.YellowTile>>(){}.getType());
        Set<FaithTrack.VaticanSection> sections = gson.fromJson(track.get("vatican-sections"), new TypeToken<HashSet<FaithTrack.VaticanSection>>(){}.getType());
        return new FaithTrack(sections, tiles);
    }

    /**
     * Returns a list of the action tokens.
     *
     * @return list of action tokens
     */
    private List<ActionToken> generateActionTokens() {
        JsonArray jsonArray = parserObject.get("action-tokens").getAsJsonArray();
        List<ActionToken> tokens = new ArrayList<>();
        List<JsonObject> prototypes = gson.fromJson(jsonArray, new TypeToken<ArrayList<JsonObject>>() {
        }.getType());
        for (JsonObject o : prototypes) {
            try {
                tokens.add(gson.fromJson(o, (Class<? extends ActionToken>) Class.forName(o.get("type").getAsString())));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException();
            }
        }
        return tokens;
    }

    /**
     * Returns the set of all the card colors.
     *
     * @return all the cars colors
     */
    private Set<DevCardColor> generateDevCardColors() {
        return gson.fromJson(parserObject.get("card-colors"), new TypeToken<HashSet<DevCardColor>>(){}.getType());
    }

    /**
     * Returns the set of all the resource types.
     *
     * @return all the resource types
     */
    private Set<ResourceType> generateResourceTypes() {
        Set<JsonObject> protoResources = gson.fromJson(parserObject.get("resource-types"), new TypeToken<HashSet<JsonObject>>(){}.getType());
        Set<ResourceType> resources = new HashSet<>();
        for(JsonObject o : protoResources){
            JsonElement subtype = o.get("subtype");
            try {
                resources.add(subtype == null ? gson.fromJson(o, ResourceType.class) : gson.fromJson(o, (Class<? extends ResourceType>) Class.forName(subtype.getAsString())));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return resources;
    }

    /** Private class representing the early game boost in resources. */
    private static class Boost {
        /** Number of choosable resources obtained at the beginning. */
        private int numStorable;

        /** Starting faith points. */
        private int faith;
    }

    /** Custom deserializer for leader card requirements. */
    private static class RequirementDeserializer implements JsonDeserializer<CardRequirement> {

        @Override
        public CardRequirement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            try {
                return new Gson().fromJson(jsonElement, (Class<? extends CardRequirement>) Class.forName(jsonElement.getAsJsonObject().get("type").getAsString()));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException();
            }
        }
    }
}
