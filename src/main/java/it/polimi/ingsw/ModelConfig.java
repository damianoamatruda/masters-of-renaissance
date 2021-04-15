package it.polimi.ingsw;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/** Class used for file parsing purposes. */
@XmlRootElement(name = "config")
public class ModelConfig {
    /** Maximum number of players for each Game. */
    @XmlElement(name = "max-players")
    private int maxPlayers;

    /** The maximum amount of faith points a player can have. */
    @XmlElement(name = "max-faith")
    private int maxFaith;

    /** The number of development cards a player can have before triggering the end of a game. */
    @XmlElement(name = "max-development-cards")
    private int maxDevCards;

    /** The maximum level a development card can have. */
    @XmlElement(name = "max-level")
    private int maxLevel;

    /** The number of different card colors. */
    @XmlElement(name = "num-colors")
    private int numColors;

    /** The number of columns of the market grid. */
    @XmlElement(name = "market-columns")
    private int marketColumns;

    @XmlElement(name = "market-replaceable-resource-type")
    private String marketReplaceableResType;

    /** Initial number of leader cards per player. */
    @XmlElement(name = "num-leaders")
    private int numLeaders;

    /** Maximum size of a Warehouse shelf. */
    @XmlElement(name = "max-shelf-size")
    private int maxShelfSize;

    /** Number of development card production slots per player. */
    @XmlElement(name = "slots-count")
    private int slotsCount;

    /** All the generated development card templates. */
    @XmlElementWrapper(name = "development-cards")
    @XmlElement(name = "Card")
    private List<XmlDevCard> devCards;

    /** All the generated leader card templates */
    @XmlElementWrapper(name = "leader-cards")
    @XmlElement(name = "Card")
    private List<XmlLeaderCard> leaderCards;

    /** The Market template. */
    @XmlElementWrapper(name = "market")
    @XmlElement(name = "resource-entry")
    private List<XmlResourceMapEntry> market;

    /** The Faith Track template. */
    @XmlElement(name = "Faith-Track")
    private XmlFaithTrack faithTrack;

    /** All the action tokens templates. */
    @XmlElementWrapper(name = "action-tokens")
    @XmlElement(name = "token")
    private List<XmlActionToken> tokens;

    /** The player's base production "recipe". */
    @XmlElement(name = "base-production")
    private XmlProduction baseProduction;

    /** The initial boost in number of resources each player can have. */
    @XmlElementWrapper(name = "initial-resources")
    @XmlElement(name = "player")
    private List<XmlBoost> initialResources;

    /** All the resource types which can be used in the Game. */
    @XmlElementWrapper(name = "resource-types")
    @XmlElement(name = "type")
    private List<XmlResource> resourceTypes;

    /** All the colors a development card can have. */
    @XmlElementWrapper(name = "card-colors")
    @XmlElement(name = "color")
    private List<String> cardColors;

    public List<XmlBoost> getInitialResources() {
        return initialResources;
    }

    public List<XmlResourceMapEntry> getMarket() {
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

    public String getMarketReplaceableResType() {
        return marketReplaceableResType;
    }

    public List<XmlDevCard> getDevCards() {
        return devCards;
    }

    public int getMaxPlayers() {
        return maxPlayers;
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

    /** Parser class representing a resource entry - type, amount. */
    @XmlRootElement(name = "resource-entry")
    static class XmlResourceMapEntry {
        /** The name of the resource. */
        @XmlElement(name = "resource-type")
        private String resourceType;

        /** The corresponding quantity. */
        @XmlElement(name = "amount")
        private int amount;

        public String getResourceType() {
            return resourceType;
        }

        public int getAmount() {
            return amount;
        }

    }

    /** Parser class representing a development card. */
    @XmlRootElement(name = "Card")
    static class XmlDevCard {
        /** The color of the card. */
        @XmlElement(name = "color")
        private String color;

        /** The level of the card. */
        @XmlElement(name = "level")
        private int level;

        /** The requirements for purchase. */
        @XmlElementWrapper(name = "cost")
        @XmlElement(name = "resource-entry")
        private List<XmlResourceMapEntry> cost;

        /** The associated production. */
        @XmlElement(name = "production")
        private XmlProduction production;

        /** The victory points given at the end. */
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

        public List<XmlResourceMapEntry> getCost() {
            return cost;
        }

        public XmlProduction getProduction() {
            return production;
        }

    }

    /** Parser class representing a production recipe. */
    @XmlRootElement(name = "production")
    static class XmlProduction {
        /** The input of the production. */
        @XmlElementWrapper(name = "input", nillable = true)
        @XmlElement(name = "resource-entry")
        private List<XmlResourceMapEntry> input;

        /** The output of the production. */
        @XmlElementWrapper(name = "output", nillable = true)
        @XmlElement(name = "resource-entry")
        private List<XmlResourceMapEntry> output;

        /** Number of choosable resources in input. */
        @XmlElement(name = "input-blanks")
        private int inputBlanks;

        /** Number of choosable resources in output. */
        @XmlElement(name = "output-blanks")
        private int outputBlanks;

        /** The types of resources that are forbidden as choosable input. */
        @XmlElementWrapper(name = "input-blanks-exclusions", nillable = true)
        @XmlElement(name = "resource-type")
        private List<String> inputBlanksExclusions;

        /** The types of resources that are forbidden as choosable output. */
        @XmlElementWrapper(name = "output-blanks-exclusions", nillable = true)
        @XmlElement(name = "resource-type")
        private List<String> outputBlanksExclusions;

        /** Says if the resource can be discarded as output. */
        @XmlElement(name = "discardable-output", nillable = true)
        private boolean discardableOutput;

        public List<XmlResourceMapEntry> getInput() {
            return input;
        }

        public List<XmlResourceMapEntry> getOutput() {
            return output;
        }

        public int getInputBlanks() {
            return inputBlanks;
        }

        public int getOutputBlanks() {
            return outputBlanks;
        }

        public List<String> getInputBlanksExclusions() {
            return inputBlanksExclusions;
        }

        public List<String> getOutputBlanksExclusions() {
            return outputBlanksExclusions;
        }

        public boolean hasDiscardableOutput() {
            return discardableOutput;
        }

    }

    /** Parser class representing the faith track. */
    @XmlRootElement(name = "Faith-Track")
    static class XmlFaithTrack {
        /** List of all the Vatican sections. */
        @XmlElementWrapper(name = "vatican-sections")
        @XmlElement(name = "section")
        private List<XmlVaticanSection> sections;

        /** List of all the yellow tiles. */
        @XmlElementWrapper(name = "yellow-tiles")
        @XmlElement(name = "tile")
        private List<XmlYellowTile> tiles;

        public List<XmlVaticanSection> getSections() {
            return sections;
        }

        public List<XmlYellowTile> getTiles() {
            return tiles;
        }

        @XmlRootElement(name = "section")
        static class XmlVaticanSection {
            /** The first tile of the section. */
            @XmlElement(name = "section-begin")
            private int beginning;

            /** The first tile of the section (i.e. the Vatican report tile). */
            @XmlElement(name = "section-end")
            private int end;

            /** The victory points that can be earned in this section. */
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
        static class XmlYellowTile {
            /** Progressive number of the tile. */
            @XmlElement(name = "number")
            private int tileNumber;

            /** Bonus points given at the end, if reached. */
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

    /** Parser class representing a requirement of development cards. */
    @XmlRootElement(name = "card-requirement")
    static class XmlCardRequirement {
        /** The color required. */
        @XmlElement(name = "card-color")
        private String color;
        /** The level required. */
        @XmlElement(name = "card-level")
        private int level;
        /** Amount of cards of the same specs required. */
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

    /** Parser class representing a leader card. */
    @XmlRootElement(name = "Card")
    static class XmlLeaderCard {
        /** Type of leader card. */
        @XmlElement(name = "type")
        private String type;

        /** Possible discount for a resource. */
        @XmlElement(name = "discount", nillable = true)
        private int discount;

        /** Slots of possible extra storage for a resource */
        @XmlElement(name = "shelf-size", nillable = true)
        private int shelfSize;

        /** Associated resource. */
        @XmlElement(name = "resource")
        private String resource;

        /** Requirements in resources possessed. */
        @XmlElementWrapper(name = "cost")
        @XmlElement(name = "resource-entry", nillable = true)
        private List<XmlResourceMapEntry> resourceRequirements;

        /** Requirements in development cards possessed. */
        @XmlElementWrapper(name = "cost")
        @XmlElement(name = "card-requirement", nillable = true)
        private List<XmlCardRequirement> ColorRequirements;

        /** The associated production, if existent. */
        @XmlElement(name = "production", nillable = true)
        private XmlProduction production;

        /** The victory points given at the end, if activated. */
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

        public List<XmlResourceMapEntry> getResourceRequirements() {
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

    /** Parser class representing an action token. */
    @XmlRootElement(name = "token")
    static class XmlActionToken {
        /** Type of token. */
        @XmlElement(name = "name")
        private String type;

        /** Associated color (if there is any). */
        @XmlElement(name = "color", nillable = true)
        private String color;

        public String getType() {
            return type;
        }

        public String getColor() {
            return color;
        }

    }

    /** Parser class representing the player's initial resources bonus. */
    @XmlRootElement(name = "player")
    static class XmlBoost {
        /** Number of player in order to start. */
        @XmlElement(name = "order")
        private int order;

        /** Number of choosable resources obtained at the beginning. */
        @XmlElement(name = "num-storable")
        private int numResources;

        /** Starting faith points. */
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

    /** Parser class representing a resource type. */
    @XmlRootElement(name = "type")
    static class XmlResource {
        /** Name of the resource. */
        @XmlElement(name = "name")
        private String name;

        /** If this resource can be physically put in a container. */
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