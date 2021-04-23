package it.polimi.ingsw.server.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.actiontokens.ActionToken;
import it.polimi.ingsw.server.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.server.model.cardrequirements.DevCardRequirement;
import it.polimi.ingsw.server.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.server.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Factory class that builds game instances from file parameters, by acting like an adapter for parsing. */
public class FileGameFactory implements GameFactory {
    /** The unmarshalled object. */
    private final ModelConfig config;

    /** The development card colors. */
    private final Map<String, DevCardColor> devCardColorMap;

    /** The resource types. */
    private final Map<String, ResourceType> resTypeMap;

    /**
     * Instantiates a new Game factory that is able to build Game instances based on parameters parsed from a config
     * file.
     *
     * @param inputStream the input stream to be parsed
     */
    public FileGameFactory(InputStream inputStream) {
//
//        GsonBuilder builder = new GsonBuilder().enableComplexMapKeySerialization();
//        builder.setPrettyPrinting();
//        Gson gson = builder.create();
//        String jsonString = gson.toJson(generateDevCards().get(0));
//        System.out.println(jsonString);
//
//        DevelopmentCard objectMarshal = gson.fromJson(jsonString, DevelopmentCard.class);

        ModelConfig tempConfig;
        try {
            tempConfig = unmarshal(inputStream);
        } catch (JAXBException e) {
            tempConfig = null;
            e.printStackTrace();
        }
        config = tempConfig;
        devCardColorMap = generateDevCardColors().stream()
                .collect(Collectors.toUnmodifiableMap(DevCardColor::getName, Function.identity()));
        resTypeMap = generateResourceTypes().stream()
                .collect(Collectors.toUnmodifiableMap(ResourceType::getName, Function.identity()));
    }

    /**
     * Unmarshals the XML in order to parse the game parameters.
     *
     * @param inputStream the input stream to be parsed
     * @return the object created by the XML unmarshalling
     * @throws JAXBException         generic exception from the JAXB library
     */
    private ModelConfig unmarshal(InputStream inputStream) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ModelConfig.class);
        return (ModelConfig) context.createUnmarshaller().unmarshal(inputStream);
    }

    @Override
    public Game getMultiGame(List<String> nicknames) {
        int playerLeadersCount = config.getNumLeaders();
        if (nicknames == null || nicknames.size() > config.getMaxPlayers() || nicknames.size() == 0)
            throw new IllegalArgumentException();

        List<LeaderCard> leaderCards;

        /* Shuffle the leader cards */
        try {
            leaderCards = generateLeaderCards();
        } catch (Exception e) {
            throw new RuntimeException();
        }
        Collections.shuffle(leaderCards);
        if (playerLeadersCount > 0 && leaderCards.size() % playerLeadersCount != 0)
            throw new IllegalArgumentException();

        /* Shuffle the nicknames */
        List<String> shuffledNicknames = new ArrayList<>(nicknames);
        Collections.shuffle(shuffledNicknames);
        if (playerLeadersCount > 0 && shuffledNicknames.size() > leaderCards.size() / playerLeadersCount)
            throw new IllegalArgumentException();

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < shuffledNicknames.size(); i++) {
            ModelConfig.XmlProduction production = config.getBaseProduction();
            Player player = new Player(
                    shuffledNicknames.get(i),
                    i == 0,
                    leaderCards.subList(playerLeadersCount * i, playerLeadersCount * (i + 1)),
                    new Warehouse(config.getMaxShelfSize()),
                    new Strongbox(),
                    generateProduction(production), config.getSlotsCount(),
                    config.getInitialResources().get(i).getNumResources(),
                    config.getInitialResources().get(i).getFaith()
            );
            players.add(player);
        }
        return new Game(
                players,
                new DevCardGrid(generateDevCards(), config.getMaxLevel(), config.getNumColors()),
                generateMarket(),
                new FaithTrack(generateVaticanSections(), generateYellowTiles()),
                config.getMaxFaith(),
                config.getMaxDevCards()
        );
    }

    @Override
    public SoloGame getSoloGame(String nickname) {
        if (nickname == null) throw new IllegalArgumentException();
        int playerLeadersCount = config.getNumLeaders();
        List<LeaderCard> shuffledLeaderCards;
        try {
            shuffledLeaderCards = new ArrayList<>(generateLeaderCards());
        } catch (Exception e) {
            throw new RuntimeException();
        }
        Collections.shuffle(shuffledLeaderCards);

        Player player = new Player(
                nickname,
                true,
                shuffledLeaderCards.subList(0, playerLeadersCount),
                new Warehouse(config.getMaxShelfSize()),
                new Strongbox(),
                generateProduction(config.getBaseProduction()), config.getSlotsCount(),
                config.getInitialResources().get(0).getNumResources(),
                config.getInitialResources().get(0).getFaith()
        );

        SoloGame game = null;
        try {
            game = new SoloGame(
                    player,
                    new DevCardGrid(generateDevCards(), config.getMaxLevel(), config.getNumColors()),
                    generateMarket(),
                    new FaithTrack(generateVaticanSections(), generateYellowTiles()),
                    generateActionTokens(),
                    config.getMaxFaith(),
                    config.getMaxDevCards()
            );
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return game;
    }

    /**
     * Getter of a resource type in the game by its name.
     *
     * @param name the name of the resource type
     * @return the resource type
     */
    public ResourceType getResourceType(String name) {
        return resTypeMap.get(name);
    }

    /**
     * Getter of a development card color in the game by its name.
     *
     * @param name the name of the development card color
     * @return the development card color
     */
    public DevCardColor getDevCardColor(String name) {
        return devCardColorMap.get(name);
    }

    /**
     * Returns a list of all possible development cards.
     *
     * @return list of development cards
     */
    public List<DevelopmentCard> generateDevCards() {
        List<ModelConfig.XmlDevCard> source = config.getDevCards();
        ResourceRequirement cost;
        Production production;

        List<DevelopmentCard> cards = new ArrayList<>();

        for (ModelConfig.XmlDevCard card : source) {
            cost = generateResourceRequirement(card.getCost());
            production = generateProduction(card.getProduction());

            cards.add(new DevelopmentCard(getDevCardColor(card.getColor()), card.getLevel(), cost, production, card.getVictoryPoints()));
        }

        return cards;
    }

    /**
     * Returns a list of all possible leader cards.
     *
     * @return list of leader cards
     */
    public List<LeaderCard> generateLeaderCards() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<ModelConfig.XmlLeaderCard> source = config.getLeaderCards();
        CardRequirement cost;
        Class<?> className;
        Constructor<?> constructor;
        Production production;
        List<LeaderCard> leaderCards = new ArrayList<>();

        for (ModelConfig.XmlLeaderCard card : source) {
            className = Class.forName(card.getType());
            constructor = className.getConstructor(int.class, int.class, Production.class, ResourceType.class, CardRequirement.class, int.class);

            if (card.getResourceRequirements() != null && card.getResourceRequirements().size() > 0) {
                cost = generateResourceRequirement(card.getResourceRequirements());
            } else if (card.getColorRequirements() != null && card.getColorRequirements().size() > 0) {
                cost = new DevCardRequirement(card.getColorRequirements()
                        .stream()
                        .map(req -> new DevCardRequirement.Entry(getDevCardColor(req.getColor()), req.getLevel(), req.getAmount()))
                        .collect(Collectors.toUnmodifiableSet())
                );
            }
            else cost = new ResourceRequirement(Map.of());

            production = card.getProduction() != null ? generateProduction(card.getProduction()) : null;

            leaderCards.add((LeaderCard) constructor.newInstance(card.getDiscount(), card.getShelfSize(), production, getResourceType(card.getResource()), cost, card.getVictoryPoints()));
        }
        return leaderCards;
    }

    /**
     * Returns a new Market instance.
     *
     * @return the Market
     */
    public Market generateMarket() {
        Map<ResourceType, Integer> resources = config.getMarket()
                .stream()
                .collect(Collectors.toUnmodifiableMap(s -> getResourceType(s.getResourceType()), ModelConfig.XmlResourceMapEntry::getAmount));

        return new Market(resources, config.getMarketColumns(), getResourceType(config.getMarketReplaceableResType()));
    }

    /**
     * Returns a set of the vatican sections.
     *
     * @return set of the vatican sections
     */
    public Set<FaithTrack.VaticanSection> generateVaticanSections() {
        return config.getFaithTrack().getSections()
                .stream()
                .map(section -> new FaithTrack.VaticanSection(section.getBeginning(), section.getEnd(), section.getPoints()))
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns a set of the yellow tiles.
     *
     * @return set of the yellow tiles
     */
    public Set<FaithTrack.YellowTile> generateYellowTiles() {
        return config.getFaithTrack().getTiles()
                .stream()
                .map(tile -> new FaithTrack.YellowTile(tile.getTileNumber(), tile.getPoints()))
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns a list of the action tokens.
     *
     * @return list of action tokens
     */
    public List<ActionToken> generateActionTokens() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<ModelConfig.XmlActionToken> source = config.getTokens();
        List<ActionToken> tokens = new ArrayList<>();
        Class<?> className;
        Constructor<?> constructor;
        for (ModelConfig.XmlActionToken token : source) {
            className = Class.forName(token.getType());
            if (token.getColor() == null) {
                constructor = className.getConstructor();
                tokens.add((ActionToken) constructor.newInstance());
            } else {
                constructor = className.getConstructor(DevCardColor.class);
                tokens.add((ActionToken) constructor.newInstance(getDevCardColor(token.getColor())));
            }
        }
        return tokens;
    }

    /**
     * Helper method that generates a Production.
     *
     * @param production the production "recipe" parsed from file
     * @return a new Production
     */
    private Production generateProduction(ModelConfig.XmlProduction production) {
        return new Production(
                Optional.ofNullable(production.getInput())
                .orElseGet(Collections::emptyList)
                .stream()
                .collect(Collectors.toUnmodifiableMap(s -> getResourceType(s.getResourceType()), ModelConfig.XmlResourceMapEntry::getAmount)),

                production.getInputBlanks(),

                Optional.ofNullable(production.getInputBlanksExclusions())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(this::getResourceType)
                .collect(Collectors.toUnmodifiableSet()),

                Optional.ofNullable(production.getOutput())
                        .orElseGet(Collections::emptyList)
                        .stream()
                        .collect(Collectors.toUnmodifiableMap(s -> getResourceType(s.getResourceType()), ModelConfig.XmlResourceMapEntry::getAmount)),

                production.getOutputBlanks(),

                Optional.ofNullable(production.getOutputBlanksExclusions())
                        .orElseGet(Collections::emptyList)
                        .stream()
                        .map(this::getResourceType)
                        .collect(Collectors.toUnmodifiableSet()),

                production.hasDiscardableOutput()
        );
    }

    /**
     * Helper method that puts together the parsed resources entries in a new data structure.
     *
     * @param requirements the resources and relative amounts required, obtained from parsing the config file
     * @return a new data structure for resource requirements
     */
    private ResourceRequirement generateResourceRequirement(List<ModelConfig.XmlResourceMapEntry> requirements) {
        return new ResourceRequirement(requirements.stream()
                .collect(Collectors.toUnmodifiableMap(s -> getResourceType(s.getResourceType()), ModelConfig.XmlResourceMapEntry::getAmount)));
    }

    public Set<DevCardColor> generateDevCardColors() {
        return config.getCardColors().stream().map(DevCardColor::new).collect(Collectors.toSet());
    }

    public Set<ResourceType> generateResourceTypes() {
        return config.getResourceTypes().stream().map(s -> new ResourceType(s.getName(), s.isStorable())).collect(Collectors.toSet());
    }
}
