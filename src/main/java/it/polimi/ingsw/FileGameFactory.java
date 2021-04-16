package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.actiontokens.ActionToken;
import it.polimi.ingsw.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.model.cardrequirements.DevCardRequirement;
import it.polimi.ingsw.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.model.leadercards.LeaderCard;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.model.resourcetypes.ResourceType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
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
        ModelConfig tempConfig;
        try {
            tempConfig = unmarshal(inputStream);
        } catch (JAXBException | FileNotFoundException e) {
            tempConfig = null;
            e.printStackTrace();
        }
        config = tempConfig;
        devCardColorMap = generateDevCardColors().stream()
                .collect(Collectors.toMap(DevCardColor::getName, Function.identity()));
        resTypeMap = generateResourceTypes().stream()
                .collect(Collectors.toMap(ResourceType::getName, Function.identity()));
    }

    /**
     * Unmarshals the XML in order to parse the game parameters.
     *
     * @param inputStream the input stream to be parsed
     * @return the object created by the XML unmarshalling
     * @throws JAXBException         generic exception from the JAXB library
     * @throws FileNotFoundException config file not found
     */
    private ModelConfig unmarshal(InputStream inputStream) throws JAXBException, FileNotFoundException {
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
            e.printStackTrace();
            return null;
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
            e.printStackTrace();
            return null;
        }
        Collections.shuffle(shuffledLeaderCards);

        ModelConfig.XmlProduction production = config.getBaseProduction();
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
    private List<DevelopmentCard> generateDevCards() {
        List<ModelConfig.XmlDevCard> source = config.getDevCards();
        final ResourceRequirement[] cost = new ResourceRequirement[1];
        final Production[] production = new Production[1];

        List<DevelopmentCard> devCards = new ArrayList<>() {{
            for (ModelConfig.XmlDevCard card : source) {
                cost[0] = generateResourceRequirement(card.getCost());

                production[0] = generateProduction(card.getProduction());

                add(new DevelopmentCard(getDevCardColor(card.getColor()), card.getLevel(), cost[0], production[0], card.getVictoryPoints()));
            }
        }};
        return devCards;
    }

    /**
     * Returns a list of all possible leader cards.
     *
     * @return list of leader cards
     */
    private List<LeaderCard> generateLeaderCards() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<ModelConfig.XmlLeaderCard> source = config.getLeaderCards();
        CardRequirement[] cost = new CardRequirement[1];
        Class<?> className;
        Constructor<?> constructor;
        Production[] production = new Production[1];
        List<LeaderCard> leaderCards = new ArrayList<>();
        for (ModelConfig.XmlLeaderCard card : source) {
            className = Class.forName(card.getType());
            constructor = className.getConstructor(int.class, int.class, Production.class, ResourceType.class, CardRequirement.class, int.class);
            if (card.getColorRequirements() == null) {
                cost[0] = generateResourceRequirement(card.getResourceRequirements());
            } else {
                cost[0] = new DevCardRequirement(new HashSet<>() {{
                    for (ModelConfig.XmlCardRequirement req : card.getColorRequirements())
                        add(new DevCardRequirement.Entry(getDevCardColor(req.getColor()), req.getLevel(), req.getAmount()));
                }});
            }
            if (card.getProduction() != null)
                production[0] = generateProduction(card.getProduction());

            leaderCards.add((LeaderCard) constructor.newInstance(card.getDiscount(), card.getShelfSize(), production[0], getResourceType(card.getResource()), cost[0], card.getVictoryPoints()));
        }
        return leaderCards;
    }

    /**
     * Returns a new Market instance.
     *
     * @return the Market
     */
    private Market generateMarket() {
        return new Market(new HashMap<>() {{
            for (ModelConfig.XmlResourceMapEntry entry : config.getMarket())
                put(getResourceType(entry.getResourceType()), entry.getAmount());
        }}, config.getMarketColumns(), getResourceType(config.getMarketReplaceableResType()));
    }

    /**
     * Returns a set of the vatican sections.
     *
     * @return set of the vatican sections
     */
    private Set<FaithTrack.VaticanSection> generateVaticanSections() {
        return new HashSet<>() {{
            for (ModelConfig.XmlFaithTrack.XmlVaticanSection section : config.getFaithTrack().getSections()) {
                add(new FaithTrack.VaticanSection(section.getBeginning(), section.getEnd(), section.getPoints()));
            }
        }};
    }

    /**
     * Returns a set of the yellow tiles.
     *
     * @return set of the yellow tiles
     */
    private Set<FaithTrack.YellowTile> generateYellowTiles() {
        return new HashSet<>() {{
            for (ModelConfig.XmlFaithTrack.XmlYellowTile tile : config.getFaithTrack().getTiles()) {
                add(new FaithTrack.YellowTile(tile.getTileNumber(), tile.getPoints()));
            }
        }};
    }

    /**
     * Returns a list of the action tokens.
     *
     * @return list of action tokens
     */
    private List<ActionToken> generateActionTokens() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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
                new HashMap<>() {{
                    if (production.getInput() != null)
                        for (ModelConfig.XmlResourceMapEntry entry : production.getInput())
                            put(getResourceType(entry.getResourceType()), entry.getAmount());
                }},
                production.getInputBlanks(),
                new HashSet<>() {{
                    if (production.getInputBlanksExclusions() != null)
                        for (String entry : production.getInputBlanksExclusions())
                            add(getResourceType(entry));
                }},
                new HashMap<>() {{
                    if (production.getOutput() != null)
                        for (ModelConfig.XmlResourceMapEntry entry : production.getOutput())
                            put(getResourceType(entry.getResourceType()), entry.getAmount());
                }},
                production.getOutputBlanks(),
                new HashSet<>() {{
                    if (production.getOutputBlanksExclusions() != null)
                        for (String entry : production.getOutputBlanksExclusions())
                            add(getResourceType(entry));
                }},
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
        return new ResourceRequirement(new HashMap<>() {{
            for (ModelConfig.XmlResourceMapEntry entry : requirements)
                put(getResourceType(entry.getResourceType()), entry.getAmount());
        }});
    }

    private Set<DevCardColor> generateDevCardColors() {
        return config.getCardColors().stream().map(s -> new DevCardColor(s)).collect(Collectors.toSet());
    }

    private Set<ResourceType> generateResourceTypes() {
        return config.getResourceTypes().stream().map(s -> new ResourceType(s.getName(), s.isStorable())).collect(Collectors.toSet());
    }
}
