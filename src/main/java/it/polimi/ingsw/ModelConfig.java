package it.polimi.ingsw;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "config")
public class ModelConfig {
    @XmlElement(name = "max-players")
    private int maxPlayers;
    @XmlElement(name = "num-leader-cards")
    private int numOfLeaderCards;
    @XmlElement(name = "max-faith")
    private int maxFaith;
    @XmlElement(name = "max-development-cards")
    private int maxDevCards;
    @XmlElement(name = "max-level")
    private int maxLevel;
    @XmlElement(name = "num-colors")
    private int numColors;
    @XmlElement(name="market-columns")
    private int marketColumns;
    @XmlElement(name="num-leaders")
    private int numLeaders;
    @XmlElement(name = "max-shelf-size")
    private int maxShelfSize;
    @XmlElement(name = "slots-count")
    private int slotsCount;
    @XmlElementWrapper(name="development-cards")
    @XmlElement(name="Card")
    private List<XmlDevCard> devCards;
    @XmlElementWrapper(name = "leader-cards")
    @XmlElement(name = "Card")
    private List<XmlLeaderCard> leaderCards;
    @XmlElementWrapper(name="market")
    @XmlElement(name="resource-entry")
    private List<XmlResourceEntry> market;
    @XmlElement(name="Faith-Track")
    private XmlFaithTrack faithTrack;
    @XmlElementWrapper(name = "action-tokens")
    @XmlElement(name = "token")
    private List<XmlActionToken> tokens;
    @XmlElement(name = "base-production")
    private XmlProduction baseProduction;
    @XmlElementWrapper(name = "initial-resources")
    @XmlElement(name = "player")
    private List<XmlBoost> initialResources;
    @XmlElementWrapper(name = "resource-types")
    @XmlElement(name = "type")
    private List<XmlResource> resourceTypes;
    @XmlElementWrapper(name = "card-colors")
    @XmlElement(name = "color")
    private List<String> cardColors;

    public List<XmlBoost> getInitialResources() {
        return initialResources;
    }

    public List<XmlResourceEntry> getMarket() {
        return market;
    }

    public XmlFaithTrack getFaithTrack() {
        return faithTrack;
    }

    public List<XmlLeaderCard> getLeaderCards() {
        return leaderCards;
    }

    public List<XmlActionToken> getTokens() {
        return tokens;
    }

    public int getNumLeaders() {
        return numLeaders;
    }

    public int getMaxShelfSize() {
        return maxShelfSize;
    }

    public XmlProduction getBaseProduction() {
        return baseProduction;
    }

    public int getSlotsCount() {
        return slotsCount;
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

    public List<XmlResource> getResourceTypes() {
        return resourceTypes;
    }

    public List<String> getCardColors() {
        return cardColors;
    }

    @XmlRootElement(name = "resource-entry")
    static class XmlResourceEntry{
        @XmlElement(name = "resource-type")
        private String resourceType;
        @XmlElement(name = "amount")
        private int amount;

        public String getResourceType() {
            return resourceType;
        }

        public int getAmount() {
            return amount;
        }

    }

    @XmlRootElement(name = "Card")
    static class XmlDevCard{
        @XmlElement(name = "color")
        private String color;
        @XmlElement(name = "level")
        private int level;
        @XmlElementWrapper(name="cost")
        @XmlElement(name = "resource-entry")
        private List<XmlResourceEntry> cost;
        @XmlElement(name = "production")
        private XmlProduction production;
        @XmlElement(name = "victory-points")
        private int victoryPoints;

        public String getColor() {
            return color;
        }

        public int getLevel() {
            return level;
        }

        public int getVictoryPoints() {
            return victoryPoints;
        }

        public List<XmlResourceEntry> getCost() {
            return cost;
        }

        public XmlProduction getProduction() {
            return production;
        }

    }

    @XmlRootElement(name = "production")
    static class XmlProduction{
        @XmlElementWrapper(name="input", nillable = true)
        @XmlElement(name = "resource-entry")
        private List<XmlResourceEntry> input;
        @XmlElementWrapper(name="output", nillable = true)
        @XmlElement(name = "resource-entry")
        private List<XmlResourceEntry> output;
        @XmlElement(name = "input-blanks")
        private int inputBlanks;
        @XmlElement(name = "output-blanks")
        private int outputBlanks;

        public List<XmlResourceEntry> getInput() {
            return input;
        }

        public List<XmlResourceEntry> getOutput() {
            return output;
        }

        public int getInputBlanks() {
            return inputBlanks;
        }

        public int getOutputBlanks() {
            return outputBlanks;
        }

    }

    @XmlRootElement(name = "Faith-Track")
    static class XmlFaithTrack{
        @XmlElementWrapper(name="vatican-sections")
        @XmlElement(name = "section")
        private List<XmlVaticanSection> sections;
        @XmlElementWrapper(name="yellow-tiles")
        @XmlElement(name = "tile")
        private List<XmlYellowTile> tiles;

        public List<XmlVaticanSection> getSections() {
            return sections;
        }

        public List<XmlYellowTile> getTiles() {
            return tiles;
        }

        @XmlRootElement(name = "section")
        static class XmlVaticanSection{
            @XmlElement(name = "section-begin")
            private int beginning;
            @XmlElement(name = "section-end")
            private int end;
            @XmlElement(name = "section-points")
            private int points;

            public int getBeginning() {
                return beginning;
            }

            public int getEnd() {
                return end;
            }

            public int getPoints() {
                return points;
            }

        }
        @XmlRootElement(name = "tile")
        static class XmlYellowTile{
            @XmlElement(name = "number")
            private int tileNumber;
            @XmlElement(name = "points")
            private int points;

            public int getTileNumber() {
                return tileNumber;
            }

            public int getPoints() {
                return points;
            }

        }
    }

    @XmlRootElement(name = "card-requirement")
    static class XmlCardRequirement{
        @XmlElement(name = "card-color")
        private String color;
        @XmlElement(name = "card-level")
        private int level;
        @XmlElement(name = "amount")
        private int amount;

        public String getColor() {
            return color;
        }

        public int getLevel() {
            return level;
        }

        public int getAmount() {
            return amount;
        }

    }

    @XmlRootElement(name = "Card")
    static class XmlLeaderCard{
        @XmlElement(name = "type")
        private String type;
        @XmlElement(name = "discount", nillable = true)
        private int discount;
        @XmlElement(name = "shelf-size", nillable = true)
        private int shelfSize;
        @XmlElement(name = "resource")
        private String resource;
        @XmlElementWrapper(name = "cost")
        @XmlElement(name = "resource-entry", nillable = true)
        private List<XmlResourceEntry> resourceRequirements;
        @XmlElementWrapper(name = "cost")
        @XmlElement(name = "card-requirement", nillable = true)
        private List<XmlCardRequirement> ColorRequirements;
        @XmlElement(name = "production", nillable = true)
        private XmlProduction production;
        @XmlElement(name = "victory-points")
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

        public String getResource() {
            return resource;
        }

        public List<XmlResourceEntry> getResourceRequirements() {
            return resourceRequirements;
        }

        public List<XmlCardRequirement> getColorRequirements() {
            return ColorRequirements;
        }

        public int getVictoryPoints() {
            return victoryPoints;
        }

        public int getDiscount() {
            return discount;
        }

    }

    @XmlRootElement(name = "token")
    static class XmlActionToken{
        @XmlElement(name = "name")
        private String type;
        @XmlElement(name = "color", nillable = true)
        private String color;

        public String getType() {
            return type;
        }

        public String getColor() {
            return color;
        }

    }

    @XmlRootElement(name = "player")
    static class XmlBoost{
        @XmlElement(name = "order")
        private int order;
        @XmlElement(name = "num-storable")
        private int numResources;
        @XmlElement(name = "faith")
        private int faith;

        public int getOrder() {
            return order;
        }

        public int getNumResources() {
            return numResources;
        }

        public int getFaith() {
            return faith;
        }

    }

    @XmlRootElement(name = "type")
    static class XmlResource {
        @XmlElement(name = "name")
        private String name;
        @XmlElement(name = "storable")
        private boolean isStorable;

        public String getName() {
            return name;
        }

        public boolean isStorable() {
            return isStorable;
        }
    }
}