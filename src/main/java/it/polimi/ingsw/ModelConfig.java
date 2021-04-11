package it.polimi.ingsw;

import it.polimi.ingsw.model.DevelopmentCard;
import it.polimi.ingsw.model.FaithTrack;
import it.polimi.ingsw.model.Market;
import it.polimi.ingsw.model.actiontokens.ActionToken;
import it.polimi.ingsw.model.leadercards.LeaderCard;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "config")
//@XmlAccessorType(XmlAccessType.FIELD)
public class ModelConfig {
    private int maxPlayers;
    private int numOfLeaderCards;
    private int maxFaith;
    private int maxDevCards;
    private int maxLevel;
    private int numColors;
    private int marketColumns;
    private int numLeaders;
    private int maxShelfSize;
    private int slotsCount;
    private List<XmlDevCard> devCards;
//    private List<XmlDepotLeaderCard> depotLeaderCards;
//    private List<XmlDiscountLeaderCard> discountLeaderCards;
//    private List<XmlZeroLeaderCard> zeroLeaderCards;
//    private List<XmlProductionLeaderCard> productionLeaderCards;
    private List<XmlLeaderCard> leaderCards;
    private List<XmlResourceEntry> market;
    private XmlFaithTrack faithTrack;
    private List<XmlActionToken> tokens;
    private XmlProduction baseProduction;

    @XmlElement(name = "max-players")
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    @XmlElement(name = "num-leader-cards")
    public void setNumOfLeaderCards(int numOfLeaderCards) {
        this.numOfLeaderCards = numOfLeaderCards;
    }

    @XmlElement(name = "max-faith")
    public void setMaxFaith(int maxFaith) {
        this.maxFaith = maxFaith;
    }

    @XmlElement(name = "max-development-cards")
    public void setMaxDevCards(int maxDevCards) {
        this.maxDevCards = maxDevCards;
    }

    @XmlElement(name = "max-level")
    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    @XmlElement(name = "num-colors")
    public void setNumColors(int numColors) {
        this.numColors = numColors;
    }

    @XmlElementWrapper(name="development-cards")
    @XmlElement(name="Card")
    public void setDevCards(List<XmlDevCard> devCards){
//        this.devCards = new ArrayList<>(){{
//            for(XmlDevCard card : devCards)
//                add(new DevelopmentCard(new JavaDevCardColorFactory().get(card.getColor()), card.getLevel(), null, null, card.getVictoryPoints()));
//        }};
        this.devCards = devCards;
    }

    @XmlElement(name="market-columns")
    public void setMarketColumns(int marketColumns) {
        this.marketColumns = marketColumns;
    }

    public List<XmlResourceEntry> getMarket() {
        return market;
    }

    @XmlElementWrapper(name="market")
    @XmlElement(name="resource-entry")
    public void setMarket(List<XmlResourceEntry> market) {
        this.market = market;
    }

    public XmlFaithTrack getFaithTrack() {
        return faithTrack;
    }

    @XmlElement(name="Faith-Track")
    public void setFaithTrack(XmlFaithTrack faithTrack) {
        this.faithTrack = faithTrack;
    }

//    public List<XmlDepotLeaderCard> getDepotLeaderCards() {
//        return depotLeaderCards;
//    }
//
//    @XmlElementWrapper(name = "depot-leader-cards")
//    @XmlElement(name = "Card")
//    public void setDepotLeaderCards(List<XmlDepotLeaderCard> depotLeaderCards) {
//        this.depotLeaderCards = depotLeaderCards;
//    }
//
//    public List<XmlDiscountLeaderCard> getDiscountLeaderCards() {
//        return discountLeaderCards;
//    }
//
//    @XmlElementWrapper(name = "discount-leader-cards")
//    @XmlElement(name = "Card")
//    public void setDiscountLeaderCards(List<XmlDiscountLeaderCard> discountLeaderCards) {
//        this.discountLeaderCards = discountLeaderCards;
//    }
//
//    public List<XmlZeroLeaderCard> getZeroLeaderCards() {
//        return zeroLeaderCards;
//    }
//
//    @XmlElementWrapper(name = "zero-leader-cards")
//    @XmlElement(name = "Card")
//    public void setZeroLeaderCards(List<XmlZeroLeaderCard> zeroLeaderCards) {
//        this.zeroLeaderCards = zeroLeaderCards;
//    }
//
//    public List<XmlProductionLeaderCard> getProductionLeaderCards() {
//        return productionLeaderCards;
//    }
//
//    @XmlElementWrapper(name = "production-leader-cards")
//    @XmlElement(name = "Card")
//    public void setProductionLeaderCards(List<XmlProductionLeaderCard> productionLeaderCards) {
//        this.productionLeaderCards = productionLeaderCards;
//    }


    public List<XmlLeaderCard> getLeaderCards() {
        return leaderCards;
    }

    @XmlElementWrapper(name = "leader-cards")
    @XmlElement(name = "Card")
    public void setLeaderCards(List<XmlLeaderCard> leaderCards) {
        this.leaderCards = leaderCards;
    }

    public List<XmlActionToken> getTokens() {
        return tokens;
    }

    @XmlElementWrapper(name = "action-tokens")
    @XmlElement(name = "token")
    public void setTokens(List<XmlActionToken> tokens) {
        this.tokens = tokens;
    }


    public int getNumLeaders() {
        return numLeaders;
    }

    @XmlElement(name="num-leaders")
    public void setNumLeaders(int numLeaders) {
        this.numLeaders = numLeaders;
    }

    public int getMaxShelfSize() {
        return maxShelfSize;
    }

    @XmlElement(name = "max-shelf-size")
    public void setMaxShelfSize(int maxShelfSize) {
        this.maxShelfSize = maxShelfSize;
    }

    public XmlProduction getBaseProduction() {
        return baseProduction;
    }

    @XmlElement(name = "base-production")
    public void setBaseProduction(XmlProduction baseProduction) {
        this.baseProduction = baseProduction;
    }

    public int getSlotsCount() {
        return slotsCount;
    }

    @XmlElement(name = "slots-count")
    public void setSlotsCount(int slotsCount) {
        this.slotsCount = slotsCount;
    }

    public int getMarketColumns() {
        return marketColumns;
    }

    public List<XmlDevCard> getDevCards() {
        return devCards;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getNumOfLeaderCards() {
        return numOfLeaderCards;
    }

    public int getMaxFaith() {
        return maxFaith;
    }

    public int getMaxDevCards() {
        return maxDevCards;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getNumColors() {
        return numColors;
    }

    @XmlRootElement(name = "resource-entry")
    static class XmlResourceEntry{
        private String resourceType;
        private int amount;

        public String getResourceType() {
            return resourceType;
        }

        @XmlElement(name = "resource-type")
        public void setResourceType(String resourceType) {
            this.resourceType = resourceType;
        }

        public int getAmount() {
            return amount;
        }

        @XmlElement(name = "amount")
        public void setAmount(int amount) {
            this.amount = amount;
        }

    }

    @XmlRootElement(name = "Card")
    static class XmlDevCard{
        private String color;
        private int level;
        private List<XmlResourceEntry> cost;
        private XmlProduction production;
        private int victoryPoints;

        public String getColor() {
            return color;
        }

        @XmlElement(name = "color")
        public void setColor(String color) {
            this.color = color;
        }

        public int getLevel() {
            return level;
        }

        @XmlElement(name = "level")
        public void setLevel(int level) {
            this.level = level;
        }

        public int getVictoryPoints() {
            return victoryPoints;
        }

        @XmlElement(name = "victory-points")
        public void setVictoryPoints(int victoryPoints) {
            this.victoryPoints = victoryPoints;
        }

        public List<XmlResourceEntry> getCost() {
            return cost;
        }

        @XmlElementWrapper(name="cost")
        @XmlElement(name = "resource-entry")
        public void setCost(List<XmlResourceEntry> cost) {
            this.cost = cost;
        }

        public XmlProduction getProduction() {
            return production;
        }

        @XmlElement(name = "production")
        public void setProduction(XmlProduction production) {
            this.production = production;
        }

    }

    @XmlRootElement(name = "production")
    static class XmlProduction{
        private List<XmlResourceEntry> input;
        private List<XmlResourceEntry> output;
        private int inputBlanks;
        private int outputBlanks;

        public List<XmlResourceEntry> getInput() {
            return input;
        }

        @XmlElementWrapper(name="input", nillable = true)
        @XmlElement(name = "resource-entry")
        public void setInput(List<XmlResourceEntry> input) {
            this.input = input;
        }

        public List<XmlResourceEntry> getOutput() {
            return output;
        }

        @XmlElementWrapper(name="output", nillable = true)
        @XmlElement(name = "resource-entry")
        public void setOutput(List<XmlResourceEntry> output) {
            this.output = output;
        }

        public int getInputBlanks() {
            return inputBlanks;
        }

        @XmlElement(name = "input-blanks")
        public void setInputBlanks(int inputBlanks) {
            this.inputBlanks = inputBlanks;
        }

        public int getOutputBlanks() {
            return outputBlanks;
        }

        @XmlElement(name = "output-blanks")
        public void setOutputBlanks(int ouputBlanks) {
            this.outputBlanks = ouputBlanks;
        }

    }

    @XmlRootElement(name = "Faith-Track")
    static class XmlFaithTrack{
        private List<XmlVaticanSection> sections;
        private List<XmlYellowTile> tiles;

        public List<XmlVaticanSection> getSections() {
            return sections;
        }

        @XmlElementWrapper(name="vatican-sections")
        @XmlElement(name = "section")
        public void setSections(List<XmlVaticanSection> sections) {
            this.sections = sections;
        }

        public List<XmlYellowTile> getTiles() {
            return tiles;
        }

        @XmlElementWrapper(name="yellow-tiles")
        @XmlElement(name = "tile")
        public void setTiles(List<XmlYellowTile> tiles) {
            this.tiles = tiles;
        }

        @XmlRootElement(name = "section")
        static class XmlVaticanSection{
            private int beginning;
            private int end;
            private int points;

            public int getBeginning() {
                return beginning;
            }

            @XmlElement(name = "section-begin")
            public void setBeginning(int beginning) {
                this.beginning = beginning;
            }

            public int getEnd() {
                return end;
            }

            @XmlElement(name = "section-end")
            public void setEnd(int end) {
                this.end = end;
            }

            public int getPoints() {
                return points;
            }

            @XmlElement(name = "section-points")
            public void setPoints(int points) {
                this.points = points;
            }

        }
        @XmlRootElement(name = "tile")
        static class XmlYellowTile{
            private int tileNumber;
            private int points;

            public int getTileNumber() {
                return tileNumber;
            }

            @XmlElement(name = "number")
            public void setTileNumber(int tileNumber) {
                this.tileNumber = tileNumber;
            }

            public int getPoints() {
                return points;
            }

            @XmlElement(name = "points")
            public void setPoints(int points) {
                this.points = points;
            }

        }
    }

    @XmlRootElement(name = "card-requirement")
    static class XmlCardRequirement{
        private String color;
        private int level;
        private int amount;

        public String getColor() {
            return color;
        }

        @XmlElement(name = "card-color")
        public void setColor(String color) {
            this.color = color;
        }

        public int getLevel() {
            return level;
        }

        @XmlElement(name = "card-level")
        public void setLevel(int level) {
            this.level = level;
        }

        public int getAmount() {
            return amount;
        }

        @XmlElement(name = "amount")
        public void setAmount(int amount) {
            this.amount = amount;
        }

    }

    @XmlRootElement(name = "Card")
    static class XmlLeaderCard{
        private String type;
        private int discount;
        private int shelfSize;
        private String resource;
        private List<XmlResourceEntry> resourceRequirements;
        private List<XmlCardRequirement> ColorRequirements;
        private XmlProduction production;
        private int victoryPoints;

        public int getShelfSize() {
            return shelfSize;
        }

        public XmlProduction getProduction() {
            return production;
        }

        public String getType() {
            return type;
        }

        @XmlElement(name = "type")
        public void setType(String type) {
            this.type = type;
        }

        public String getResource() {
            return resource;
        }

        @XmlElement(name = "resource")
        public void setResource(String resource) {
            this.resource = resource;
        }

        public List<XmlResourceEntry> getResourceRequirements() {
            return resourceRequirements;
        }

        @XmlElementWrapper(name = "cost")
        @XmlElement(name = "resource-entry", nillable = true)
        public void setResourceRequirements(List<XmlResourceEntry> resourceRequirements) {
            this.resourceRequirements = resourceRequirements;
        }

        public List<XmlCardRequirement> getColorRequirements() {
            return ColorRequirements;
        }

        @XmlElementWrapper(name = "cost")
        @XmlElement(name = "card-requirement", nillable = true)
        public void setColorRequirements(List<XmlCardRequirement> colorRequirements) {
            ColorRequirements = colorRequirements;
        }

        public int getVictoryPoints() {
            return victoryPoints;
        }

        @XmlElement(name = "victory-points")
        public void setVictoryPoints(int victoryPoints) {
            this.victoryPoints = victoryPoints;
        }

        @XmlElement(name = "production", nillable = true)
        public void setProduction(XmlProduction production) {
            this.production = production;
        }

        @XmlElement(name = "shelf-size", nillable = true)
        public void setShelfSize(int shelfSize) {
            this.shelfSize = shelfSize;
        }

        public int getDiscount() {
            return discount;
        }

        @XmlElement(name = "discount", nillable = true)
        public void setDiscount(int discount) {
            this.discount = discount;
        }
    }
    @XmlRootElement(name = "zero-leader-cards")
    static class XmlZeroLeaderCard{
        private String type;
        private String resource;
        private List<XmlResourceEntry> resourceRequirements;
        private List<XmlCardRequirement> ColorRequirements;
        private int victoryPoints;

        public String getType() {
            return type;
        }

        @XmlElement(name = "type")
        public void setType(String type) {
            this.type = type;
        }

        public String getResource() {
            return resource;
        }

        @XmlElement(name = "resource")
        public void setResource(String resource) {
            this.resource = resource;
        }

        public List<XmlResourceEntry> getResourceRequirements() {
            return resourceRequirements;
        }

        @XmlElementWrapper(name = "cost")
        @XmlElement(name = "resource-entry")
        public void setResourceRequirements(List<XmlResourceEntry> resourceRequirements) {
            this.resourceRequirements = resourceRequirements;
        }

        public List<XmlCardRequirement> getColorRequirements() {
            return ColorRequirements;
        }

        @XmlElementWrapper(name = "cost")
        @XmlElement(name = "card-requirement")
        public void setColorRequirements(List<XmlCardRequirement> colorRequirements) {
            ColorRequirements = colorRequirements;
        }

        public int getVictoryPoints() {
            return victoryPoints;
        }

        @XmlElement(name = "victory-points")
        public void setVictoryPoints(int victoryPoints) {
            this.victoryPoints = victoryPoints;
        }


    }

    @XmlRootElement(name = "production-leader-cards")
    static class XmlProductionLeaderCard{
        private String type;
        private String resource;
        private List<XmlResourceEntry> resourceRequirements;
        private List<XmlCardRequirement> ColorRequirements;
        private XmlProduction production;
        private int victoryPoints;

        public String getType() {
            return type;
        }

        @XmlElement(name = "type")
        public void setType(String type) {
            this.type = type;
        }

        public String getResource() {
            return resource;
        }

        @XmlElement(name = "resource")
        public void setResource(String resource) {
            this.resource = resource;
        }

        public List<XmlResourceEntry> getResourceRequirements() {
            return resourceRequirements;
        }

        @XmlElementWrapper(name = "cost")
        @XmlElement(name = "resource-entry")
        public void setResourceRequirements(List<XmlResourceEntry> resourceRequirements) {
            this.resourceRequirements = resourceRequirements;
        }

        public List<XmlCardRequirement> getColorRequirements() {
            return ColorRequirements;
        }

        @XmlElementWrapper(name = "cost")
        @XmlElement(name = "card-requirement")
        public void setColorRequirements(List<XmlCardRequirement> colorRequirements) {
            ColorRequirements = colorRequirements;
        }

        public XmlProduction getProduction() {
            return production;
        }

        @XmlElement(name = "production")
        public void setProduction(XmlProduction production) {
            this.production = production;
        }

        public int getVictoryPoints() {
            return victoryPoints;
        }

        @XmlElement(name = "victory-points")
        public void setVictoryPoints(int victoryPoints) {
            this.victoryPoints = victoryPoints;
        }


    }

    @XmlRootElement(name = "depot-leader-cards")
    static class XmlDepotLeaderCard{
        private String type;
        private int shelfSize;
        private String resource;
        private List<XmlResourceEntry> resourceRequirements;
        private List<XmlCardRequirement> ColorRequirements;
        private int victoryPoints;

        public String getType() {
            return type;
        }

        @XmlElement(name = "type")
        public void setType(String type) {
            this.type = type;
        }

        public int getShelfSize() {
            return shelfSize;
        }

        @XmlElement(name = "shelf-size")
        public void setShelfSize(int shelfSize) {
            this.shelfSize = shelfSize;
        }

        public String getResource() {
            return resource;
        }

        @XmlElement(name = "resource")
        public void setResource(String resource) {
            this.resource = resource;
        }

        public List<XmlResourceEntry> getResourceRequirements() {
            return resourceRequirements;
        }

        @XmlElementWrapper(name = "cost")
        @XmlElement(name = "resource-entry")
        public void setResourceRequirements(List<XmlResourceEntry> resourceRequirements) {
            this.resourceRequirements = resourceRequirements;
        }

        public List<XmlCardRequirement> getColorRequirements() {
            return ColorRequirements;
        }

        @XmlElementWrapper(name = "cost")
        @XmlElement(name = "card-requirement")
        public void setColorRequirements(List<XmlCardRequirement> colorRequirements) {
            ColorRequirements = colorRequirements;
        }

        public int getVictoryPoints() {
            return victoryPoints;
        }

        @XmlElement(name = "victory-points")
        public void setVictoryPoints(int victoryPoints) {
            this.victoryPoints = victoryPoints;
        }


    }

    @XmlRootElement(name = "discount-leader-cards")
    static class XmlDiscountLeaderCard{
        private String type;
        private int discount;
        private String resource;
        private List<XmlResourceEntry> resourceRequirements;
        private List<XmlCardRequirement> ColorRequirements;
        private int victoryPoints;

        public String getType() {
            return type;
        }

        @XmlElement(name = "type")
        public void setType(String type) {
            this.type = type;
        }

        public int getDiscount() {
            return discount;
        }

        @XmlElement(name = "discount")
        public void setDiscount(int discount) {
            this.discount = discount;
        }

        public String getResource() {
            return resource;
        }

        @XmlElement(name = "resource")
        public void setResource(String resource) {
            this.resource = resource;
        }

        public List<XmlResourceEntry> getResourceRequirements() {
            return resourceRequirements;
        }

        @XmlElementWrapper(name = "cost")
        @XmlElement(name = "resource-entry")
        public void setResourceRequirements(List<XmlResourceEntry> resourceRequirements) {
            this.resourceRequirements = resourceRequirements;
        }

        public List<XmlCardRequirement> getColorRequirements() {
            return ColorRequirements;
        }

        @XmlElementWrapper(name = "cost")
        @XmlElement(name = "card-requirement")
        public void setColorRequirements(List<XmlCardRequirement> colorRequirements) {
            ColorRequirements = colorRequirements;
        }

        public int getVictoryPoints() {
            return victoryPoints;
        }

        @XmlElement(name = "victory-points")
        public void setVictoryPoints(int victoryPoints) {
            this.victoryPoints = victoryPoints;
        }


    }

    @XmlRootElement(name = "token")
    static class XmlActionToken{
        private String type;
        private String color;

        public String getType() {
            return type;
        }

        @XmlElement(name = "name")
        public void setType(String type) {
            this.type = type;
        }

        public String getColor() {
            return color;
        }

        @XmlElement(name = "color", nillable = true)
        public void setColor(String color) {
            this.color = color;
        }

    }
}
