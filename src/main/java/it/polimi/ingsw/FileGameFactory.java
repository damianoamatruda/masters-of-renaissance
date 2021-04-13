package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.actiontokens.ActionToken;
import it.polimi.ingsw.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.model.cardrequirements.DevCardRequirement;
import it.polimi.ingsw.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.model.devcardcolors.DevCardColor;
import it.polimi.ingsw.model.leadercards.LeaderCard;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.model.resourcetypes.ResourceType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class FileGameFactory implements GameFactory {
    /** The unmarshalled object. */
    private ModelConfig config;

    /** The development card colors. */
    Map<String, DevCardColor> devCardColorMap;

    /** The resource types. */
    Map<String, ResourceType> resTypeMap;

    /**
     * Instantiates a new Game factory that is able to build Game instances based on parameters parsed from a config file.
     *
     * @param path  the config file to be parsed
     */
    public FileGameFactory(String path) {
        config = null;
        try {
            config = unmarshall(path);
        } catch (JAXBException | FileNotFoundException e) {
            e.printStackTrace();
        }
        devCardColorMap = generateDevCardColors().stream()
                .collect(Collectors.toMap(DevCardColor::getName, Function.identity()));
        resTypeMap = generateResourceTypes().stream()
                .collect(Collectors.toMap(ResourceType::getName, Function.identity()));
    }

    /**
     * Builder of a multiplayer game instance.
     *
     * @param nicknames the list of nicknames of players who joined
     * @return          the multiplayer game
     */
    public Game buildMultiGame(List<String> nicknames) {
        int playerLeadersCount = config.getNumLeaders();
        if (nicknames.size() > config.getMaxPlayers())
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
        return new Game(players, new DevCardGrid(generateDevCards(), parseLevelsCount(), parseColorsCount()), generateMarket(), new FaithTrack(generateVaticanSections(), generateYellowTiles()), parseMaxFaith(), parseMaxDevCards());
    }

    /**
     * Builder of a single-player game instance.
     *
     * @param nickname  the nickname of the only player
     * @return          the single-player game
     */
    public SoloGame buildSoloGame(String nickname) {
        int playerLeadersCount = config.getNumLeaders();
        List<LeaderCard> shuffledLeaderCards = null;
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
            game = new SoloGame(player, new DevCardGrid(generateDevCards(), parseLevelsCount(), parseColorsCount()), generateMarket(), new FaithTrack(generateVaticanSections(), generateYellowTiles()), generateActionTokens(), parseMaxFaith(), parseMaxDevCards());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return game;
    }

    /**
     * Unmarshalls the XML in order to parse the game parameters.
     *
     * @param path                      the config file to be parsed
     * @return                          the object created by the XML unmarshalling
     * @throws JAXBException            generic exception from the JAXB library
     * @throws FileNotFoundException    config file not found
     */
    public ModelConfig unmarshall(String path) throws JAXBException, FileNotFoundException {
        JAXBContext context = JAXBContext.newInstance(ModelConfig.class);
        return (ModelConfig) context.createUnmarshaller()
                .unmarshal(new FileReader(path));
    }

    public ModelConfig getModelConfig() {
        return config;
    }

    @Override
    public ResourceType getResType(String name) {
        return resTypeMap.get(name);
    }

    @Override
    public DevCardColor getDevCardColor(String name) {
        return devCardColorMap.get(name);
    }

    @Override
    public List<DevelopmentCard> generateDevCards() {
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

    @Override
    public List<LeaderCard> generateLeaderCards() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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

            leaderCards.add((LeaderCard) constructor.newInstance(card.getDiscount(), card.getShelfSize(), production[0], getResType(card.getResource()), cost[0], card.getVictoryPoints()));
        }
        return leaderCards;
    }

    @Override
    public Market generateMarket() {
        return new Market(new HashMap<>() {{
            for (ModelConfig.XmlResourceEntry entry : config.getMarket())
                put(getResType(entry.getResourceType()), entry.getAmount());
        }}, parseColumnsCount(), getResType("zero"));
    }

    @Override
    public Set<FaithTrack.VaticanSection> generateVaticanSections() {
        return new HashSet<>() {{
            for (ModelConfig.XmlFaithTrack.XmlVaticanSection section : config.getFaithTrack().getSections()) {
                add(new FaithTrack.VaticanSection(section.getBeginning(), section.getEnd(), section.getPoints()));
            }
        }};
    }

    @Override
    public Set<FaithTrack.YellowTile> generateYellowTiles() {
        return new HashSet<>() {{
            for (ModelConfig.XmlFaithTrack.XmlYellowTile tile : config.getFaithTrack().getTiles()) {
                add(new FaithTrack.YellowTile(tile.getTileNumber(), tile.getPoints()));
            }
        }};
    }

    @Override
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
     * Returns the maximum level a development card can have.
     *
     * @return  max card level
     */
    public int parseLevelsCount() {
        return config.getMaxLevel();
    }

    /**
     * Returns the number of different card colors.
     *
     * @return  number of card colors
     */
    public int parseColorsCount() {
        return config.getNumColors();
    }

    /**
     * Returns the number of columns of the market grid.
     *
     * @return  number of market columns
     */
    public int parseColumnsCount() {
        return config.getMarketColumns();
    }

    /**
     * Returns the maximum amount of faith points a player can have.
     *
     * @return  max number of faith points
     */
    public int parseMaxFaith() {
        return config.getMaxFaith();
    }

    /**
     * Returns the number of development cards a player can have before triggering the end of a game.
     *
     * @return  max number of development cards purchasable by a player
     */
    public int parseMaxDevCards() {
        return config.getMaxDevCards();
    }

    /**
     * Helper method that generates a Production.
     *
     * @param production    the production "recipe" parsed from file
     * @return              a new Production
     */
    private Production generateProduction(ModelConfig.XmlProduction production) {
        return new Production(new HashMap<>() {{
            if (production.getInput() != null)
                for (ModelConfig.XmlResourceEntry entry : production.getInput())
                    put(getResType(entry.getResourceType()), entry.getAmount());
        }}, production.getInputBlanks(), new HashMap<>() {{
            if (production.getOutput() != null)
                for (ModelConfig.XmlResourceEntry entry : production.getOutput())
                    put(getResType(entry.getResourceType()), entry.getAmount());
        }}, production.getOutputBlanks());
    }

    /**
     * Helper method that puts together the parsed resources entries in a new data structure.
     *
     * @param requirements  the resources and relative amounts required, obtained from parsing the config file
     * @return              a new data structure for resource requirements
     */
    private ResourceRequirement generateResourceRequirement(List<ModelConfig.XmlResourceEntry> requirements) {
        return new ResourceRequirement(new HashMap<>() {{
            for (ModelConfig.XmlResourceEntry entry : requirements)
                put(getResType(entry.getResourceType()), entry.getAmount());
        }});
    }

    public Set<DevCardColor> generateDevCardColors(){
        return config.getCardColors().stream().map(s -> new DevCardColor(s)).collect(Collectors.toSet());
    }

    public Set<ResourceType> generateResourceTypes(){
        return config.getResourceTypes().stream().map(s -> new ResourceType(s.getName(), s.isStorable())).collect(Collectors.toSet());
    }
}
