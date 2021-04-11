package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.actiontokens.ActionToken;
import it.polimi.ingsw.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.model.cardrequirements.DevCardRequirement;
import it.polimi.ingsw.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.model.devcardcolors.DevCardColor;
import it.polimi.ingsw.model.leadercards.DepotLeader;
import it.polimi.ingsw.model.leadercards.LeaderCard;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.model.resourcetypes.ResourceType;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


public class FileGameFactory implements GameFactory {
    String configPath;
    ModelConfig config;
    JavaDevCardColorFactory colorFactory;
    JavaResourceTypeFactory resTypeFactory;

    public FileGameFactory(String path) throws JAXBException, FileNotFoundException {
        configPath = path;
        config = unmarshall(path);
        colorFactory = new JavaDevCardColorFactory();
        resTypeFactory = new JavaResourceTypeFactory();
    }

    public ModelConfig unmarshall(String path) throws JAXBException, FileNotFoundException {
        JAXBContext context = JAXBContext.newInstance(ModelConfig.class);
        return (ModelConfig) context.createUnmarshaller()
                .unmarshal(new FileReader(path));
    }

    public Game buildMultiGame(List<String> nicknames) {
        int playerLeadersCount = config.getNumLeaders();
        if (nicknames.size() > config.getMaxPlayers())
            throw new RuntimeException();

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
            throw new RuntimeException();

        /* Shuffle the nicknames */
        List<String> shuffledNicknames = new ArrayList<>(nicknames);
        Collections.shuffle(shuffledNicknames);
        if (playerLeadersCount > 0 && shuffledNicknames.size() > leaderCards.size() / playerLeadersCount)
            throw new RuntimeException();

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < shuffledNicknames.size(); i++) {
            ModelConfig.XmlProduction production = config.getBaseProduction();
            Player player = new Player(
                    shuffledNicknames.get(i),
                    i == 0,
                    leaderCards.subList(playerLeadersCount * i, playerLeadersCount * (i+1)),
                    new Warehouse(config.getMaxShelfSize()),
                    new Strongbox(),
                    new Production(new HashMap<>(){{
                        for (ModelConfig.XmlResourceEntry entry : production.getInput())
                            put(resTypeFactory.get(entry.getResourceType()), entry.getAmount());
                    }}, production.getInputBlanks(), new HashMap<>(){{
                        for (ModelConfig.XmlResourceEntry entry : production.getOutput())
                            put(resTypeFactory.get(entry.getResourceType()), entry.getAmount());
                    }}, production.getOutputBlanks()), config.getSlotsCount()
            );
            players.add(player);
        }
        return new Game(players, new DevCardGrid(generateDevCards(), parseLevelsCount(), parseColorsCount()), generateMarket(), new FaithTrack(generateVaticanSections(), generateYellowTiles()), parseMaxFaith(), parseMaxDevCards());
    }

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
                new Production(new HashMap<>(){{
                    for (ModelConfig.XmlResourceEntry entry : production.getInput())
                        put(resTypeFactory.get(entry.getResourceType()), entry.getAmount());
                }}, production.getInputBlanks(), new HashMap<>(){{
                    for (ModelConfig.XmlResourceEntry entry : production.getOutput())
                        put(resTypeFactory.get(entry.getResourceType()), entry.getAmount());
                }}, production.getOutputBlanks()), config.getSlotsCount()
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

    public List<DevelopmentCard> generateDevCards(){
        List<ModelConfig.XmlDevCard> source = config.getDevCards();
        final ResourceRequirement[] cost = new ResourceRequirement[1];
        final Production[] production = new Production[1];

        List<DevelopmentCard> devCards = new ArrayList<>(){{
            for(ModelConfig.XmlDevCard card : source) {
                cost[0] = new ResourceRequirement(new HashMap<>() {{
                    for (ModelConfig.XmlResourceEntry entry : card.getCost())
                        put(resTypeFactory.get(entry.getResourceType()), entry.getAmount());
                }});

                production[0] = new Production(new HashMap<>(){{
                    for (ModelConfig.XmlResourceEntry entry : card.getProduction().getInput())
                        put(resTypeFactory.get(entry.getResourceType()), entry.getAmount());
                }}, card.getProduction().getInputBlanks(), new HashMap<>(){{
                    for (ModelConfig.XmlResourceEntry entry : card.getProduction().getOutput())
                        put(resTypeFactory.get(entry.getResourceType()), entry.getAmount());
                }}, card.getProduction().getOutputBlanks());

                add(new DevelopmentCard(colorFactory.get(card.getColor()), card.getLevel(), cost[0], production[0], card.getVictoryPoints()));
            }
        }};
        return devCards;
    }

    public List<LeaderCard> generateLeaderCards() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
//        List<ModelConfig.XmlDepotLeaderCard> sourceDepot = config.getDepotLeaderCards();
//        List<ModelConfig.XmlDiscountLeaderCard> sourceDiscount = config.getDiscountLeaderCards();
//        List<ModelConfig.XmlZeroLeaderCard> sourceZero = config.getZeroLeaderCards();
//        List<ModelConfig.XmlProductionLeaderCard> sourceProduction = config.getProductionLeaderCards();
        List<ModelConfig.XmlLeaderCard> source = config.getLeaderCards();
        CardRequirement[] cost = new CardRequirement[1];
        Class<?> className;
        Constructor<?> constructor;
        List<LeaderCard> leaderCards = new ArrayList<>();
        for(ModelConfig.XmlLeaderCard card : source) {
            className = Class.forName(card.getType());
            constructor = className.getConstructor(Integer.class, Integer.class, Production.class, ResourceType.class, CardRequirement.class, Integer.class);
            if(card.getColorRequirements()==null){
                cost[0] = new ResourceRequirement(new HashMap<>() {{
                    for (ModelConfig.XmlResourceEntry entry : card.getResourceRequirements())
                        put(resTypeFactory.get(entry.getResourceType()), entry.getAmount());
                }});
            }
            else{
                cost[0] = new DevCardRequirement(new HashSet<>() {{
                    for (ModelConfig.XmlCardRequirement req : card.getColorRequirements())
                        add(new DevCardRequirement.Entry(colorFactory.get(req.getColor()), req.getLevel(),req.getAmount()));
                }});
            }
            leaderCards.add((LeaderCard) constructor.newInstance(card.getDiscount(), card.getShelfSize(), card.getProduction(), card.getResource()));
        }
        return leaderCards;
    }

    public Market generateMarket(){
        return new Market(new HashMap<>(){{
            for(ModelConfig.XmlResourceEntry entry : config.getMarket())
                put(resTypeFactory.get(entry.getResourceType()), entry.getAmount());
        }}, parseColumnsCount(), resTypeFactory.get("zero"));
    }

    public Set<FaithTrack.VaticanSection> generateVaticanSections(){
        return new HashSet<>(){{
            for(ModelConfig.XmlFaithTrack.XmlVaticanSection section : config.getFaithTrack().getSections()){
                add(new FaithTrack.VaticanSection(section.getBeginning(), section.getEnd(), section.getPoints()));
            }
        }};
    }

    public Set<FaithTrack.YellowTile> generateYellowTiles(){
        return new HashSet<>(){{
            for(ModelConfig.XmlFaithTrack.XmlYellowTile tile : config.getFaithTrack().getTiles()){
                add(new FaithTrack.YellowTile(tile.getTileNumber(), tile.getPoints()));
            }
        }};
    }

    public List<ActionToken> generateActionTokens() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<ModelConfig.XmlActionToken> source = config.getTokens();
        List<ActionToken> tokens = new ArrayList<>();
        Class<?> className;
        Constructor<?> constructor;
        for(ModelConfig.XmlActionToken token : source) {
            className = Class.forName(token.getType());
            if(token.getColor()==null){
                constructor = className.getConstructor();
                tokens.add((ActionToken) constructor.newInstance());
            }
            else{
                constructor = className.getConstructor(DevCardColor.class);
                tokens.add((ActionToken) constructor.newInstance(colorFactory.get(token.getColor())));
            }
        }

        return tokens;
    }

    public int parseLevelsCount(){
        return config.getMaxLevel();
    }
    public int parseColorsCount(){
        return config.getNumColors();
    }
    public int parseColumnsCount(){
        return config.getMarketColumns();
    }
    public int parseMaxFaith(){
        return config.getMaxFaith();
    }
    public int parseMaxDevCards(){
        return config.getMaxDevCards();
    }
}
